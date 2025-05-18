package com.coding2.the.max.horse.order.repo;

import com.coding2.the.max.horse.order.model.HorseStoreOrder;

/**
 * Interface for inventory management service
 */
public interface InventoryService {
  boolean isAvailable(String petId, int quantity);

  void reserveInventory(HorseStoreOrder order);

  void returnInventoryForOrder(HorseStoreOrder order);
}