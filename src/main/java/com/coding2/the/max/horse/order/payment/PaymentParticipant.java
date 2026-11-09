package com.coding2.the.max.horse.order.payment;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import com.coding2.the.max.horse.order.saga.FeedPurchaseMessage;
import com.coding2.the.max.horse.order.saga.SagaRoutes;

/**
 * The payment step of the saga.
 *
 * <p>This is a stand-in for a real payment gateway. It declines a charge when
 * the token starts with {@code tok_declined} or the total is above
 * {@code payment.max-amount}. It talks to the orchestrator only through
 * RabbitMQ, so it can move to its own service later without other changes.
 */
@Component
@EnableConfigurationProperties(PaymentProperties.class)
public class PaymentParticipant {

  static final String DECLINED_TOKEN_PREFIX = "tok_declined";

  private static final Logger log = LoggerFactory.getLogger(PaymentParticipant.class);

  private final AmqpTemplate amqp;
  private final PaymentProperties properties;
  // Remembers charges so a redelivered command doesn't charge twice.
  private final Map<UUID, String> paymentsBySaga = new ConcurrentHashMap<>();

  public PaymentParticipant(AmqpTemplate amqp, PaymentProperties properties) {
    this.amqp = amqp;
    this.properties = properties;
  }

  @RabbitListener(queues = SagaRoutes.PAYMENT_COMMANDS_QUEUE)
  public void onCharge(FeedPurchaseMessage command) {
    String declineReason = declineReason(command);
    if (declineReason != null) {
      log.info("Saga {} payment declined: {}", command.sagaId(), declineReason);
      amqp.convertAndSend(SagaRoutes.EXCHANGE, SagaRoutes.PAYMENT_FAILED, command.withFailure(declineReason));
      return;
    }
    String paymentId = paymentsBySaga.computeIfAbsent(command.sagaId(), id -> "pay_" + UUID.randomUUID());
    log.info("Saga {} charged {} as {}", command.sagaId(), command.totalPrice(), paymentId);
    amqp.convertAndSend(SagaRoutes.EXCHANGE, SagaRoutes.PAYMENT_COMPLETED, command.withPaymentId(paymentId));
  }

  private String declineReason(FeedPurchaseMessage command) {
    if (command.totalPrice() == null) {
      return "No price was set for the purchase";
    }
    if (command.paymentToken() != null && command.paymentToken().startsWith(DECLINED_TOKEN_PREFIX)) {
      return "Card declined";
    }
    if (command.totalPrice().compareTo(properties.maxAmount()) > 0) {
      return "Amount " + command.totalPrice() + " is over the limit of " + properties.maxAmount();
    }
    return null;
  }
}
