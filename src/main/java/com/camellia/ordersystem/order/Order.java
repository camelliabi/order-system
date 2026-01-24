package com.camellia.ordersystem.order;
import java.util.List;
import java.util.ArrayList;
import com.camellia.ordersystem.menu.MenuItem;


public class Order {
    private int orderId;
    private String tableId; 
    private double totalPrice;
    private List<OrderItem> orderItems = new ArrayList<OrderItem>();
    private String orderStatus;

    public Order() {
        
    }

    public Order(int orderId, String tableId) {
        this.orderId = orderId;
        this.tableId = tableId;
    }

    public int getOrderId() {
        return orderId;
    }
    public String getTableId() {
        return tableId;
    }
    public double getTotalPrice() {
        return totalPrice;
    }
    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void addItem(MenuItem item, int quantity) {
        orderItems.add(new OrderItem(item, quantity));
        totalPrice += item.getItemPrice() * quantity;
    }

    public void removeItem(MenuItem item, int quantity) {
        for (int i = 0; i < orderItems.size(); i++) {
            OrderItem orderItem = orderItems.get(i);
            if (orderItem.getMenuItem().getItemId() == item.getItemId() && orderItem.getQuantity() == quantity) {
                orderItems.remove(i);
                totalPrice -= item.getItemPrice() * quantity;
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

    public void addItems(List<OrderItem> items) {
        for (OrderItem orderItem : items) {
            orderItems.add(orderItem);
            totalPrice += orderItem.getMenuItem().getItemPrice() * orderItem.getQuantity();
        }
    }
}
