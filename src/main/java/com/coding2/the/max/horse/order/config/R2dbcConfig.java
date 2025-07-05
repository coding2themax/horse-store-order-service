package com.coding2.the.max.horse.order.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.dialect.PostgresDialect;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;

import com.coding2.the.max.horse.order.model.converter.OrderReadConverter;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.r2dbc.spi.ConnectionFactory;

@Configuration
public class R2dbcConfig {

  @Bean
  public ReactiveTransactionManager transactionManager(ConnectionFactory connectionFactory) {
    return new R2dbcTransactionManager(connectionFactory);
  }

  @Bean
  public TransactionalOperator transactionalOperator(ReactiveTransactionManager transactionManager) {
    return TransactionalOperator.create(transactionManager);
  }

  @Bean
  public R2dbcCustomConversions r2dbcCustomConversions(ObjectMapper objectMapper) {
    List<Object> converters = new ArrayList<>();
    converters.add(new OrderReadConverter(objectMapper));
    return R2dbcCustomConversions.of(PostgresDialect.INSTANCE, converters);
  }
}