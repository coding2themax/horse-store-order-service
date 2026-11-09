package com.coding2.the.max.horse.order.purchase;

import java.util.UUID;

/** Returned when a purchase is accepted and the saga has started. */
public record FeedPurchaseAccepted(UUID sagaId, String status, String statusUrl) {
}
