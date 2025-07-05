package com.coding2.the.max.horse.order.exception;

/**
 * Exception thrown when payment processing fails
 */
public class PaymentProcessingException extends Exception {
  public PaymentProcessingException(String message) {
    super(message);
  }
}