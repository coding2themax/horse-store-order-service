package com.coding2.the.max.horse.order.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.coding2.the.max.horse.order.exception.InvalidOrderException;
import com.coding2.the.max.horse.order.exception.InvalidStatusChangeException;
import com.coding2.the.max.horse.order.exception.OrderNotFoundException;
import com.coding2.the.max.horse.order.exception.PaymentProcessingException;
import com.coding2.the.max.horse.order.model.HorseStoreOrder;
import com.coding2.the.max.horse.order.model.OrderStatus;
import com.coding2.the.max.horse.order.model.PaymentDetails;
import com.coding2.the.max.horse.order.service.HorseStoreOrderService;

import reactor.core.publisher.Mono;

@Component
public class OrderHandler {

  private final HorseStoreOrderService orderService;

  public OrderHandler(HorseStoreOrderService orderService) {
    this.orderService = orderService;
  }

  public Mono<ServerResponse> createOrder(ServerRequest request) {
    return request.bodyToMono(HorseStoreOrder.class)
        .flatMap(order -> Mono.fromCallable(() -> orderService.createOrder(order)))
        .flatMap(order -> ServerResponse.ok().bodyValue(order))
        .onErrorResume(InvalidOrderException.class, e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
  }

  public Mono<ServerResponse> getOrderById(ServerRequest request) {
    String orderId = request.pathVariable("orderId");
    return Mono.fromCallable(() -> orderService.getOrderById(orderId))
        .flatMap(order -> ServerResponse.ok().bodyValue(order))
        .onErrorResume(OrderNotFoundException.class, e -> ServerResponse.notFound().build());
  }

  public Mono<ServerResponse> updateOrder(ServerRequest request) {
    String orderId = request.pathVariable("orderId");
    return request.bodyToMono(HorseStoreOrder.class)
        .flatMap(order -> Mono.fromCallable(() -> orderService.updateOrder(orderId, order)))
        .flatMap(order -> ServerResponse.ok().bodyValue(order))
        .onErrorResume(OrderNotFoundException.class, e -> ServerResponse.notFound().build())
        .onErrorResume(InvalidOrderException.class, e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
  }

  public Mono<ServerResponse> cancelOrder(ServerRequest request) {
    String orderId = request.pathVariable("orderId");
    return Mono.fromCallable(() -> orderService.cancelOrder(orderId))
        .flatMap(success -> ServerResponse.ok().bodyValue(success))
        .onErrorResume(OrderNotFoundException.class, e -> ServerResponse.notFound().build())
        .onErrorResume(InvalidStatusChangeException.class, e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
  }

  public Mono<ServerResponse> processPayment(ServerRequest request) {
    String orderId = request.pathVariable("orderId");
    return request.bodyToMono(PaymentDetails.class)
        .flatMap(payment -> Mono.fromCallable(() -> orderService.processPayment(orderId, payment)))
        .flatMap(order -> ServerResponse.ok().bodyValue(order))
        .onErrorResume(OrderNotFoundException.class, e -> ServerResponse.notFound().build())
        .onErrorResume(PaymentProcessingException.class, e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
  }

  public Mono<ServerResponse> updateOrderStatus(ServerRequest request) {
    String orderId = request.pathVariable("orderId");
    return request.bodyToMono(OrderStatus.class)
        .flatMap(status -> Mono.fromCallable(() -> orderService.updateOrderStatus(orderId, status)))
        .flatMap(order -> ServerResponse.ok().bodyValue(order))
        .onErrorResume(OrderNotFoundException.class, e -> ServerResponse.notFound().build())
        .onErrorResume(InvalidStatusChangeException.class, e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
  }

  public Mono<ServerResponse> findOrdersByCustomer(ServerRequest request) {
    String customerId = request.pathVariable("customerId");
    return Mono.fromCallable(() -> orderService.findOrdersByCustomer(customerId))
        .flatMap(orders -> ServerResponse.ok().bodyValue(orders));
  }

  public Mono<ServerResponse> findOrdersByStatus(ServerRequest request) {
    String statusStr = request.pathVariable("status");
    OrderStatus status = OrderStatus.valueOf(statusStr.toUpperCase());
    return Mono.fromCallable(() -> orderService.findOrdersByStatus(status))
        .flatMap(orders -> ServerResponse.ok().bodyValue(orders));
  }
}