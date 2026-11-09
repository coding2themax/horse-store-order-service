package com.coding2.the.max.horse.order.purchase;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.r2dbc.core.DatabaseClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.coding2.the.max.horse.order.saga.FeedPurchaseMessage;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import reactor.test.StepVerifier;

/** Saves and reads purchases in a real Postgres started by Testcontainers. */
@Testcontainers
class FeedPurchaseStoreTest {

  @Container
  static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");

  private static FeedPurchaseStore store;

  @BeforeAll
  static void createSchema() throws Exception {
    ConnectionFactory connections = ConnectionFactories.get(String.format(
        "r2dbc:postgresql://%s:%s@%s:%d/%s", POSTGRES.getUsername(), POSTGRES.getPassword(), POSTGRES.getHost(),
        POSTGRES.getFirstMappedPort(), POSTGRES.getDatabaseName()));
    DatabaseClient db = DatabaseClient.create(connections);
    String schema = new ClassPathResource("schema.sql").getContentAsString(StandardCharsets.UTF_8);
    for (String statement : schema.split(";")) {
      if (!statement.isBlank()) {
        db.sql(statement).then().block();
      }
    }
    store = new FeedPurchaseStore(db);
  }

  @Test
  void savesAndReadsCompletedPurchase() {
    FeedPurchaseMessage message = new FeedPurchaseMessage(UUID.randomUUID(), "cust-1", "alfalfa-hay", 5, "tok",
        new BigDecimal("18.50"), new BigDecimal("92.50"), "pay_1", null);

    store.save(message, FeedPurchaseStatus.COMPLETED).block();

    StepVerifier.create(store.findBySagaId(message.sagaId()))
        .assertNext(purchase -> {
          assertThat(purchase.sagaId()).isEqualTo(message.sagaId());
          assertThat(purchase.customerId()).isEqualTo("cust-1");
          assertThat(purchase.feedId()).isEqualTo("alfalfa-hay");
          assertThat(purchase.quantity()).isEqualTo(5);
          assertThat(purchase.unitPrice()).isEqualByComparingTo("18.50");
          assertThat(purchase.totalPrice()).isEqualByComparingTo("92.50");
          assertThat(purchase.paymentId()).isEqualTo("pay_1");
          assertThat(purchase.status()).isEqualTo(FeedPurchaseStatus.COMPLETED);
          assertThat(purchase.failureReason()).isNull();
          assertThat(purchase.completedAt()).isNotNull();
        })
        .verifyComplete();
  }

  @Test
  void savesCancelledPurchaseWithoutPrice() {
    FeedPurchaseMessage message = new FeedPurchaseMessage(UUID.randomUUID(), "cust-2", "senior-feed", 999, "tok",
        null, null, null, "Only 80 of senior-feed in stock");

    store.save(message, FeedPurchaseStatus.CANCELLED).block();

    StepVerifier.create(store.findBySagaId(message.sagaId()))
        .assertNext(purchase -> {
          assertThat(purchase.status()).isEqualTo(FeedPurchaseStatus.CANCELLED);
          assertThat(purchase.unitPrice()).isNull();
          assertThat(purchase.paymentId()).isNull();
          assertThat(purchase.failureReason()).isEqualTo("Only 80 of senior-feed in stock");
        })
        .verifyComplete();
  }

  @Test
  void savingSameSagaTwiceKeepsFirstRow() {
    FeedPurchaseMessage message = new FeedPurchaseMessage(UUID.randomUUID(), "cust-3", "alfalfa-hay", 1, "tok",
        new BigDecimal("18.50"), new BigDecimal("18.50"), "pay_3", null);

    store.save(message, FeedPurchaseStatus.COMPLETED).block();
    store.save(message.withFailure("late duplicate"), FeedPurchaseStatus.CANCELLED).block();

    StepVerifier.create(store.findBySagaId(message.sagaId()))
        .assertNext(purchase -> assertThat(purchase.status()).isEqualTo(FeedPurchaseStatus.COMPLETED))
        .verifyComplete();
  }

  @Test
  void unknownSagaIsEmpty() {
    StepVerifier.create(store.findBySagaId(UUID.randomUUID())).verifyComplete();
  }
}
