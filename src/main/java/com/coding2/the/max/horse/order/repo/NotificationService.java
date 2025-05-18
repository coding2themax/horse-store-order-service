package com.coding2.the.max.horse.order.repo;

import com.coding2.the.max.horse.order.model.HorseStoreOrder;

/**
 * Interface for notification service
 */
public interface NotificationService {
  void sendOrderCreationNotification(HorseStoreOrder order);

  void sendOrderStatusUpdateNotification(HorseStoreOrder order);

  void sendPaymentConfirmationNotification(HorseStoreOrder order);

  void sendOrderCancellationNotification(HorseStoreOrder order);
}