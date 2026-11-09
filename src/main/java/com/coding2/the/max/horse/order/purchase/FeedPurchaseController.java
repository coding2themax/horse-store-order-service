package com.coding2.the.max.horse.order.purchase;

import java.net.URI;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coding2.the.max.horse.order.saga.FeedPurchaseMessage;
import com.coding2.the.max.horse.order.saga.FeedPurchaseSagaOrchestrator;

import jakarta.validation.Valid;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Lets a user buy feed.
 *
 * <p>{@code POST /feed-purchases} starts the saga and returns 202 straight away.
 * {@code GET /feed-purchases/{sagaId}} returns the purchase once the saga has
 * finished, and 404 until then.
 */
@RestController
@RequestMapping("/feed-purchases")
public class FeedPurchaseController {

  private final FeedPurchaseSagaOrchestrator orchestrator;
  private final FeedPurchaseStore store;

  public FeedPurchaseController(FeedPurchaseSagaOrchestrator orchestrator, FeedPurchaseStore store) {
    this.orchestrator = orchestrator;
    this.store = store;
  }

  @PostMapping
  public Mono<ResponseEntity<FeedPurchaseAccepted>> purchaseFeed(@Valid @RequestBody FeedPurchaseRequest request) {
    FeedPurchaseMessage purchase = FeedPurchaseMessage.start(
        request.customerId(), request.feedId(), request.quantity(), request.paymentToken());
    // Publishing to RabbitMQ blocks, so keep it off the event loop.
    return Mono.fromCallable(() -> orchestrator.start(purchase))
        .subscribeOn(Schedulers.boundedElastic())
        .map(started -> {
          String statusUrl = "/feed-purchases/" + started.sagaId();
          return ResponseEntity.accepted()
              .location(URI.create(statusUrl))
              .body(new FeedPurchaseAccepted(started.sagaId(), "PENDING", statusUrl));
        });
  }

  @GetMapping("/{sagaId}")
  public Mono<ResponseEntity<FeedPurchase>> getPurchase(@PathVariable UUID sagaId) {
    return store.findBySagaId(sagaId)
        .map(ResponseEntity::ok)
        .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
  }
}
