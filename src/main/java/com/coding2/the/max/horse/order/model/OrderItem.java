package com.coding2.the.max.horse.order.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents an item in a pet order
 */
@Builder
@Getter
@Setter
public class OrderItem {
  private String itemId;
  private String petId;
  private String petName;
  private String petType;
  private int quantity;
  private double unitPrice;

  // Constructors, getters, setters
}