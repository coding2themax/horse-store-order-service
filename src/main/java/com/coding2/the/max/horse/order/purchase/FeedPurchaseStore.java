package com.coding2.the.max.horse.order.purchase;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.DatabaseClient.GenericExecuteSpec;
import org.springframework.stereotype.Repository;

import com.coding2.the.max.horse.order.saga.FeedPurchaseMessage;

import io.r2dbc.spi.Readable;
import reactor.core.publisher.Mono;

/** Saves and reads finished feed purchases in Postgres. */
@Repository
public class FeedPurchaseStore {

  private static final String INSERT = """
      INSERT INTO feed_purchase
        (saga_id, customer_id, feed_id, quantity, unit_price, total_price,
         payment_id, status, failure_reason, completed_at)
      VALUES
        (:sagaId, :customerId, :feedId, :quantity, :unitPrice, :totalPrice,
         :paymentId, :status, :failureReason, :completedAt)
      ON CONFLICT (saga_id) DO NOTHING
      """;

  private static final String SELECT_BY_ID = """
      SELECT saga_id, customer_id, feed_id, quantity, unit_price, total_price,
             payment_id, status, failure_reason, completed_at
      FROM feed_purchase
      WHERE saga_id = :sagaId
      """;

  private final DatabaseClient db;

  public FeedPurchaseStore(DatabaseClient db) {
    this.db = db;
  }

  /**
   * Saves the final state of a saga. Saving the same saga twice keeps the first
   * row, so a redelivered message is harmless.
   */
  public Mono<Void> save(FeedPurchaseMessage message, FeedPurchaseStatus status) {
    GenericExecuteSpec spec = db.sql(INSERT)
        .bind("sagaId", message.sagaId())
        .bind("customerId", message.customerId())
        .bind("feedId", message.feedId())
        .bind("quantity", message.quantity())
        .bind("status", status.name())
        .bind("completedAt", OffsetDateTime.now());
    spec = bindNullable(spec, "unitPrice", message.unitPrice(), BigDecimal.class);
    spec = bindNullable(spec, "totalPrice", message.totalPrice(), BigDecimal.class);
    spec = bindNullable(spec, "paymentId", message.paymentId(), String.class);
    spec = bindNullable(spec, "failureReason", message.failureReason(), String.class);
    return spec.then();
  }

  public Mono<FeedPurchase> findBySagaId(UUID sagaId) {
    return db.sql(SELECT_BY_ID)
        .bind("sagaId", sagaId)
        .map(FeedPurchaseStore::toPurchase)
        .one();
  }

  private static <T> GenericExecuteSpec bindNullable(GenericExecuteSpec spec, String name, T value,
      Class<T> type) {
    return value == null ? spec.bindNull(name, type) : spec.bind(name, value);
  }

  private static FeedPurchase toPurchase(Readable row) {
    return new FeedPurchase(
        row.get("saga_id", UUID.class),
        row.get("customer_id", String.class),
        row.get("feed_id", String.class),
        row.get("quantity", Integer.class),
        row.get("unit_price", BigDecimal.class),
        row.get("total_price", BigDecimal.class),
        row.get("payment_id", String.class),
        FeedPurchaseStatus.valueOf(row.get("status", String.class)),
        row.get("failure_reason", String.class),
        row.get("completed_at", OffsetDateTime.class));
  }
}
