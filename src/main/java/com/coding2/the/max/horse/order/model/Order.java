package com.coding2.the.max.horse.order.model;

import java.math.BigDecimal;

import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders", schema = "horse_order")
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
