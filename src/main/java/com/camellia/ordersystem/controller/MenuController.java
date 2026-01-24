package com.camellia.ordersystem.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.camellia.ordersystem.menu.MenuItem;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MenuController {

    @GetMapping("/menu")
    public List<MenuItem> menu() {
        MenuItem item1 = new MenuItem(1, "Fried Rice", 8.99);
        item1.addOption("Chicken", 8.99);
        item1.addOption("Beef", 9.99);
        item1.addNote("No onions", 0);
        item1.addNote("Add rice", 1.00);

        MenuItem item2 = new MenuItem(2, "Beef Noodles", 12.99);
        
        
        item2.setSoldout(true);
        MenuItem item3 = new MenuItem(3, "Coke Zero", 2.99);
        MenuItem item4 = new MenuItem(4, "Spring Rolls", 5.49);
        item4.addNote("Soy sauce", 0);
        return List.of(
                item1,
                item2,
                item3,
                item4
        );
    }
}