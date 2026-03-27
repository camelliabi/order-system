package com.camellia.ordersystem.order;
import com.camellia.ordersystem.menu.MenuItem;

public class OrderItem {
    private MenuItem menuItem;
    private int quantity;
    private String customerName;
    private String chosenOption;
    private String note;


    public OrderItem() {
        
    }

    public OrderItem(MenuItem menuItem, int quantity) {
        // FIX: Add null check for menuItem
        if (menuItem == null) {
            throw new IllegalArgumentException("MenuItem cannot be null");
        }
        // FIX: Add validation for quantity
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive, got: " + quantity);
        }
        
        this.menuItem = menuItem;
        this.quantity = quantity;
    }

    public OrderItem(MenuItem menuItem, int quantity, String customerName) {
        // FIX: Add null check for menuItem
        if (menuItem == null) {
            throw new IllegalArgumentException("MenuItem cannot be null");
        }
        // FIX: Add validation for quantity
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive, got: " + quantity);
        }
        
        this.menuItem = menuItem;
        this.quantity = quantity;
        this.customerName = customerName;
    }
    
    // Getters
    public MenuItem getMenuItem() {
        return menuItem;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public String getCustomerName() {
        return customerName;
    }

    public String getChosenOption() {
        return chosenOption;
    }
    
    public String getNote() {
        return note;
    }

    // Setters 
    public void setMenuItem(MenuItem menuItem) {
        // FIX: Add null check for menuItem
        if (menuItem == null) {
            throw new IllegalArgumentException("MenuItem cannot be null");
        }
        this.menuItem = menuItem;
    }
    
    public void setQuantity(int quantity) {
        // FIX: Add validation for quantity
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive, got: " + quantity);
        }
        this.quantity = quantity;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    
    public void setChosenOption(String chosenOption) {
        this.chosenOption = chosenOption;
    }
    
    public void setNote(String note) {
        this.note = note;
    }
}
