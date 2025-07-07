package com.coding2.the.max.horse.order.repo;

import java.util.UUID;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.coding2.the.max.horse.order.model.Order;

import reactor.core.publisher.Flux;

@Repository
public interface OrderRepository extends ReactiveCrudRepository<Order, UUID> {
  @Query("SELECT * FROM horse_order.orders")
  Flux<Order> getAllOrFluxders();
}
