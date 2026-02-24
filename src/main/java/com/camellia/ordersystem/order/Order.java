package com.camellia.ordersystem.order;
import java.util.List;
import java.sql.Timestamp;
import java.util.ArrayList;
import com.camellia.ordersystem.menu.MenuItem;
import java.util.Map;

public class Order {
    private int orderId;
    private String tableId; 
    private double totalPrice;
    private List<OrderItem> orderItems = new ArrayList<OrderItem>();
    private String orderStatus;
    private Timestamp createdAt;

    public Order() {
        setTotalPrice();
        
    }

    public Order(int orderId, String tableId) {
        this.orderId = orderId;
        this.tableId = tableId;
        setTotalPrice();
    }

    public int getOrderId() {
        return orderId;
    }
    public String getTableId() {
        return tableId;
    }

    public void setTotalPrice(){
        double total = 0;
        // FIX: Add null check for orderItems list
        if (orderItems != null) {
            for(OrderItem itm : orderItems) {
                // FIX: Add null check for individual order item
                if (itm != null) {
                    total += this.calculateItemPrice(itm);
                }
            }
        }
        totalPrice = total;

    }

    public double getTotalPrice() {

        return totalPrice;
    }
    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    /**
     * Calculate the actual price for an order item including chosen option and notes
     * @param orderItem The order item to calculate price for
     * @return The total price for this item (base/option price + note prices)
     */
    private double calculateItemPrice(OrderItem orderItem) {
        // FIX: Add null check for orderItem
        if (orderItem == null) {
            return 0.0;
        }
        
        MenuItem menuItem = orderItem.getMenuItem();
        // FIX: Add null check for menuItem
        if (menuItem == null) {
            return 0.0;
        }
        
        double itemPrice = menuItem.getItemPrice(); // Start with base price
        
        // If customer chose an option (e.g., "Chicken" instead of base "Fried Rice")
        // Use the option's price instead of base price
        if (orderItem.getChosenOption() != null && !orderItem.getChosenOption().trim().isEmpty()) {
            String chosenOptionName = orderItem.getChosenOption().trim();
            Map<String, Double> options = menuItem.getOptions();
            
            // FIX: Add null check for options map and ensure containsKey doesn't throw NPE
            if (options != null && !options.isEmpty() && options.containsKey(chosenOptionName)) {
                Double optionPrice = options.get(chosenOptionName);
                // FIX: Add null check for the price value from map
                if (optionPrice != null) {
                    itemPrice = optionPrice; // Replace base price with option price
                }
            }
        }
        
        // Add note/add-on prices (e.g., "Add rice" costs extra $1.00)
        if (orderItem.getNote() != null && !orderItem.getNote().trim().isEmpty()) {
            String noteName = orderItem.getNote().trim();
            Map<String, Double> notes = menuItem.getNotes();
            
            // FIX: Add null check for notes map and ensure containsKey doesn't throw NPE
            if (notes != null && !notes.isEmpty() && notes.containsKey(noteName)) {
                Double notePrice = notes.get(noteName);
                // FIX: Add null check for the price value from map
                if (notePrice != null) {
                    itemPrice += notePrice; // Add note price to total
                }
            }
        }
        
        return itemPrice;
    }

    public void addItem(MenuItem item, int quantity) {
        // FIX: Add validation for null item and negative/zero quantity
        if (item == null) {
            throw new IllegalArgumentException("Cannot add null menu item to order");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive, got: " + quantity);
        }
        
        OrderItem orderItem = new OrderItem(item, quantity);
        orderItems.add(orderItem);
        totalPrice += calculateItemPrice(orderItem) * quantity;
        
    }

    public void removeItem(MenuItem item, int quantity) {
        // FIX: Add validation for null item
        if (item == null) {
            return; // Silently ignore null items in remove operation
        }
        
        // FIX: Add validation for negative quantity
        if (quantity <= 0) {
            return; // Silently ignore invalid quantities
        }
        
        // FIX: Use size() check to prevent IndexOutOfBoundsException
        if (orderItems == null || orderItems.isEmpty()) {
            return;
        }
        
        // FIX: Iterate backwards to safely remove items while iterating
        for (int i = orderItems.size() - 1; i >= 0; i--) {
            OrderItem orderItem = orderItems.get(i);
            // FIX: Add null checks before accessing methods
            if (orderItem != null && orderItem.getMenuItem() != null && 
                orderItem.getMenuItem().getItemId() == item.getItemId() && 
                orderItem.getQuantity() == quantity) {
                orderItems.remove(i);
                totalPrice -= calculateItemPrice(orderItem) * quantity;
                break;
            }
        }
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String status) {
        this.orderStatus = status;
    }

    public Timestamp getCreatedAt(){
        return createdAt;
    }

    public void setCreatedAt(Timestamp time){
        this.createdAt = time;
    }

    public void addItems(List<OrderItem> items) {
        // FIX: Add null check for items list
        if (items == null || items.isEmpty()) {
            return;
        }
        
        for (OrderItem orderItem : items) {
            // FIX: Add null check for individual order item
            if (orderItem != null) {
                orderItems.add(orderItem);
                // FIX: Add null check before accessing quantity
                int qty = orderItem.getQuantity();
                if (qty > 0) {
                    totalPrice += calculateItemPrice(orderItem) * qty;
                }
            }
        }
    }
}
