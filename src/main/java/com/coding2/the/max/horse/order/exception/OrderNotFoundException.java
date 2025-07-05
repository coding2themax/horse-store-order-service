package com.coding2.the.max.horse.order.exception;

/**
 * Exception thrown when an order cannot be found
 */
public class OrderNotFoundException extends Exception {
  public OrderNotFoundException(String message) {
    super(message);
  }
}
