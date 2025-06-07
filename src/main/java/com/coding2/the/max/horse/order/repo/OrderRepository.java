package com.coding2.the.max.horse.order.repo;

import java.util.UUID;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.coding2.the.max.horse.order.model.Order;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Repository interface for horse orders
 */
@Repository
public interface OrderRepository extends ReactiveCrudRepository<Order, UUID> {

  @Query("SELECT * FROM horse_order.orders WHERE user_id = :userId")
  Flux<Order> findByUserId(UUID userId);

  @Query("SELECT * FROM horse_order.orders WHERE status = :status")
  Flux<Order> findByStatus(String status);

  @Query("SELECT * FROM horse_order.orders WHERE id = :orderId")
  Mono<Order> findByOrderId(String orderId);
}
