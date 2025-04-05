package com.coding2.the.max.horse.order.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MailingAddress {
  private String streetAddress;
  private String city;
  private String state; // Use 2-letter state abbreviations (e.g., CA, NY)
  private String zipCode; // 5-digit or 9-digit ZIP code
  private String country = "USA"; // Default to USA

  @Override
  public String toString() {
    return streetAddress + ", " + city + ", " + state + " " + zipCode + ", " + country;
  }
}