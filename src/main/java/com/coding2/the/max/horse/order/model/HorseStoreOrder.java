package com.coding2.the.max.horse.order.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a pet order in the system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("orders")
public class HorseStoreOrder {
  @Id
  private UUID orderId;
  private UUID userId;
  private LocalDateTime orderDate;
  private OrderStatus status;
  private String shippingInfo;
  private String paymentInfo;
  private BigDecimal totalAmount;
}
