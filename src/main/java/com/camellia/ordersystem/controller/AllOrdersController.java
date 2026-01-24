package com.camellia.ordersystem.controller;
import com.camellia.ordersystem.order.Order;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.camellia.ordersystem.menu.MenuItem;
import com.camellia.ordersystem.order.OrderItem;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AllOrdersController { 

    public List<OrderItem> orderItems1() {
        MenuItem menuItem1 = new MenuItem(1, "Fried Rice", 8.99);
        MenuItem menuItem2 = new MenuItem(2, "Beef Noodles", 12.99);
        OrderItem item1 = new OrderItem(menuItem1, 1);
        OrderItem item2 = new OrderItem(menuItem2, 3);

        return List.of(
                item1,
                item2
        );
    }

    public List<OrderItem> orderItems() {
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

        return List.of(
                item1,
                item2,
                item3
        );
    }

    @GetMapping("/all_orders")
    public List<Order> allOrders() {
        Order order1 = new Order(101, "A1");
        order1.addItems(orderItems());

        Order order2 = new Order(102, "B2");
        order2.addItems(orderItems1());

        return List.of(
                order1,
                order2
 
        );
    }
}