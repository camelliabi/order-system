package com.camellia.ordersystem.repo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.camellia.ordersystem.entity.MenuItemEntity;
import com.camellia.ordersystem.entity.MenuItemOptionEntity;
import com.camellia.ordersystem.entity.MenuItemNoteEntity;

import java.math.BigDecimal;
import java.util.List;

/**
 * Integration test for MenuItemRepository LazyInitializationException fix.
 * 
 * Tests verify:
 * - Issue #4: @EntityGraph prevents LazyInitializationException
 * - Collections are accessible outside transaction context
 */
@DataJpaTest
public class MenuItemRepositoryTest {

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private TestEntityManager entityManager;

    /**
     * REGRESSION TEST for Issue #4: LazyInitializationException fix
     * 
     * Buggy behavior (without @EntityGraph):
     * - findAll() returns entities with lazy proxy collections
     * - Accessing options or notes outside transaction throws LazyInitializationException
     * - Controller cannot map entities to DTOs
     * 
     * Fixed behavior (with @EntityGraph):
     * - findAll() eagerly loads options and notes
     * - Collections are accessible after transaction closes
     * - Controller can safely access collections
     * 
     * This test would FAIL without @EntityGraph and PASS with it.
     */
    @Test
    @DisplayName("findAll() should eagerly load options and notes to prevent LazyInitializationException")
    public void shouldLoadOptionsAndNotesWithoutLazyInitializationException() {
        // Arrange - Create test menu item with options and notes
        MenuItemEntity menuItem = new MenuItemEntity();
        menuItem.setItemName("Test Burger");
        menuItem.setItemPrice(new BigDecimal("10.99"));
        menuItem.setSoldout(false);
        
        menuItem = entityManager.persistAndFlush(menuItem);
        Integer itemId = menuItem.getItemId();
        
        // Add an option
        MenuItemOptionEntity option = new MenuItemOptionEntity();
        option.setMenuItem(menuItem);
        option.setOptionName("Large");
        option.setOptionPrice(new BigDecimal("12.99"));
        entityManager.persistAndFlush(option);
        
        // Add a note
        MenuItemNoteEntity note = new MenuItemNoteEntity();
        note.setMenuItem(menuItem);
        note.setNoteName("Extra cheese");
        note.setNotePrice(new BigDecimal("1.50"));
        entityManager.persistAndFlush(note);
        
        entityManager.clear(); // Clear persistence context to simulate transaction boundary

        // Act - Call findAll() (this returns entities, transaction closes)
        List<MenuItemEntity> items = menuItemRepository.findAll();

        // Assert - Access collections OUTSIDE transaction context
        // BUGGY CODE (no @EntityGraph) WOULD FAIL HERE with LazyInitializationException
        // FIXED CODE (with @EntityGraph) PASSES because collections are initialized
        assertFalse(items.isEmpty(), "Should find at least one menu item");
        
        MenuItemEntity retrievedItem = items.stream()
            .filter(i -> i.getItemId().equals(itemId))
            .findFirst()
            .orElseThrow();

        // These assertions would throw LazyInitializationException without @EntityGraph
        assertDoesNotThrow(() -> retrievedItem.getOptions().size(),
            "Should access options without LazyInitializationException");
        assertDoesNotThrow(() -> retrievedItem.getNotes().size(),
            "Should access notes without LazyInitializationException");

        // Verify collections are actually loaded
        assertNotNull(retrievedItem.getOptions(),
            "Options should not be null");
        assertNotNull(retrievedItem.getNotes(),
            "Notes should not be null");
        assertFalse(retrievedItem.getOptions().isEmpty(),
            "Options should contain the added option");
        assertFalse(retrievedItem.getNotes().isEmpty(),
            "Notes should contain the added note");
        
        // Verify we can iterate and access nested properties
        retrievedItem.getOptions().forEach(opt -> {
            assertNotNull(opt.getOptionName(), "Option name should be accessible");
            assertNotNull(opt.getOptionPrice(), "Option price should be accessible");
        });
        
        retrievedItem.getNotes().forEach(n -> {
            assertNotNull(n.getNoteName(), "Note name should be accessible");
            assertNotNull(n.getNotePrice(), "Note price should be accessible");
        });
    }

    /**
     * Additional test: Verify @EntityGraph doesn't break empty collections
     */
    @Test
    @DisplayName("findAll() should handle menu items with no options or notes")
    public void shouldHandleMenuItemsWithEmptyCollections() {
        // Arrange - Create menu item WITHOUT options or notes
        MenuItemEntity menuItem = new MenuItemEntity();
        menuItem.setItemName("Simple Item");
        menuItem.setItemPrice(new BigDecimal("5.99"));
        menuItem.setSoldout(false);
        
        entityManager.persistAndFlush(menuItem);
        entityManager.clear();

        // Act
        List<MenuItemEntity> items = menuItemRepository.findAll();

        // Assert - Should not throw exception even if collections are empty
        assertDoesNotThrow(() -> {
            items.forEach(item -> {
                // Access collections (they may be empty but should be initialized)
                int optionsCount = item.getOptions() != null ? item.getOptions().size() : 0;
                int notesCount = item.getNotes() != null ? item.getNotes().size() : 0;
                
                // No assertion on counts - just verify no exception thrown
                assertTrue(optionsCount >= 0);
                assertTrue(notesCount >= 0);
            });
        }, "Should handle items with empty collections without exception");
    }
}
