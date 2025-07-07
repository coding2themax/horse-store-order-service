package com.coding2.the.max.horse.order.converters;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

import com.coding2.the.max.horse.order.model.Order;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.r2dbc.postgresql.codec.Json;
import io.r2dbc.spi.Row;

@Component
@ReadingConverter
public class OrderReadConverter implements Converter<Row, Order> {

  private final ObjectMapper objectMapper;
  private static final Logger log = LoggerFactory.getLogger(OrderReadConverter.class);

  public OrderReadConverter(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public Order convert(Row row) {
    try {
      UUID orderId = row.get("order_id", UUID.class);
      UUID userId = row.get("user_id", UUID.class);
      LocalDateTime orderDate = row.get("order_date", LocalDateTime.class);
      String status = row.get("status", String.class);
      BigDecimal totalAmount = row.get("total_amount", BigDecimal.class);

      // Handle shipping_info JSON
      Object shippingInfoObj = row.get("shipping_info");
      JsonNode shippingInfo = convertToJsonNode(shippingInfoObj, "shipping_info");

      // Handle payment_info JSON
      Object paymentInfoObj = row.get("payment_info");
      JsonNode paymentInfo = convertToJsonNode(paymentInfoObj, "payment_info");

      return new Order(orderId, userId, orderDate, status, shippingInfo, paymentInfo, totalAmount);
    } catch (Exception e) {
      log.error("Error converting database row to Order", e);
      throw new RuntimeException("Error converting database row to Order: " + e.getMessage(), e);
    }
  }

  private JsonNode convertToJsonNode(Object jsonObj, String fieldName) {
    try {
      if (jsonObj == null) {
        log.debug("{} is null, returning empty object", fieldName);
        return objectMapper.createObjectNode();
      }

      log.debug("{} object type: {}", fieldName, jsonObj.getClass().getName());

      if (jsonObj instanceof Json) {
        // Handle PostgreSQL specific JSON type
        String content = ((Json) jsonObj).asString();
        log.debug("{} content from Json: {}", fieldName, content);
        return objectMapper.readTree(content);
      } else if (jsonObj instanceof String) {
        log.debug("{} content from String: {}", fieldName, jsonObj);
        return objectMapper.readTree((String) jsonObj);
      } else if (jsonObj instanceof byte[]) {
        byte[] bytes = (byte[]) jsonObj;
        log.debug("{} content from byte array, length: {}", fieldName, bytes.length);
        return objectMapper.readTree(bytes);
      } else if (jsonObj instanceof JsonNode) {
        return (JsonNode) jsonObj;
      } else if (jsonObj instanceof io.r2dbc.postgresql.codec.Json) {
        // Handle PostgreSQL specific JSON type
        return objectMapper.readTree(((io.r2dbc.postgresql.codec.Json) jsonObj).asString());
      } else {
        // As a fallback, try to convert the object using toString()
        log.warn("{} using fallback conversion for type: {}", fieldName, jsonObj.getClass().getName());
        return objectMapper.readTree(jsonObj.toString());
      }
    } catch (Exception e) {
      log.error("Error converting {} to JsonNode: {}", fieldName, e.getMessage());
      // Return empty object node instead of throwing exception
      return objectMapper.createObjectNode();
    }
  }
}
