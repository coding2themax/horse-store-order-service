package com.coding2.the.max.horse.order.model.converter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

import com.coding2.the.max.horse.order.model.Order;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.r2dbc.spi.Row;

@Component
@ReadingConverter
public class OrderReadConverter implements Converter<Row, Order> {

  private final ObjectMapper objectMapper;

  public OrderReadConverter(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public Order convert(Row row) {
    UUID orderId = row.get("order_id", UUID.class);
    UUID userId = row.get("user_id", UUID.class);
    LocalDateTime orderDate = row.get("order_date", LocalDateTime.class);
    String status = row.get("status", String.class);
    BigDecimal totalAmount = row.get("total_amount", BigDecimal.class);

    // Convert shipping_info from database format to JsonNode
    JsonNode shippingInfo;
    try {
      String shippingInfoJson = row.get("shipping_info", String.class);
      shippingInfo = objectMapper.readTree(shippingInfoJson);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to parse shipping_info JSON", e);
    }

    // Convert payment_info from database format to JsonNode
    JsonNode paymentInfo;
    try {
      String paymentInfoJson = row.get("payment_info", String.class);
      paymentInfo = objectMapper.readTree(paymentInfoJson);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to parse payment_info JSON", e);
    }

    return new Order(orderId, userId, orderDate, status, shippingInfo, paymentInfo, totalAmount);
  }
}