package com.camellia.ordersystem.repo;

import com.camellia.ordersystem.entity.MenuItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuItemRepository extends JpaRepository<MenuItemEntity, Integer> {}

