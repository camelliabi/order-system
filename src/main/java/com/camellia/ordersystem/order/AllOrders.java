package com.camellia.ordersystem.order;

import java.util.List;

public class AllOrders {
    
    List<Order> orders;

    public AllOrders() {
        
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public void addOrder(Order order) {
        orders.add(order);
    }

    public void removeOrder(Order order) {
        orders.remove(order);
    }
}
