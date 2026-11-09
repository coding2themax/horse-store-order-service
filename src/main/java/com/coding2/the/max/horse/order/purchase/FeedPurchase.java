package com.coding2.the.max.horse.order.purchase;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/** A finished feed purchase, as saved in Postgres. */
public record FeedPurchase(
    UUID sagaId,
    String customerId,
    String feedId,
    int quantity,
    BigDecimal unitPrice,
    BigDecimal totalPrice,
    String paymentId,
    FeedPurchaseStatus status,
    String failureReason,
    OffsetDateTime completedAt) {
}
