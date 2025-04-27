package com.coding2.the.max.horse.order.exception;

/**
 * Exception thrown when order data is invalid
 */
public class InvalidOrderException extends Exception {
  public InvalidOrderException(String message) {
    super(message);
  }
}