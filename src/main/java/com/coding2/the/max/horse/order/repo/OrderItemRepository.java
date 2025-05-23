package com.coding2.the.max.horse.order.repo;

import java.util.UUID;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.coding2.the.max.horse.order.model.OrderItem;

import reactor.core.publisher.Flux;

@Repository
public interface OrderItemRepository extends ReactiveCrudRepository<OrderItem, Long> {

  @Query("SELECT * FROM order_items WHERE order_id = :orderId")
  Flux<OrderItem> findByOrderId(UUID orderId);

  @Query("SELECT * FROM order_items WHERE product_id = :productId")
  Flux<OrderItem> findByProductId(UUID productId);

  @Query("SELECT * FROM order_items WHERE horse_id = :horseId")
  Flux<OrderItem> findByHorseId(UUID horseId);
}