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
            // FIXED: Use setters instead of direct field access
            dto.setItemId(item.getItemId());
            dto.setItemName(item.getItemName());
            dto.setItemPrice(item.getItemPrice());
            dto.setSoldout(item.getSoldout());

            // FIXED: Use setters for collections too
            dto.setOptions(item.getOptions() == null ? Map.of()
                : item.getOptions().stream()
                    .collect(Collectors.toMap(
                        o -> o.getOptionName(),
                        o -> o.getOptionPrice()
                    )));

            dto.setNotes(item.getNotes() == null ? Map.of()
                : item.getNotes().stream()
                    .collect(Collectors.toMap(
                        n -> n.getNoteName(),
                        n -> n.getNotePrice()
                    )));

            return dto;
        }).toList();
    }
}
