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
        for(OrderItem itm : orderItems) {
            total += this.calculateItemPrice(itm);
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
        MenuItem menuItem = orderItem.getMenuItem();
        double itemPrice = menuItem.getItemPrice(); // Start with base price
        
        // If customer chose an option (e.g., "Chicken" instead of base "Fried Rice")
        // Use the option's price instead of base price
        if (orderItem.getChosenOption() != null && !orderItem.getChosenOption().trim().isEmpty()) {
            String chosenOptionName = orderItem.getChosenOption().trim();
            Map<String, Double> options = menuItem.getOptions();
            
            if (options != null && options.containsKey(chosenOptionName)) {
                itemPrice = options.get(chosenOptionName); // Replace base price with option price
            }
        }
        
        // Add note/add-on prices (e.g., "Add rice" costs extra $1.00)
        if (orderItem.getNote() != null && !orderItem.getNote().trim().isEmpty()) {
            String noteName = orderItem.getNote().trim();
            Map<String, Double> notes = menuItem.getNotes();
            
            if (notes != null && notes.containsKey(noteName)) {
                double notePrice = notes.get(noteName);
                itemPrice += notePrice; // Add note price to total
            }
        }
        
        return itemPrice;
    }

    public void addItem(MenuItem item, int quantity) {
        OrderItem orderItem = new OrderItem(item, quantity);
        orderItems.add(orderItem);
        totalPrice += calculateItemPrice(orderItem) * quantity;
        
    }

    public void removeItem(MenuItem item, int quantity) {
        for (int i = 0; i < orderItems.size(); i++) {
            OrderItem orderItem = orderItems.get(i);
            if (orderItem.getMenuItem().getItemId() == item.getItemId() && orderItem.getQuantity() == quantity) {
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
        for (OrderItem orderItem : items) {
            orderItems.add(orderItem);
            totalPrice += calculateItemPrice(orderItem) * orderItem.getQuantity();
        }
    }
}
