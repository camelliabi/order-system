package com.camellia.ordersystem.controller;

import com.camellia.ordersystem.dto.MenuItemDTO;
import com.camellia.ordersystem.repo.MenuItemRepository;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class MenuController {

    private final MenuItemRepository menuRepo;

    public MenuController(MenuItemRepository menuRepo) {
        this.menuRepo = menuRepo;
    }

    @GetMapping("/menu")
    public List<MenuItemDTO> menu() {

        return menuRepo.findAll().stream().map(item -> {
            MenuItemDTO dto = new MenuItemDTO();
            dto.itemId = item.getItemId();
            dto.itemName = item.getItemName();
            dto.itemPrice = item.getItemPrice();
            dto.setSoldout(item.getSoldout());

            dto.options = item.getOptions() == null ? Map.of()
                : item.getOptions().stream()
                    .collect(Collectors.toMap(
                        o -> o.getOptionName(),
                        o -> o.getOptionPrice()
                    ));

            dto.notes = item.getNotes() == null ? Map.of()
                : item.getNotes().stream()
                    .collect(Collectors.toMap(
                        n -> n.getNoteName(),
                        n -> n.getNotePrice()
                    ));

            return dto;
        }).toList();
    }
}
