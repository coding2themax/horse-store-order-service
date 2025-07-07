package com.coding2.the.max.horse.order.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.dialect.PostgresDialect;

import com.coding2.the.max.horse.order.converters.OrderReadConverter;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class R2DBCConfig {

  @Bean
  public R2dbcCustomConversions r2dbcCustomConversions(ObjectMapper objectMapper) {
    List<Object> converters = new ArrayList<>();
    converters.add(new OrderReadConverter(objectMapper));
    return R2dbcCustomConversions.of(PostgresDialect.INSTANCE, converters);
  }
}
