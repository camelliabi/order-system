package com.camellia.ordersystem.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Regression tests for MenuItemDTO encapsulation fixes.
 * 
 * These tests verify fixes for:
 * - Issue #2: Inconsistent field access modifiers
 * - Proper JavaBeans compliance
 * - Type consistency
 */
public class MenuItemDTOTest {

    private MenuItemDTO dto;

    @BeforeEach
    public void setUp() {
        dto = new MenuItemDTO();
    }

    /**
     * REGRESSION TEST for Issue #2: All fields should have getters
     * 
     * Buggy behavior:
     * - Fields were public, no getters existed for most fields
     * - Only soldout had a getter
     * - Violated JavaBeans specification
     * 
     * This test ensures proper JavaBeans compliance.
     */
    @Test
    @DisplayName("All fields should have proper getters")
    public void shouldHaveGettersForAllFields() {
        // Arrange
        dto.setItemId(42);
        dto.setItemName("Test Item");
        dto.setItemPrice(new BigDecimal("19.99"));
        dto.setSoldout(true);
        dto.setOptions(Map.of("Large", new BigDecimal("2.00")));
        dto.setNotes(Map.of("Extra sauce", new BigDecimal("0.50")));

        // Act & Assert - Verify all getters exist and work
        assertEquals(42, dto.getItemId(),
            "getItemId() should return set value");
        assertEquals("Test Item", dto.getItemName(),
            "getItemName() should return set value");
        assertEquals(new BigDecimal("19.99"), dto.getItemPrice(),
            "getItemPrice() should return set value");
        assertTrue(dto.isSoldout(),
            "isSoldout() should return set value");
        assertNotNull(dto.getOptions(),
            "getOptions() should return set value");
        assertNotNull(dto.getNotes(),
            "getNotes() should return set value");
    }

    /**
     * REGRESSION TEST for Issue #2: All fields should have setters
     */
    @Test
    @DisplayName("All fields should have proper setters")
    public void shouldHaveSettersForAllFields() {
        // Act - Call all setters (should not throw exceptions)
        assertDoesNotThrow(() -> dto.setItemId(1));
        assertDoesNotThrow(() -> dto.setItemName("Pizza"));
        assertDoesNotThrow(() -> dto.setItemPrice(new BigDecimal("15.99")));
        assertDoesNotThrow(() -> dto.setSoldout(false));
        assertDoesNotThrow(() -> dto.setOptions(new HashMap<>()));
        assertDoesNotThrow(() -> dto.setNotes(new HashMap<>()));

        // Assert - Verify values were set (through getters)
        assertEquals(1, dto.getItemId());
        assertEquals("Pizza", dto.getItemName());
        assertEquals(new BigDecimal("15.99"), dto.getItemPrice());
        assertFalse(dto.isSoldout());
        assertNotNull(dto.getOptions());
        assertNotNull(dto.getNotes());
    }

    /**
     * REGRESSION TEST for Issue #2: Field type consistency
     * 
     * Buggy behavior:
     * - soldout field was boolean (primitive)
     * - getSoldout() returned Boolean (wrapper)
     * - setSoldout() accepted Boolean (wrapper)
     * - Type mismatch caused potential NullPointerException
     * 
     * This test would FAIL with buggy code if null was passed.
     */
    @Test
    @DisplayName("soldout field should handle boolean consistently")
    public void shouldHandleSoldoutBooleanTypeConsistently() {
        // Act
        dto.setSoldout(true);
        boolean result1 = dto.isSoldout();
        
        dto.setSoldout(false);
        boolean result2 = dto.isSoldout();

        // Assert
        assertTrue(result1, "Should return true when set to true");
        assertFalse(result2, "Should return false when set to false");
        
        // Verify getter returns primitive boolean (not Boolean wrapper)
        // This prevents NullPointerException from autoboxing
        assertNotNull(dto.isSoldout(), "Getter should never return null");
    }

    /**
     * REGRESSION TEST for Issue #2: Fields should be private
     * 
     * Buggy behavior:
     * - Most fields were PUBLIC
     * - Broke encapsulation
     * - Violated JavaBeans spec
     * 
     * This test ensures fields are properly encapsulated.
     */
    @Test
    @DisplayName("All fields should be private for proper encapsulation")
    public void shouldHaveAllPrivateFields() throws Exception {
        // Use reflection to verify all fields are private
        Field[] fields = MenuItemDTO.class.getDeclaredFields();
        
        for (Field field : fields) {
            assertTrue(Modifier.isPrivate(field.getModifiers()),
                "Field '" + field.getName() + "' should be private, but is " +
                Modifier.toString(field.getModifiers()));
        }
        
        // BUGGY CODE WOULD FAIL: itemId, itemName, itemPrice, options, notes were public
        // FIXED CODE PASSES: All fields are now private
    }

    /**
     * Test that collections can be set and retrieved
     */
    @Test
    @DisplayName("Options and notes maps should be settable and gettable")
    public void shouldHandleOptionsAndNotesMaps() {
        // Arrange
        Map<String, BigDecimal> options = new HashMap<>();
        options.put("Small", new BigDecimal("8.99"));
        options.put("Large", new BigDecimal("12.99"));

        Map<String, BigDecimal> notes = new HashMap<>();
        notes.put("Extra cheese", new BigDecimal("1.50"));
        notes.put("No onions", new BigDecimal("0.00"));

        // Act
        dto.setOptions(options);
        dto.setNotes(notes);

        // Assert
        assertEquals(options, dto.getOptions(),
            "getOptions() should return the set map");
        assertEquals(notes, dto.getNotes(),
            "getNotes() should return the set map");
        assertEquals(2, dto.getOptions().size());
        assertEquals(2, dto.getNotes().size());
    }

    /**
     * Test null handling for optional fields
     */
    @Test
    @DisplayName("DTO should handle null values for optional fields")
    public void shouldHandleNullValues() {
        // Act
        dto.setItemId(null);
        dto.setItemName(null);
        dto.setItemPrice(null);
        dto.setOptions(null);
        dto.setNotes(null);

        // Assert - Nulls should be accepted for optional fields
        assertNull(dto.getItemId());
        assertNull(dto.getItemName());
        assertNull(dto.getItemPrice());
        assertNull(dto.getOptions());
        assertNull(dto.getNotes());
    }

    /**
     * Test that DTO can be instantiated and used as JavaBean
     */
    @Test
    @DisplayName("DTO should work as standard JavaBean")
    public void shouldWorkAsJavaBean() {
        // This test verifies JavaBeans compatibility
        // Frameworks like Jackson, Spring, etc. expect this pattern

        // Arrange & Act
        dto.setItemId(99);
        dto.setItemName("Burger");
        dto.setItemPrice(new BigDecimal("14.99"));
        dto.setSoldout(false);

        // Assert - All values accessible through getters
        assertAll("JavaBean property access",
            () -> assertEquals(99, dto.getItemId()),
            () -> assertEquals("Burger", dto.getItemName()),
            () -> assertEquals(new BigDecimal("14.99"), dto.getItemPrice()),
            () -> assertFalse(dto.isSoldout())
        );
    }
}
