package com.coding2.the.max.horse.order.purchase;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/** What a user sends to buy feed. */
public record FeedPurchaseRequest(
    @NotBlank String customerId,
    @NotBlank String feedId,
    @Min(1) @Max(1000) int quantity,
    @NotBlank String paymentToken) {
}
