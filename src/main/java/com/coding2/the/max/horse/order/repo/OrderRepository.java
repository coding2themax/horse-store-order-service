package com.coding2.the.max.horse.order.repo;

import java.util.UUID;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.coding2.the.max.horse.order.model.HorseStoreOrder;
import com.coding2.the.max.horse.order.model.OrderStatus;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Repository interface for pet orders
 */
@Repository
public interface OrderRepository extends ReactiveCrudRepository<HorseStoreOrder, UUID> {

  @Query("SELECT * FROM orders WHERE user_id = :userId")
  Flux<HorseStoreOrder> findByUserId(UUID userId);

  @Query("SELECT * FROM orders WHERE status = :status")
  Flux<HorseStoreOrder> findByStatus(OrderStatus status);

  @Query("SELECT * FROM orders WHERE order_id = :orderId")
  Mono<HorseStoreOrder> findByOrderId(UUID orderId);
}
