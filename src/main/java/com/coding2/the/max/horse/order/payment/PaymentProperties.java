package com.coding2.the.max.horse.order.payment;

import java.math.BigDecimal;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Settings for the stand-in payment step.
 *
 * @param maxAmount charges above this are declined
 */
@ConfigurationProperties(prefix = "payment")
public record PaymentProperties(BigDecimal maxAmount) {

  public PaymentProperties {
    if (maxAmount == null) {
      maxAmount = new BigDecimal("5000.00");
    }
  }
}
