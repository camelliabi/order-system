package com.camellia.ordersystem.controller;
import com.camellia.ordersystem.order.Order;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.camellia.ordersystem.menu.MenuItem;
import com.camellia.ordersystem.order.OrderItem;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api")
public class AllOrdersController { 

    // In-memory storage for orders (simulates database until persistence is implemented)
    // Using ConcurrentHashMap for thread-safety
    private static final Map<Integer, Order> orderStore = new ConcurrentHashMap<>();
    
    // Valid status transitions
    private static final List<String> VALID_STATUSES = List.of("NEW", "ACCEPTED", "READY", "COMPLETED", "CANCELLED");

    // Initialize orders on first access
    static {
        initializeOrders();
    }

    /**
     * Initialize sample orders in the in-memory store
     */
    private static void initializeOrders() {
        Order order1 = createSampleOrder1();
        Order order2 = createSampleOrder2();
        
        orderStore.put(order1.getOrderId(), order1);
        orderStore.put(order2.getOrderId(), order2);
    }

    /**
     * Create sample order 1 with options and notes
     */
    private static Order createSampleOrder1() {
        MenuItem menuItem1 = new MenuItem(1, "Fried Rice", 8.99);
        menuItem1.addOption("Chicken", 8.99);
        menuItem1.addOption("Beef", 9.99);
        menuItem1.addNote("No onions", 0);
        menuItem1.addNote("Add rice", 1.00);

        MenuItem menuItem2 = new MenuItem(2, "Beef Noodles", 12.99);
        MenuItem menuItem3 = new MenuItem(3, "Spring Rolls", 5.49);

        OrderItem item1 = new OrderItem(menuItem1, 2, "John Doe");
        item1.setChosenOption("Chicken");
        item1.setNote("No onions");

        OrderItem item2 = new OrderItem(menuItem2, 1, "Jane Smith");
        OrderItem item3 = new OrderItem(menuItem3, 3, "Alice Johnson");

        Order order = new Order(101, "A1");
        order.addItems(List.of(item1, item2, item3));
        order.setOrderStatus("NEW");
        order.setCreatedAt(LocalDateTime.now());
        
        return order;
    }

    /**
     * Create sample order 2
     */
    private static Order createSampleOrder2() {
        MenuItem menuItem1 = new MenuItem(1, "Fried Rice", 8.99);
        MenuItem menuItem2 = new MenuItem(2, "Beef Noodles", 12.99);
        
        OrderItem item1 = new OrderItem(menuItem1, 1);
        OrderItem item2 = new OrderItem(menuItem2, 3);

        Order order = new Order(102, "B2");
        order.addItems(List.of(item1, item2));
        order.setOrderStatus("NEW");
        order.setCreatedAt(LocalDateTime.now());
        
        return order;
    }

    /**
     * GET /api/all_orders
     * Returns all orders from in-memory store
     */
    @GetMapping("/all_orders")
    public ResponseEntity<List<Order>> allOrders() {
        List<Order> orders = new ArrayList<>(orderStore.values());
        return ResponseEntity.ok(orders);
    }

    /**
     * PATCH /api/all_orders/{orderId}
     * Updates order status
     * 
     * Request body: { "status": "ACCEPTED" }
     * 
     * @param orderId The ID of the order to update
     * @param updates Map containing the status update
     * @return Updated order or error response
     */
    @PatchMapping("/all_orders/{orderId}")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable int orderId, 
            @RequestBody Map<String, String> updates) {
        
        // Validate request body
        if (updates == null || !updates.containsKey("status")) {
            return ResponseEntity
                .badRequest()
                .body(Map.of("error", "Missing 'status' field in request body"));
        }

        String newStatus = updates.get("status");
        
        // Validate status value
        if (newStatus == null || newStatus.trim().isEmpty()) {
            return ResponseEntity
                .badRequest()
                .body(Map.of("error", "Status cannot be empty"));
        }

        // Validate status is one of the allowed values
        if (!VALID_STATUSES.contains(newStatus)) {
            return ResponseEntity
                .badRequest()
                .body(Map.of(
                    "error", "Invalid status. Allowed values: " + String.join(", ", VALID_STATUSES)
                ));
        }

        // Find order in store
        Order order = orderStore.get(orderId);
        
        if (order == null) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Order not found with ID: " + orderId));
        }

        // Update order status
        String previousStatus = order.getOrderStatus();
        order.setOrderStatus(newStatus);
        
        // Log the status change (useful for debugging)
        System.out.println(String.format(
            "[ORDER STATUS UPDATE] Order #%d: %s -> %s", 
            orderId, previousStatus, newStatus
        ));

        // Return updated order
        return ResponseEntity.ok(order);
    }

    /**
     * Helper method to get order by ID (for future use)
     */
    public Order getOrderById(int orderId) {
        return orderStore.get(orderId);
    }

    /**
     * Helper method to add new order (for future POST endpoint)
     */
    public Order addOrder(Order order) {
        orderStore.put(order.getOrderId(), order);
        return order;
    }
}
