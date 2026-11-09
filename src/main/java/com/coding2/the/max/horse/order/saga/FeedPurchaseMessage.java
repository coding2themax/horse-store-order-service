package com.coding2.the.max.horse.order.saga;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * The message passed between saga steps over RabbitMQ.
 *
 * <p>It carries the whole saga state, so no step has to save anything until
 * the saga ends. Each step returns a copy with its own result filled in.
 *
 * <p>The inventory service keeps a matching copy of this record. Keep the field
 * names in step.
 */
public record FeedPurchaseMessage(
    UUID sagaId,
    String customerId,
    String feedId,
    int quantity,
    String paymentToken,
    BigDecimal unitPrice,
    BigDecimal totalPrice,
    String paymentId,
    String failureReason) {

  public static FeedPurchaseMessage start(String customerId, String feedId, int quantity, String paymentToken) {
    return new FeedPurchaseMessage(UUID.randomUUID(), customerId, feedId, quantity, paymentToken,
        null, null, null, null);
  }

  public FeedPurchaseMessage withPaymentId(String newPaymentId) {
    return new FeedPurchaseMessage(sagaId, customerId, feedId, quantity, paymentToken,
        unitPrice, totalPrice, newPaymentId, failureReason);
  }

  public FeedPurchaseMessage withFailure(String reason) {
    return new FeedPurchaseMessage(sagaId, customerId, feedId, quantity, paymentToken,
        unitPrice, totalPrice, paymentId, reason);
  }
}
