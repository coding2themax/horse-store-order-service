package com.coding2.the.max.horse.order.model;

import java.math.BigDecimal; // Add this import statement


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Order {
  private String id;
  private int quantity;
  private String shipDate;
  private String status;
  private boolean complete;
  private MailingAddress shippingAddress;
  private MailingAddress billingAddress;
  private BigDecimal totalPrice;
}
