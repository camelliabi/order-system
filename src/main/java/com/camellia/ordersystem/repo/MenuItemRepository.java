package com.camellia.ordersystem.repo;

import com.camellia.ordersystem.entity.MenuItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;

/**
 * Repository for MenuItemEntity.
 * FIXED: Added @EntityGraph to prevent LazyInitializationException.
 */
public interface MenuItemRepository extends JpaRepository<MenuItemEntity, Integer> {
    
    /**
     * Find all menu items with options and notes eagerly loaded.
     * 
     * The @EntityGraph annotation tells JPA to fetch the 'options' and 'notes'
     * collections in the same query (using LEFT JOIN), preventing
     * LazyInitializationException when accessing these collections outside
     * the transaction boundary.
     * 
     * Without this, accessing item.getOptions() or item.getNotes() in the
     * controller would throw LazyInitializationException.
     */
    @Override
    @EntityGraph(attributePaths = {"options", "notes"})
    List<MenuItemEntity> findAll();
}
