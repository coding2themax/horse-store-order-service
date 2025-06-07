package com.coding2.the.max.horse.order.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.fasterxml.jackson.databind.JsonNode;

@Table(name = "orders", schema = "horse_order")
public class Order {

  @Id
  @Column("order_id")
  private UUID orderId;

  @Column("user_id")
  private UUID userId;

  @Column("order_date")
  private LocalDateTime orderDate;

  @Column("status")
  private String status;

  @Column("shipping_info")
  private JsonNode shippingInfo;

  @Column("payment_info")
  private JsonNode paymentInfo;

  @Column("total_amount")
  private BigDecimal totalAmount;

  // Default constructor
  public Order() {
  }

  // Constructor with required fields
  public Order(UUID userId, JsonNode shippingInfo, JsonNode paymentInfo, BigDecimal totalAmount) {
    this.userId = userId;
    this.shippingInfo = shippingInfo;
    this.paymentInfo = paymentInfo;
    this.totalAmount = totalAmount;
    this.orderDate = LocalDateTime.now();
    this.status = "pending";
  }

  // Full constructor
  public Order(UUID orderId, UUID userId, LocalDateTime orderDate, String status, JsonNode shippingInfo,
      JsonNode paymentInfo, BigDecimal totalAmount) {
    this.orderId = orderId;
    this.userId = userId;
    this.orderDate = orderDate;
    this.status = status;
    this.shippingInfo = shippingInfo;
    this.paymentInfo = paymentInfo;
    this.totalAmount = totalAmount;
  }

  // Getters and Setters
  public UUID getOrderId() {
    return orderId;
  }

  public void setOrderId(UUID orderId) {
    this.orderId = orderId;
  }

  public UUID getUserId() {
    return userId;
  }

  public void setUserId(UUID userId) {
    this.userId = userId;
  }

  public LocalDateTime getOrderDate() {
    return orderDate;
  }

  public void setOrderDate(LocalDateTime orderDate) {
    this.orderDate = orderDate;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public JsonNode getShippingInfo() {
    return shippingInfo;
  }

  public void setShippingInfo(JsonNode shippingInfo) {
    this.shippingInfo = shippingInfo;
  }

  public JsonNode getPaymentInfo() {
    return paymentInfo;
  }

  public void setPaymentInfo(JsonNode paymentInfo) {
    this.paymentInfo = paymentInfo;
  }

  public BigDecimal getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(BigDecimal totalAmount) {
    this.totalAmount = totalAmount;
  }

  @Override
  public String toString() {
    return "Order{" + "orderId=" + orderId + ", userId=" + userId + ", orderDate=" + orderDate + ", status='" + status
        + '\'' + ", shippingInfo=" + shippingInfo + ", paymentInfo=" + paymentInfo + ", totalAmount=" + totalAmount
        + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Order order = (Order) o;
    return orderId != null && orderId.equals(order.orderId);
  }

  @Override
  public int hashCode() {
    return orderId != null ? orderId.hashCode() : 0;
  }
}