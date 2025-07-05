package com.coding2.the.max.horse.order.repo;

import com.coding2.the.max.horse.order.dto.HorseStoreOrderDTO;

/**
 * Interface for inventory management service
 */
public interface InventoryService {
  boolean isAvailable(String petId, int quantity);

  void reserveInventory(HorseStoreOrderDTO order);

  void returnInventoryForOrder(HorseStoreOrderDTO order);
}