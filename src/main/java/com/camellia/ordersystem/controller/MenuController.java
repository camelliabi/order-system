package com.camellia.ordersystem.controller;

import com.camellia.ordersystem.entity.MenuItemEntity;
import com.camellia.ordersystem.repo.MenuItemRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MenuController {

    private final MenuItemRepository menuRepo;

    public MenuController(MenuItemRepository menuRepo) {
        this.menuRepo = menuRepo;
    }

    @GetMapping("/menu")
    public List<MenuItemEntity> menu() {
        return menuRepo.findAll();
    }
}
