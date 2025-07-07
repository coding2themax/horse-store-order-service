package com.coding2.the.max.horse.order.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.coding2.the.max.horse.order.handler.OrderHandler;

@Configuration
public class OrderRouter {

  private final OrderHandler orderHandler;

  public OrderRouter(OrderHandler orderHandler) {
    this.orderHandler = orderHandler;
  }

  @Bean
  public RouterFunction<ServerResponse> routes() {
    return RouterFunctions.route().GET("/orders", orderHandler::getAllOrders).build();
  }

}
