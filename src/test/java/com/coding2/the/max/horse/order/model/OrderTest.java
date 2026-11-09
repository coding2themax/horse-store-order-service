package com.coding2.the.max.horse.order.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.openapitools.client.model.Address;

class OrderTest {

  @Test
  void holdsOrderValues() {
    Address shipping = new Address();
    Address billing = new Address();
    Order order = new Order();
    order.setId("o1");
    order.setQuantity(2);
    order.setShipDate("2026-11-10");
    order.setStatus(OrderStatus.PLACED.name());
    order.setComplete(true);
    order.setShippingAddress(shipping);
    order.setBillingAddress(billing);
    order.setTotalPrice(new BigDecimal("37.00"));

    assertThat(order.getId()).isEqualTo("o1");
    assertThat(order.getQuantity()).isEqualTo(2);
    assertThat(order.getShipDate()).isEqualTo("2026-11-10");
    assertThat(order.getStatus()).isEqualTo("PLACED");
    assertThat(order.isComplete()).isTrue();
    assertThat(order.getShippingAddress()).isSameAs(shipping);
    assertThat(order.getBillingAddress()).isSameAs(billing);
    assertThat(order.getTotalPrice()).isEqualByComparingTo("37.00");
  }

  @Test
  void statusesRoundTrip() {
    for (OrderStatus status : OrderStatus.values()) {
      assertThat(OrderStatus.valueOf(status.name())).isEqualTo(status);
    }
  }
}
