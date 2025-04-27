package com.coding2.the.max.horse.order.repo;

import com.coding2.the.max.horse.order.model.PetOrder;

/**
 * Interface for notification service
 */
public interface NotificationService {
  void sendOrderCreationNotification(PetOrder order);

  void sendOrderStatusUpdateNotification(PetOrder order);

  void sendPaymentConfirmationNotification(PetOrder order);

  void sendOrderCancellationNotification(PetOrder order);
}