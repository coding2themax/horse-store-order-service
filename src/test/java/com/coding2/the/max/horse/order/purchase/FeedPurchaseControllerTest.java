package com.coding2.the.max.horse.order.purchase;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.coding2.the.max.horse.order.saga.FeedPurchaseSagaOrchestrator;

import reactor.core.publisher.Mono;

@WebFluxTest(FeedPurchaseController.class)
class FeedPurchaseControllerTest {

  @Autowired
  private WebTestClient client;

  @MockitoBean
  private FeedPurchaseSagaOrchestrator orchestrator;

  @MockitoBean
  private FeedPurchaseStore store;

  @Test
  void postStartsSagaAndReturnsAccepted() {
    when(orchestrator.start(any())).thenAnswer(call -> call.getArgument(0));

    client.post().uri("/feed-purchases")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(new FeedPurchaseRequest("cust-1", "alfalfa-hay", 3, "tok_visa"))
        .exchange()
        .expectStatus().isAccepted()
        .expectHeader().exists("Location")
        .expectBody()
        .jsonPath("$.sagaId").isNotEmpty()
        .jsonPath("$.status").isEqualTo("PENDING");

    verify(orchestrator).start(any());
  }

  @Test
  void invalidRequestIsRejected() {
    client.post().uri("/feed-purchases")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(new FeedPurchaseRequest("", "alfalfa-hay", 0, "tok_visa"))
        .exchange()
        .expectStatus().isBadRequest();

    verify(orchestrator, never()).start(any());
  }

  @Test
  void getReturnsFinishedPurchase() {
    UUID sagaId = UUID.randomUUID();
    when(store.findBySagaId(sagaId)).thenReturn(Mono.just(new FeedPurchase(sagaId, "cust-1", "alfalfa-hay", 3,
        new BigDecimal("18.50"), new BigDecimal("55.50"), "pay_1", FeedPurchaseStatus.COMPLETED, null,
        OffsetDateTime.now())));

    client.get().uri("/feed-purchases/{id}", sagaId)
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.status").isEqualTo("COMPLETED")
        .jsonPath("$.totalPrice").isEqualTo(55.50);
  }

  @Test
  void getReturnsNotFoundWhileSagaIsRunning() {
    UUID sagaId = UUID.randomUUID();
    when(store.findBySagaId(sagaId)).thenReturn(Mono.empty());

    client.get().uri("/feed-purchases/{id}", sagaId)
        .exchange()
        .expectStatus().isNotFound();
  }
}
