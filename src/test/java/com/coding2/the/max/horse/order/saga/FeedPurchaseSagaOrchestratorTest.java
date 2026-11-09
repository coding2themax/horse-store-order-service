package com.coding2.the.max.horse.order.saga;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpTemplate;

import com.coding2.the.max.horse.order.purchase.FeedPurchaseStatus;
import com.coding2.the.max.horse.order.purchase.FeedPurchaseStore;

import reactor.core.publisher.Mono;

class FeedPurchaseSagaOrchestratorTest {

  private AmqpTemplate amqp;
  private FeedPurchaseStore store;
  private FeedPurchaseSagaOrchestrator orchestrator;
  private FeedPurchaseMessage purchase;

  @BeforeEach
  void setUp() {
    amqp = mock(AmqpTemplate.class);
    store = mock(FeedPurchaseStore.class);
    when(store.save(any(), any())).thenReturn(Mono.empty());
    orchestrator = new FeedPurchaseSagaOrchestrator(amqp, store);
    purchase = new FeedPurchaseMessage(java.util.UUID.randomUUID(), "cust-1", "alfalfa-hay", 2, "tok_visa",
        new BigDecimal("18.50"), new BigDecimal("37.00"), null, null);
  }

  @Test
  void startAsksInventoryToReserve() {
    FeedPurchaseMessage started = orchestrator.start(purchase);

    assertThat(started).isEqualTo(purchase);
    verify(amqp).convertAndSend(SagaRoutes.EXCHANGE, SagaRoutes.INVENTORY_RESERVE, purchase);
    verifyNoInteractions(store);
  }

  @Test
  void reservedStockLeadsToPaymentWithoutSaving() {
    orchestrator.onReply(purchase, SagaRoutes.INVENTORY_RESERVED);

    verify(amqp).convertAndSend(SagaRoutes.EXCHANGE, SagaRoutes.PAYMENT_CHARGE, purchase);
    verifyNoInteractions(store);
  }

  @Test
  void completedPaymentLeadsToCommitWithoutSaving() {
    orchestrator.onReply(purchase, SagaRoutes.PAYMENT_COMPLETED);

    verify(amqp).convertAndSend(SagaRoutes.EXCHANGE, SagaRoutes.INVENTORY_COMMIT, purchase);
    verifyNoInteractions(store);
  }

  @Test
  void failedPaymentReleasesStock() {
    orchestrator.onReply(purchase, SagaRoutes.PAYMENT_FAILED);

    verify(amqp).convertAndSend(SagaRoutes.EXCHANGE, SagaRoutes.INVENTORY_RELEASE, purchase);
    verifyNoInteractions(store);
  }

  @Test
  void committedStockSavesCompletedPurchase() {
    orchestrator.onReply(purchase, SagaRoutes.INVENTORY_COMMITTED);

    verify(store).save(purchase, FeedPurchaseStatus.COMPLETED);
    verifyNoInteractions(amqp);
  }

  @Test
  void rejectedStockSavesCancelledPurchase() {
    orchestrator.onReply(purchase, SagaRoutes.INVENTORY_REJECTED);

    verify(store).save(purchase, FeedPurchaseStatus.CANCELLED);
    verifyNoInteractions(amqp);
  }

  @Test
  void releasedStockSavesCancelledPurchase() {
    orchestrator.onReply(purchase, SagaRoutes.INVENTORY_RELEASED);

    verify(store).save(purchase, FeedPurchaseStatus.CANCELLED);
    verifyNoInteractions(amqp);
  }

  @Test
  void failedSaveIsRethrownSoRabbitRedelivers() {
    when(store.save(any(), any())).thenReturn(Mono.error(new IllegalStateException("db down")));

    assertThatThrownBy(() -> orchestrator.onReply(purchase, SagaRoutes.INVENTORY_COMMITTED))
        .hasMessageContaining("db down");
  }

  @Test
  void unknownReplyIsRejected() {
    assertThatThrownBy(() -> orchestrator.onReply(purchase, "saga.unknown"))
        .isInstanceOf(IllegalArgumentException.class);
    verify(store, never()).save(any(), any());
  }
}
