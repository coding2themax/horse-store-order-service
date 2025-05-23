package com.coding2.the.max.horse.order.model;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an item in a pet order
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("order_items")
public class OrderItem {
  @Id
  private Long orderItemId;
  private UUID orderId;
  private UUID productId;
  private UUID horseId;
  private Integer quantity;
  private BigDecimal unitPrice;

  // Constructors, getters, setters
}