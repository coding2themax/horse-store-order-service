package com.coding2.the.max.horse.order.exception;

/**
 * Exception thrown when an invalid status change is attempted
 */
public class InvalidStatusChangeException extends Exception {
  public InvalidStatusChangeException(String message) {
    super(message);
  }
}