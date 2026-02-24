package com.camellia.ordersystem.order;

import java.util.ArrayList;
import java.util.List;

public class AllOrders {
    
    private List<Order> orders;

    public AllOrders() {
        // FIX: Initialize orders list to prevent NullPointerException
        this.orders = new ArrayList<>();
    }

    public List<Order> getOrders() {
        // FIX: Return empty list if orders is somehow null
        return orders != null ? orders : new ArrayList<>();
    }

    public void setOrders(List<Order> orders) {
        // FIX: Initialize with empty list if null is passed
        this.orders = orders != null ? orders : new ArrayList<>();
    }

    public void addOrder(Order order) {
        // FIX: Add null check for defensive programming
        if (order != null) {
            // FIX: Ensure orders list is initialized
            if (this.orders == null) {
                this.orders = new ArrayList<>();
            }
            orders.add(order);
        }
    }

    public void removeOrder(Order order) {
        // FIX: Add null check for defensive programming
        if (order != null && this.orders != null) {
            orders.remove(order);
        }
    }
}
