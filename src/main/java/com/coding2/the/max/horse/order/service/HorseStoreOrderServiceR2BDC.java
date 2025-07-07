package com.coding2.the.max.horse.order.service;

import org.springframework.stereotype.Service;

import com.coding2.the.max.horse.order.model.Order;
import com.coding2.the.max.horse.order.repo.OrderRepository;

import reactor.core.publisher.Flux;

@Service
public class HorseStoreOrderServiceR2BDC implements HorseStoreOrderService {

  // Inject the repository
  private final OrderRepository orderRepository;

  public HorseStoreOrderServiceR2BDC(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }

  @Override
  public Flux<Order> getAllOrders() {
    return orderRepository.getAllOrFluxders();
  }

}
