package com.coding2.the.max.horse.order.repo;

import com.coding2.the.max.horse.order.exception.PaymentProcessingException;
import com.coding2.the.max.horse.order.model.PaymentDetails;

/**
 * Interface for payment processing service
 */
public interface PaymentService {
  String processPayment(PaymentDetails paymentDetails) throws PaymentProcessingException;

  boolean processRefund(String orderId) throws PaymentProcessingException;
}