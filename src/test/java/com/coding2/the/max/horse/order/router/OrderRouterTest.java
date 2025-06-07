package com.coding2.the.max.horse.order.router;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.coding2.the.max.horse.order.handler.OrderHandler;
import com.coding2.the.max.horse.order.model.Order;
import com.coding2.the.max.horse.order.model.OrderStatus;
import com.coding2.the.max.horse.order.model.PaymentDetails;
import com.coding2.the.max.horse.order.service.HorseStoreOrderService;

@WebFluxTest(OrderRouter.class)
public class OrderRouterTest {

  @Autowired
  private WebTestClient webTestClient;

  @MockitoBean
  private OrderHandler orderHandler;

  @MockitoBean
  private HorseStoreOrderService orderService;

  @Test
  public void testCreateOrder() {
    Order order = new Order();
    order.setOrderId(UUID.fromString("123"));
    order.setStatus(OrderStatus.PENDING.toString());

    when(orderHandler.createOrder(any())).thenReturn(ServerResponse.ok().bodyValue(order));

    webTestClient.post().uri("/api/orders").contentType(MediaType.APPLICATION_JSON).bodyValue(order).exchange()
        .expectStatus().isOk().expectBody().jsonPath("$.id").isEqualTo("123").jsonPath("$.status").isEqualTo("PENDING");
  }

  @Test
  public void testGetOrderById() {
    Order order = new Order();
    order.setOrderId(UUID.fromString("123"));
    order.setStatus(OrderStatus.PENDING.toString());

    when(orderHandler.getOrderById(any())).thenReturn(ServerResponse.ok().bodyValue(order));

    webTestClient.get().uri("/api/orders/123").exchange().expectStatus().isOk().expectBody().jsonPath("$.id")
        .isEqualTo("123").jsonPath("$.status").isEqualTo("PENDING");
  }

  @Test
  public void testUpdateOrder() {
    Order order = new Order();
    order.setOrderId(UUID.fromString("123"));
    order.setStatus(OrderStatus.PROCESSING.toString());

    when(orderHandler.updateOrder(any())).thenReturn(ServerResponse.ok().bodyValue(order));

    webTestClient.put().uri("/api/orders/123").contentType(MediaType.APPLICATION_JSON).bodyValue(order).exchange()
        .expectStatus().isOk().expectBody().jsonPath("$.id").isEqualTo("123").jsonPath("$.status")
        .isEqualTo("PROCESSING");
  }

  @Test
  public void testCancelOrder() {
    when(orderHandler.cancelOrder(any())).thenReturn(ServerResponse.ok().bodyValue(true));

    webTestClient.delete().uri("/api/orders/123").exchange().expectStatus().isOk().expectBody(Boolean.class)
        .isEqualTo(true);
  }

  @Test
  public void testProcessPayment() {
    Order order = new Order();
    order.setOrderId(UUID.fromString("123"));
    order.setStatus(OrderStatus.PROCESSING.toString());
    PaymentDetails paymentDetails = PaymentDetails.builder().paymentMethodId("123").transactionId("1234567890")
        .amount(100.0).currency("USD").build();

    when(orderHandler.processPayment(any())).thenReturn(ServerResponse.ok().bodyValue(order));

    webTestClient.post().uri("/api/orders/123/payment").contentType(MediaType.APPLICATION_JSON)
        .bodyValue(paymentDetails).exchange().expectStatus().isOk().expectBody().jsonPath("$.id").isEqualTo("123")
        .jsonPath("$.status").isEqualTo("PROCESSING");
  }

  @Test
  public void testUpdateOrderStatus() {
    Order order = new Order();
    UUID orderId = UUID.randomUUID();
    order.setOrderId(orderId);
    order.setStatus(OrderStatus.PROCESSING.toString());

    when(orderHandler.updateOrderStatus(any())).thenReturn(ServerResponse.ok().bodyValue(order));

    webTestClient.patch().uri("/api/orders/123/status").contentType(MediaType.APPLICATION_JSON)
        .bodyValue(OrderStatus.PROCESSING).exchange().expectStatus().isOk().expectBody().jsonPath("$.id")
        .isEqualTo(orderId.toString()).jsonPath("$.status").isEqualTo("PROCESSING");
  }

  @Test
  public void testFindOrdersByCustomer() {
    Order order = new Order();
    when(orderHandler.findOrdersByCustomer(any())).thenReturn(ServerResponse.ok().bodyValue(order));

    webTestClient.get().uri("/api/orders/customer/123").exchange().expectStatus().isOk();
  }

  @Test
  public void testFindOrdersByStatus() {
    Order order = new Order();
    when(orderHandler.findOrdersByStatus(any())).thenReturn(ServerResponse.ok().bodyValue(order));

    webTestClient.get().uri("/api/orders/status/PENDING").exchange().expectStatus().isOk();
  }
}