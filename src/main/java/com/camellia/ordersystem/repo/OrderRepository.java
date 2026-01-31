package com.camellia.ordersystem.repo;

import com.camellia.ordersystem.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, Integer> {}
