package com.coding2.the.max.horse.order.service;

import com.coding2.the.max.horse.order.model.Order;

import reactor.core.publisher.Flux;

public interface HorseStoreOrderService {

  /**
   * Retrieve all orders in the system
   * 
   * @return Flux of all horse orders
   */
  Flux<Order> getAllOrders();

}
