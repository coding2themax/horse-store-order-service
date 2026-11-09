package com.coding2.the.max.horse.order.saga;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Declares the exchanges and the queues this service reads from.
 *
 * <p>Messages that still fail after the listener retries go to the dead-letter
 * queue instead of being redelivered forever.
 */
@Configuration
public class SagaMessagingConfig {

  @Bean
  TopicExchange sagaExchange() {
    return new TopicExchange(SagaRoutes.EXCHANGE, true, false);
  }

  @Bean
  FanoutExchange sagaDeadLetterExchange() {
    return new FanoutExchange(SagaRoutes.DEAD_LETTER_EXCHANGE, true, false);
  }

  @Bean
  Queue sagaDeadLetterQueue() {
    return QueueBuilder.durable(SagaRoutes.DEAD_LETTER_QUEUE).build();
  }

  @Bean
  Binding sagaDeadLetterBinding() {
    return BindingBuilder.bind(sagaDeadLetterQueue()).to(sagaDeadLetterExchange());
  }

  @Bean
  Queue sagaRepliesQueue() {
    return durableWithDeadLetter(SagaRoutes.SAGA_REPLIES_QUEUE);
  }

  @Bean
  Binding sagaRepliesBinding() {
    return BindingBuilder.bind(sagaRepliesQueue()).to(sagaExchange()).with("saga.#");
  }

  @Bean
  Queue paymentCommandsQueue() {
    return durableWithDeadLetter(SagaRoutes.PAYMENT_COMMANDS_QUEUE);
  }

  @Bean
  Binding paymentCommandsBinding() {
    return BindingBuilder.bind(paymentCommandsQueue()).to(sagaExchange()).with("payment.*");
  }

  /**
   * Sends records as JSON. Listeners read the type from their method parameter
   * rather than a class-name header, so each service can keep its own copy of
   * {@link FeedPurchaseMessage}.
   */
  @Bean
  MessageConverter sagaMessageConverter(ObjectMapper objectMapper) {
    Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);
    converter.setAlwaysConvertToInferredType(true);
    return converter;
  }

  private static Queue durableWithDeadLetter(String name) {
    return QueueBuilder.durable(name)
        .deadLetterExchange(SagaRoutes.DEAD_LETTER_EXCHANGE)
        .build();
  }
}
