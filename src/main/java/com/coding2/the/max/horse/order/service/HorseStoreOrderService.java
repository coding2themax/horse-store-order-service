package com.coding2.the.max.horse.order.service;

import com.coding2.the.max.horse.order.exception.InvalidOrderException;
import com.coding2.the.max.horse.order.exception.InvalidStatusChangeException;
import com.coding2.the.max.horse.order.exception.OrderNotFoundException;
import com.coding2.the.max.horse.order.exception.PaymentProcessingException;
import com.coding2.the.max.horse.order.model.Order;
import com.coding2.the.max.horse.order.model.OrderStatus;
import com.coding2.the.max.horse.order.model.PaymentDetails;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Main interface for Horse Store Order Service. Defines reactive operations for
 * managing horse orders in a horse store system.
 */
public interface HorseStoreOrderService {

    /**
     * Create a new horse order in the system
     * 
     * @param horseOrder The order details to be created
     * @return Mono containing the created horse order with generated ID and status
     * @throws InvalidOrderException If the order data is invalid
     */
    Mono<Order> createOrder(Order horseOrder);

    /**
     * Retrieve an existing horse order by its unique identifier
     * 
     * @param orderId The unique identifier of the order
     * @return Mono containing the horse order if found
     * @throws OrderNotFoundException If no order exists with the given ID
     */
    Mono<Order> getOrderById(String orderId);

    /**
     * Update an existing horse order
     * 
     * @param orderId    The ID of the order to update
     * @param horseOrder The updated order details
     * @return Mono containing the updated horse order
     * @throws OrderNotFoundException If no order exists with the given ID
     * @throws InvalidOrderException  If the updated order data is invalid
     */
    Mono<Order> updateOrder(String orderId, Order horseOrder);

    /**
     * Cancel an existing horse order
     * 
     * @param orderId The ID of the order to cancel
     * @return Mono containing true if successfully canceled, false otherwise
     * @throws OrderNotFoundException       If no order exists with the given ID
     * @throws InvalidStatusChangeException If the order cannot be canceled due to
     *                                      its current status
     */
    Mono<Boolean> cancelOrder(String orderId);

    /**
     * Process payment for an order
     * 
     * @param orderId        The ID of the order to process payment for
     * @param paymentDetails The payment details
     * @return Mono containing the updated order with payment status
     * @throws OrderNotFoundException     If no order exists with the given ID
     * @throws PaymentProcessingException If payment processing fails
     */
    Mono<Order> processPayment(String orderId, PaymentDetails paymentDetails);

    /**
     * Update the status of an order
     * 
     * @param orderId The ID of the order to update status
     * @param status  The new status to set
     * @return Mono containing the updated order with new status
     * @throws OrderNotFoundException       If no order exists with the given ID
     * @throws InvalidStatusChangeException If the status change is not allowed
     */
    Mono<Order> updateOrderStatus(String orderId, OrderStatus status);

    /**
     * Find orders by customer ID
     * 
     * @param customerId The ID of the customer
     * @return Flux of orders for the specified customer
     */
    Flux<Order> findOrdersByCustomer(String customerId);

    /**
     * Find orders by status
     * 
     * @param status The order status to search for
     * @return Flux of orders with the specified status
     */
    Flux<Order> findOrdersByStatus(OrderStatus status);

    /**
     * Retrieve all orders in the system
     * 
     * @return Flux of all horse orders
     */
    Flux<Order> getAllOrders();
}