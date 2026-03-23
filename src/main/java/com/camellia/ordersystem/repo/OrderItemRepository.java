package com.camellia.ordersystem.repo;

import com.camellia.ordersystem.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Integer> {
    boolean existsByMenuItem_ItemId(Integer itemId);
}