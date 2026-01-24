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
        this.menuItem = menuItem;
        this.quantity = quantity;
    }

    public OrderItem(MenuItem menuItem, int quantity, String customerName) {
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
        this.menuItem = menuItem;
    }
    public void setQuantity(int quantity) {
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
