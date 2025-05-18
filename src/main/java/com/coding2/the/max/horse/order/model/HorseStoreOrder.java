package com.coding2.the.max.horse.order.model;

import java.util.Date;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a pet order in the system
 */
@Builder
@Getter
@Setter
public class HorseStoreOrder {
  private String orderId;
  private String customerId;
  private Date orderDate;
  private List<OrderItem> items;
  private OrderStatus status;
  private PaymentStatus paymentStatus;
  private Address shippingAddress;
  private double totalAmount;
  private String specialInstructions;

  // Constructors, getters, setters
}
