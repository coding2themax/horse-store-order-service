package com.coding2.the.max.horse.order.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.core.AmqpTemplate;

import com.coding2.the.max.horse.order.saga.FeedPurchaseMessage;
import com.coding2.the.max.horse.order.saga.SagaRoutes;

class PaymentParticipantTest {

  private AmqpTemplate amqp;
  private PaymentParticipant payment;

  @BeforeEach
  void setUp() {
    amqp = mock(AmqpTemplate.class);
    payment = new PaymentParticipant(amqp, new PaymentProperties(new BigDecimal("100.00")));
  }

  @Test
  void chargesAndRepliesWithPaymentId() {
    payment.onCharge(command("tok_visa", "40.00"));

    FeedPurchaseMessage reply = reply(SagaRoutes.PAYMENT_COMPLETED);
    assertThat(reply.paymentId()).startsWith("pay_");
    assertThat(reply.failureReason()).isNull();
  }

  @Test
  void redeliveredChargeReusesPaymentId() {
    FeedPurchaseMessage command = command("tok_visa", "40.00");
    payment.onCharge(command);
    payment.onCharge(command);

    ArgumentCaptor<FeedPurchaseMessage> captor = ArgumentCaptor.forClass(FeedPurchaseMessage.class);
    verify(amqp, times(2)).convertAndSend(eq(SagaRoutes.EXCHANGE), eq(SagaRoutes.PAYMENT_COMPLETED),
        captor.capture());
    assertThat(captor.getAllValues().get(0).paymentId()).isEqualTo(captor.getAllValues().get(1).paymentId());
  }

  @Test
  void declinedTokenFails() {
    payment.onCharge(command("tok_declined_card", "40.00"));

    assertThat(reply(SagaRoutes.PAYMENT_FAILED).failureReason()).isEqualTo("Card declined");
  }

  @Test
  void amountOverLimitFails() {
    payment.onCharge(command("tok_visa", "100.01"));

    assertThat(reply(SagaRoutes.PAYMENT_FAILED).failureReason()).contains("over the limit");
  }

  @Test
  void missingPriceFails() {
    payment.onCharge(command("tok_visa", null));

    assertThat(reply(SagaRoutes.PAYMENT_FAILED).failureReason()).contains("No price");
  }

  @Test
  void defaultLimitIsUsedWhenNotSet() {
    assertThat(new PaymentProperties(null).maxAmount()).isEqualByComparingTo("5000.00");
  }

  private static FeedPurchaseMessage command(String token, String total) {
    return new FeedPurchaseMessage(UUID.randomUUID(), "cust-1", "alfalfa-hay", 1, token,
        total == null ? null : new BigDecimal(total), total == null ? null : new BigDecimal(total), null, null);
  }

  private FeedPurchaseMessage reply(String routingKey) {
    ArgumentCaptor<FeedPurchaseMessage> captor = ArgumentCaptor.forClass(FeedPurchaseMessage.class);
    verify(amqp).convertAndSend(eq(SagaRoutes.EXCHANGE), eq(routingKey), captor.capture());
    return captor.getValue();
  }
}
