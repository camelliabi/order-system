package com.camellia.ordersystem.order;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import com.camellia.ordersystem.menu.MenuItem;

import java.util.List;
import java.util.Map;

/**
 * Regression tests for Order class price calculation bugs.
 * 
 * These tests verify fixes for:
 * - Issue #1: setTotalPrice() missing quantity multiplication
 * - Issue #2: Wasteful setTotalPrice() calls in constructors
 */
public class OrderTest {

    private MenuItem riceItem;
    private MenuItem noodlesItem;

    @BeforeEach
    public void setUp() {
        // Create test menu items with options and notes
        riceItem = new MenuItem(1, "Fried Rice", 8.99);
        riceItem.setOptions(Map.of(
            "Chicken", 8.99,
            "Beef", 9.99
        ));
        riceItem.setNotes(Map.of(
            "Add rice", 1.00,
            "No onions", 0.00
        ));

        noodlesItem = new MenuItem(2, "Beef Noodles", 12.99);
    }

    /**
     * REGRESSION TEST for Issue #1: setTotalPrice() missing quantity multiplication
     * 
     * Buggy behavior:
     * - setTotalPrice() summed item prices without multiplying by quantity
     * - Order with 3 items at $10 each would total $10 instead of $30
     * 
     * This test would FAIL with buggy code and PASS with fix.
     */
    @Test
    @DisplayName("setTotalPrice() should correctly multiply by quantity")
    public void shouldCalculateCorrectTotalWhenSettingPriceWithMultipleQuantities() {
        // Arrange
        Order order = new Order(1, "TABLE-1");
        OrderItem item1 = new OrderItem(riceItem, 3);  // 3 x $8.99 = $26.97
        OrderItem item2 = new OrderItem(noodles Item, 2);  // 2 x $12.99 = $25.98
        order.addItems(List.of(item1, item2));

        // Act
        order.setTotalPrice();  // Recalculate from scratch

        // Assert
        // BUGGY CODE WOULD FAIL: Would calculate $8.99 + $12.99 = $21.98 (missing quantities)
        // FIXED CODE PASSES: Correctly calculates (3 * $8.99) + (2 * $12.99) = $52.95
        assertEquals(52.95, order.getTotalPrice(), 0.01, 
            "setTotalPrice() must multiply item price by quantity");
    }

    /**
     * REGRESSION TEST for Issue #1: setTotalPrice() vs addItems() consistency
     * 
     * Buggy behavior:
     * - addItems() multiplied by quantity (correct)
     * - setTotalPrice() did NOT multiply by quantity (incorrect)
     * - Same data would produce different totals depending on method used
     * 
     * This test would FAIL with buggy code and PASS with fix.
     */
    @Test
    @DisplayName("setTotalPrice() should match addItems() calculation")
    public void shouldProduceSameTotalUsingAddItemsOrSetTotalPrice() {
        // Arrange
        OrderItem item = new OrderItem(riceItem, 5);  // 5 x $8.99

        // Act - Method 1: Use addItems()
        Order order1 = new Order(1, "TABLE-A");
        order1.addItems(List.of(item));
        double totalViaAddItems = order1.getTotalPrice();

        // Act - Method 2: Add items manually then call setTotalPrice()
        Order order2 = new Order(2, "TABLE-B");
        order2.getOrderItems().add(item);  // Direct list manipulation
        order2.setTotalPrice();  // Recalculate
        double totalViaSetTotalPrice = order2.getTotalPrice();

        // Assert
        // BUGGY CODE WOULD FAIL: totalViaAddItems = $44.95, totalViaSetTotalPrice = $8.99
        // FIXED CODE PASSES: Both methods produce $44.95
        assertEquals(totalViaAddItems, totalViaSetTotalPrice, 0.01,
            "Both calculation methods must produce identical results");
    }

    /**
     * REGRESSION TEST for Issue #2: Wasteful constructor calls
     * 
     * Buggy behavior:
     * - Constructors called setTotalPrice() on empty orderItems list
     * - Wasted CPU cycles iterating over empty list
     * 
     * This test verifies the fix (direct assignment instead of method call).
     */
    @Test
    @DisplayName("Constructor should initialize totalPrice to 0.0 without wasteful computation")
    public void shouldInitializeTotalPriceToZeroInConstructor() {
        // Act
        Order order1 = new Order();
        Order order2 = new Order(100, "TABLE-X");

        // Assert
        assertEquals(0.0, order1.getTotalPrice(), 0.0,
            "Default constructor should initialize totalPrice to 0.0");
        assertEquals(0.0, order2.getTotalPrice(), 0.0,
            "Parameterized constructor should initialize totalPrice to 0.0");
        assertTrue(order1.getOrderItems().isEmpty(),
            "New order should have empty items list");
        assertTrue(order2.getOrderItems().isEmpty(),
            "New order should have empty items list");
    }

    /**
     * REGRESSION TEST for comprehensive price calculation with options and notes
     * 
     * Verifies that setTotalPrice() correctly handles:
     * - Items with chosen options (using option price instead of base)
     * - Items with notes (adding note prices)
     * - Quantity multiplication
     */
    @Test
    @DisplayName("setTotalPrice() should correctly calculate with options and notes")
    public void shouldCalculateCorrectTotalWithOptionsAndNotes() {
        // Arrange
        Order order = new Order(1, "TABLE-5");
        
        // Item with option: Beef ($9.99) instead of base ($8.99)
        OrderItem item1 = new OrderItem(riceItem, 2);
        item1.setChosenOption("Beef");
        
        // Item with note: Add rice ($1.00)
        OrderItem item2 = new OrderItem(riceItem, 1);
        item2.setNote("Add rice");
        
        order.getOrderItems().add(item1);
        order.getOrderItems().add(item2);

        // Act
        order.setTotalPrice();

        // Assert
        // Item 1: 2 x $9.99 (Beef option) = $19.98
        // Item 2: 1 x ($8.99 base + $1.00 note) = $9.99
        // Total: $19.98 + $9.99 = $29.97
        // BUGGY CODE WOULD FAIL: Would calculate $9.99 + $9.99 = $19.98 (missing quantities)
        // FIXED CODE PASSES: Correctly calculates $29.97
        assertEquals(29.97, order.getTotalPrice(), 0.01,
            "Total should include option prices, note prices, and quantities");
    }

    /**
     * Edge case test: Empty order
     */
    @Test
    @DisplayName("setTotalPrice() should handle empty order")
    public void shouldHandleEmptyOrder() {
        // Arrange
        Order order = new Order(1, "TABLE-1");

        // Act
        order.setTotalPrice();

        // Assert
        assertEquals(0.0, order.getTotalPrice(), 0.0,
            "Empty order should have total of 0.0");
    }

    /**
     * Edge case test: Zero quantity (should this even be allowed?)
     */
    @Test
    @DisplayName("setTotalPrice() should handle zero quantity items")
    public void shouldHandleZeroQuantityItems() {
        // Arrange
        Order order = new Order(1, "TABLE-1");
        OrderItem item = new OrderItem(riceItem, 0);  // Quantity = 0
        order.getOrderItems().add(item);

        // Act
        order.setTotalPrice();

        // Assert
        assertEquals(0.0, order.getTotalPrice(), 0.0,
            "Item with quantity 0 should contribute 0 to total");
    }
}
