package com.coding2.the.max.horse.order.saga;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.coding2.the.max.horse.order.purchase.FeedPurchaseStatus;
import com.coding2.the.max.horse.order.purchase.FeedPurchaseStore;

/**
 * Runs the feed purchase saga.
 *
 * <pre>
 * reserve stock ──ok──► charge payment ──ok──► commit stock ──► save COMPLETED
 *      │                     │
 *   rejected               failed
 *      │                     ▼
 *      │               release stock (compensation)
 *      ▼                     ▼
 * save CANCELLED       save CANCELLED
 * </pre>
 *
 * Nothing is written to Postgres until one of the "save" boxes is reached.
 */
@Component
public class FeedPurchaseSagaOrchestrator {

  private static final Logger log = LoggerFactory.getLogger(FeedPurchaseSagaOrchestrator.class);
  private static final Duration SAVE_TIMEOUT = Duration.ofSeconds(10);

  private final AmqpTemplate amqp;
  private final FeedPurchaseStore store;

  public FeedPurchaseSagaOrchestrator(AmqpTemplate amqp, FeedPurchaseStore store) {
    this.amqp = amqp;
    this.store = store;
  }

  /** Starts a saga by asking inventory to reserve the feed. */
  public FeedPurchaseMessage start(FeedPurchaseMessage purchase) {
    log.info("Saga {} started: {} x {} for {}", purchase.sagaId(), purchase.quantity(), purchase.feedId(),
        purchase.customerId());
    send(SagaRoutes.INVENTORY_RESERVE, purchase);
    return purchase;
  }

  @RabbitListener(queues = SagaRoutes.SAGA_REPLIES_QUEUE)
  public void onReply(FeedPurchaseMessage reply, @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String routingKey) {
    log.info("Saga {} received {}", reply.sagaId(), routingKey);
    switch (routingKey) {
      case SagaRoutes.INVENTORY_RESERVED -> send(SagaRoutes.PAYMENT_CHARGE, reply);
      case SagaRoutes.INVENTORY_REJECTED -> finish(reply, FeedPurchaseStatus.CANCELLED);
      case SagaRoutes.PAYMENT_COMPLETED -> send(SagaRoutes.INVENTORY_COMMIT, reply);
      case SagaRoutes.PAYMENT_FAILED -> send(SagaRoutes.INVENTORY_RELEASE, reply);
      case SagaRoutes.INVENTORY_COMMITTED -> finish(reply, FeedPurchaseStatus.COMPLETED);
      case SagaRoutes.INVENTORY_RELEASED -> finish(reply, FeedPurchaseStatus.CANCELLED);
      default -> throw new IllegalArgumentException("Unknown saga reply: " + routingKey);
    }
  }

  private void send(String routingKey, FeedPurchaseMessage message) {
    amqp.convertAndSend(SagaRoutes.EXCHANGE, routingKey, message);
  }

  /**
   * Saves the outcome. This blocks so the reply is only acknowledged once the
   * row is in Postgres; if the save fails, RabbitMQ redelivers the reply.
   */
  private void finish(FeedPurchaseMessage reply, FeedPurchaseStatus status) {
    store.save(reply, status).block(SAVE_TIMEOUT);
    log.info("Saga {} finished as {}", reply.sagaId(), status);
  }
}
