package com.coding2.the.max.horse.order.repo;

import com.coding2.the.max.horse.order.dto.HorseStoreOrderDTO;

/**
 * Interface for notification service
 */
public interface NotificationService {
  void sendOrderCreationNotification(HorseStoreOrderDTO order);

  void sendOrderStatusUpdateNotification(HorseStoreOrderDTO order);

  void sendPaymentConfirmationNotification(HorseStoreOrderDTO order);

  void sendOrderCancellationNotification(HorseStoreOrderDTO order);
}