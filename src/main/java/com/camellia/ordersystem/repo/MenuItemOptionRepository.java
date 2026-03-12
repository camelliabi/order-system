package com.camellia.ordersystem.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.camellia.ordersystem.entity.MenuItemOptionEntity;

public interface MenuItemOptionRepository extends JpaRepository<MenuItemOptionEntity, Integer> {
    // no custom methods needed; cascade/orphanRemoval handles option lifecycle
}
