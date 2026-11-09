package com.coding2.the.max.horse.order.saga;

/**
 * RabbitMQ names shared by the feed purchase saga.
 *
 * <p>Commands ask a participant to do a step. Replies ({@code saga.*}) tell the
 * orchestrator how the step went. The inventory service uses the same names.
 */
public final class SagaRoutes {

  public static final String EXCHANGE = "feed.purchase.saga";
  public static final String DEAD_LETTER_EXCHANGE = "feed.purchase.saga.dlx";
  public static final String DEAD_LETTER_QUEUE = "feed-purchase.dead-letter";

  public static final String INVENTORY_COMMANDS_QUEUE = "feed-purchase.inventory.commands";
  public static final String PAYMENT_COMMANDS_QUEUE = "feed-purchase.payment.commands";
  public static final String SAGA_REPLIES_QUEUE = "feed-purchase.saga.replies";

  // Commands
  public static final String INVENTORY_RESERVE = "inventory.reserve";
  public static final String INVENTORY_COMMIT = "inventory.commit";
  public static final String INVENTORY_RELEASE = "inventory.release";
  public static final String PAYMENT_CHARGE = "payment.charge";

  // Replies
  public static final String INVENTORY_RESERVED = "saga.inventory.reserved";
  public static final String INVENTORY_REJECTED = "saga.inventory.rejected";
  public static final String INVENTORY_COMMITTED = "saga.inventory.committed";
  public static final String INVENTORY_RELEASED = "saga.inventory.released";
  public static final String PAYMENT_COMPLETED = "saga.payment.completed";
  public static final String PAYMENT_FAILED = "saga.payment.failed";

  private SagaRoutes() {
  }
}
