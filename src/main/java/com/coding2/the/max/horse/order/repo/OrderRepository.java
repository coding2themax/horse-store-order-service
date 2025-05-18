package com.coding2.the.max.horse.order.repo;

import java.util.List;
import java.util.Optional;

import com.coding2.the.max.horse.order.model.HorseStoreOrder;
import com.coding2.the.max.horse.order.model.OrderStatus;

/**
 * Repository interface for pet orders
 */
public interface OrderRepository {
  void save(HorseStoreOrder order);

  Optional<HorseStoreOrder> findById(String orderId);

  List<HorseStoreOrder> findByCustomerId(String customerId);

  List<HorseStoreOrder> findByStatus(OrderStatus status);
}
