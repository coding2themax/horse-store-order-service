package com.coding2.the.max.horse.order.handler;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.coding2.the.max.horse.order.service.HorseStoreOrderService;

import reactor.core.publisher.Mono;

@Component
public class OrderHandler {
  private final HorseStoreOrderService orderService;

  public OrderHandler(HorseStoreOrderService orderService) {
    this.orderService = orderService;
  }

  public Mono<ServerResponse> getAllOrders(ServerRequest request) {
    return orderService.getAllOrders().collectList()
        .flatMap(orders -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(orders));
  }
}
