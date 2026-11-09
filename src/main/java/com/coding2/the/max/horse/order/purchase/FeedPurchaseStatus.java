package com.coding2.the.max.horse.order.purchase;

/** How a feed purchase saga ended. */
public enum FeedPurchaseStatus {
  /** Stock was taken and payment went through. */
  COMPLETED,
  /** Stock was not available or payment failed; any reserved stock was released. */
  CANCELLED
}
