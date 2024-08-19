package com.coding2.the.max.horse.order.model;

import org.openapitools.client.model.Address;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal; // Add this import statement

@Getter
@Setter
public class Order {
  /// generate order class
  private String id;
  private int quantity;
  private String shipDate;
  private String status;
  private boolean complete;
  private Address shippingAddress;
  private Address billingAddress;
  private BigDecimal totalPrice;
}
