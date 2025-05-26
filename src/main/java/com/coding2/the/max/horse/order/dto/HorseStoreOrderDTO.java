package com.coding2.the.max.horse.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.coding2.the.max.horse.order.model.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a pet order in the system
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HorseStoreOrderDTO {
  private UUID orderId;
  private UUID userId;
  private LocalDateTime orderDate;
  private OrderStatus status;
  private String shippingInfo;
  private String paymentInfo;
  private BigDecimal totalAmount;
}
