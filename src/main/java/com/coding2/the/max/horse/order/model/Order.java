package com.coding2.the.max.horse.order.model;

import java.math.BigDecimal; // Add this import statement

import org.springframework.boot.autoconfigure.amqp.RabbitConnectionDetails.Address;

import lombok.Getter;
import lombok.Setter;

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
