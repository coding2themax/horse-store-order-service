package com.coding2.the.max.horse.order.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.coding2.the.max.horse.order.handler.OrderHandler;

/**
 * Router configuration for Horse Store Order Service endpoints. Defines all
 * REST endpoints for order management operations.
 */
@Configuration
public class OrderRouter {

  private static final String BASE_PATH = "/api/orders";
  private static final String ORDER_ID_PATH = "/{orderId}";
  private static final String CUSTOMER_PATH = "/customer/{customerId}";
  private static final String STATUS_PATH = "/status/{status}";

  @Bean
  public RouterFunction<ServerResponse> orderRoutes(OrderHandler orderHandler) {
    return RouterFunctions.route()
        // Order CRUD operations
        .POST(BASE_PATH, orderHandler::createOrder).GET(BASE_PATH + ORDER_ID_PATH, orderHandler::getOrderById)
        .PUT(BASE_PATH + ORDER_ID_PATH, orderHandler::updateOrder)
        .DELETE(BASE_PATH + ORDER_ID_PATH, orderHandler::cancelOrder)

        // Order status and payment operations
        .PATCH(BASE_PATH + ORDER_ID_PATH + "/status", orderHandler::updateOrderStatus)
        .POST(BASE_PATH + ORDER_ID_PATH + "/payment", orderHandler::processPayment)

        // Order search operations
        .GET(BASE_PATH + CUSTOMER_PATH, orderHandler::findOrdersByCustomer)
        .GET(BASE_PATH + "/all", orderHandler::getAllOrders)
        .GET(BASE_PATH + STATUS_PATH, orderHandler::findOrdersByStatus).build();
  }
}