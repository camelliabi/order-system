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
                // FIX: Add null check for individual items
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
        // FIX: Return empty list instead of null
        return orderItems != null ? orderItems : new ArrayList<>();
    }

    /**
     * Calculate the actual price for an order item including chosen option and notes
     * @param orderItem The order item to calculate price for
     * @return The total price for this item (base/option price + note prices)
     */
    private double calculateItemPrice(OrderItem orderItem) {
        // FIX: Add null check for orderItem parameter
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
            
            if (options != null && options.containsKey(chosenOptionName)) {
                Double optionPrice = options.get(chosenOptionName);
                // FIX: Add null check for optionPrice
                if (optionPrice != null) {
                    itemPrice = optionPrice; // Replace base price with option price
                }
            }
        }
        
        // Add note/add-on prices (e.g., "Add rice" costs extra $1.00)
        if (orderItem.getNote() != null && !orderItem.getNote().trim().isEmpty()) {
            String noteName = orderItem.getNote().trim();
            Map<String, Double> notes = menuItem.getNotes();
            
            if (notes != null && notes.containsKey(noteName)) {
                Double notePrice = notes.get(noteName);
                // FIX: Add null check for notePrice
                if (notePrice != null) {
                    itemPrice += notePrice; // Add note price to total
                }
            }
        }
        
        return itemPrice;
    }

    public void addItem(MenuItem item, int quantity) {
        // FIX: Add null check for item parameter
        if (item == null) {
            throw new IllegalArgumentException("Cannot add null menu item to order");
        }
        // FIX: Validate quantity is positive
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0, got: " + quantity);
        }
        
        OrderItem orderItem = new OrderItem(item, quantity);
        // FIX: Ensure orderItems is initialized
        if (this.orderItems == null) {
            this.orderItems = new ArrayList<>();
        }
        orderItems.add(orderItem);
        totalPrice += calculateItemPrice(orderItem) * quantity;
        
    }

    public void removeItem(MenuItem item, int quantity) {
        // FIX: Add null check for item parameter
        if (item == null) {
            return;
        }
        // FIX: Add null check for orderItems
        if (this.orderItems == null) {
            return;
        }
        
        for (int i = 0; i < orderItems.size(); i++) {
            OrderItem orderItem = orderItems.get(i);
            // FIX: Add null check for orderItem and its menuItem
            if (orderItem != null && orderItem.getMenuItem() != null) {
                if (orderItem.getMenuItem().getItemId() == item.getItemId() && orderItem.getQuantity() == quantity) {
                    orderItems.remove(i);
                    totalPrice -= calculateItemPrice(orderItem) * quantity;
                    break;
                }
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
        if (items == null) {
            return;
        }
        
        // FIX: Ensure orderItems is initialized
        if (this.orderItems == null) {
            this.orderItems = new ArrayList<>();
        }
        
        for (OrderItem orderItem : items) {
            // FIX: Add null check for individual items
            if (orderItem != null) {
                orderItems.add(orderItem);
                totalPrice += calculateItemPrice(orderItem) * orderItem.getQuantity();
            }
        }
    }
}
