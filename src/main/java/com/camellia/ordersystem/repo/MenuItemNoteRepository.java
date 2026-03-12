package com.camellia.ordersystem.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.camellia.ordersystem.entity.MenuItemNoteEntity;

public interface MenuItemNoteRepository extends JpaRepository<MenuItemNoteEntity, Integer> {
    // no custom methods needed; cascade/orphanRemoval handles note lifecycle
}
