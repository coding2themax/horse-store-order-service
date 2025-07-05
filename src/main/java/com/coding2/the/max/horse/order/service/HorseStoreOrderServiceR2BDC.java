package com.coding2.the.max.horse.order.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.coding2.the.max.horse.order.exception.InvalidOrderException;
import com.coding2.the.max.horse.order.exception.InvalidStatusChangeException;
import com.coding2.the.max.horse.order.exception.OrderNotFoundException;
import com.coding2.the.max.horse.order.exception.PaymentProcessingException;
import com.coding2.the.max.horse.order.model.Order;
import com.coding2.the.max.horse.order.model.OrderStatus;
import com.coding2.the.max.horse.order.model.PaymentDetails;
import com.coding2.the.max.horse.order.repo.OrderRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class HorseStoreOrderServiceR2BDC implements HorseStoreOrderService {

  private final OrderRepository orderRepository;

  @Override
  public Mono<Order> createOrder(Order horseOrder) {
    return Mono.just(horseOrder).map(order -> {
      order.setStatus(OrderStatus.PENDING.toString());
      return order;
    }).flatMap(orderRepository::save)
        .onErrorMap(e -> new InvalidOrderException("Failed to create order: " + e.getMessage()));
  }

  @Override
  public Mono<Order> getOrderById(String orderId) {
    return orderRepository.findByOrderId(UUID.fromString(orderId))
        .switchIfEmpty(Mono.error(new OrderNotFoundException("Order not found with id: " + orderId)));
  }

  @Override
  public Mono<Order> updateOrder(String orderId, Order horseOrder) {
    return orderRepository.findByOrderId(UUID.fromString(orderId))
        .switchIfEmpty(Mono.error(new OrderNotFoundException("Order not found with id: " + orderId)))
        .flatMap(existingOrder -> {
          horseOrder.setOrderId(existingOrder.getOrderId());
          return orderRepository.save(horseOrder);
        }).onErrorMap(e -> {
          if (e instanceof OrderNotFoundException) {
            return e;
          }
          return new InvalidOrderException("Failed to update order: " + e.getMessage());
        });
  }

  @Override
  public Mono<Boolean> cancelOrder(String orderId) {
    return orderRepository.findByOrderId(UUID.fromString(orderId))
        .switchIfEmpty(Mono.error(new OrderNotFoundException("Order not found with id: " + orderId))).flatMap(order -> {
          if (order.getStatus().equals(OrderStatus.COMPLETED.toString())) {
            return Mono.error(new InvalidStatusChangeException("Cannot cancel a completed order"));
          }
          order.setStatus(OrderStatus.CANCELLED.toString());
          return orderRepository.save(order).thenReturn(true);
        }).onErrorMap(e -> {
          if (e instanceof OrderNotFoundException || e instanceof InvalidStatusChangeException) {
            return e;
          }
          return new InvalidStatusChangeException("Failed to cancel order: " + e.getMessage());
        });
  }

  @Override
  public Mono<Order> processPayment(String orderId, PaymentDetails paymentDetails) {
    return orderRepository.findByOrderId(UUID.fromString(orderId))
        .switchIfEmpty(Mono.error(new OrderNotFoundException("Order not found with id: " + orderId))).flatMap(order -> {
          if (!order.getStatus().equals(OrderStatus.PENDING.toString())) {
            return Mono.error(new InvalidStatusChangeException("Can only process payment for pending orders"));
          }
          order.setStatus(OrderStatus.PROCESSING.toString());
          return orderRepository.save(order);
        }).onErrorMap(e -> {
          if (e instanceof OrderNotFoundException || e instanceof InvalidStatusChangeException) {
            return e;
          }
          return new PaymentProcessingException("Failed to process payment: " + e.getMessage());
        });
  }

  @Override
  public Mono<Order> updateOrderStatus(String orderId, OrderStatus status) {
    return orderRepository.findByOrderId(UUID.fromString(orderId))
        .switchIfEmpty(Mono.error(new OrderNotFoundException("Order not found with id: " + orderId))).flatMap(order -> {
          if (!isValidStatusTransition(OrderStatus.valueOf(order.getStatus()), status)) {
            return Mono.error(new InvalidStatusChangeException(
                "Invalid status transition from " + order.getStatus() + " to " + status));
          }
          order.setStatus(status.toString());
          return orderRepository.save(order);
        }).onErrorMap(e -> {
          if (e instanceof OrderNotFoundException || e instanceof InvalidStatusChangeException) {
            return e;
          }
          return new InvalidStatusChangeException("Failed to update order status: " + e.getMessage());
        });
  }

  @Override
  public Flux<Order> findOrdersByCustomer(String customerId) {
    return orderRepository.findByUserId(UUID.fromString(customerId));
  }

  @Override
  public Flux<Order> findOrdersByStatus(OrderStatus status) {
    return orderRepository.findByStatus(status.toString());
  }

  private boolean isValidStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
    if (currentStatus == null || newStatus == null) {
      return false;
    }

    switch (currentStatus) {
    case PENDING:
      return newStatus == OrderStatus.PROCESSING || newStatus == OrderStatus.CANCELLED;
    case PROCESSING:
      return newStatus == OrderStatus.COMPLETED || newStatus == OrderStatus.FAILED;
    case COMPLETED:
    case CANCELLED:
    case FAILED:
      return false;
    default:
      return false;
    }
  }

  @Override
  public Flux<Order> getAllOrders() {
    return orderRepository.getAllOrFluxders()
        .switchIfEmpty(Flux.error(new OrderNotFoundException("No orders found in the system.")));
  }
}