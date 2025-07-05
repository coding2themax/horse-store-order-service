package com.coding2.the.max.horse.order.model;

import java.util.Date;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents payment details for processing an order
 */
@Builder
@Getter
@Setter
public class PaymentDetails {
  private String paymentMethodId;
  private String transactionId;
  private double amount;
  private String currency;
  private Date paymentDate;

  // Constructors, getters, setters
}
