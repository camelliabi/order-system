package com.camellia.ordersystem.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.camellia.ordersystem.dto.MenuItemDTO;
import com.camellia.ordersystem.dto.MenuItemRequest;
import com.camellia.ordersystem.entity.MenuItemEntity;
import com.camellia.ordersystem.entity.MenuItemNoteEntity;
import com.camellia.ordersystem.entity.MenuItemOptionEntity;
import com.camellia.ordersystem.repo.MenuItemRepository;
import com.camellia.ordersystem.repo.OrderRepository;
import com.camellia.ordersystem.repo.OrderItemRepository;


@RestController
@RequestMapping("/api")
public class MenuController {

    private final MenuItemRepository menuRepo;
    private final OrderItemRepository orderItemRepo;


    public MenuController(MenuItemRepository menuRepo, OrderItemRepository orderItemRepo) {
        this.menuRepo = menuRepo;
        this.orderItemRepo = orderItemRepo;
    }

    @GetMapping("/menu")
    public List<MenuItemDTO> menu() {

        return menuRepo.findAll().stream().map(item -> {
            MenuItemDTO dto = new MenuItemDTO();
            dto.itemId = item.getItemId();
            dto.itemName = item.getItemName();
            dto.itemPrice = item.getItemPrice();
            dto.setSoldout(item.getSoldout());

            if (item.getOptions() == null) {
                dto.options = Map.of();
            } else {
                dto.options = item.getOptions().stream()
                    .collect(Collectors.toMap(
                        o -> o.getOptionName(),
                        o -> o.getOptionPrice()
                    ));
            }

            if (item.getNotes() == null) {
                dto.notes = Map.of();
            } else {
                dto.notes = item.getNotes().stream()
                    .collect(Collectors.toMap(
                        n -> n.getNoteName(),
                        n -> n.getNotePrice()
                    ));
            }

            return dto;
        }).toList();
    }

    /**
     * Create a new menu item with options and notes
     */
    @Transactional
    @PostMapping("/menu")
    public ResponseEntity<MenuItemDTO> createMenuItem(@RequestBody MenuItemRequest request) {
        try {
            MenuItemEntity item = new MenuItemEntity();
            item.setItemName(request.itemName);
            item.setItemPrice(request.itemPrice);
            item.setSoldout(request.soldout != null ? request.soldout : false);

            // populate options/note lists via cascade
            if (request.options != null && !request.options.isEmpty()) {
                List<MenuItemOptionEntity> options = request.options.stream()
                    .map(opt -> {
                        MenuItemOptionEntity optionEntity = new MenuItemOptionEntity();
                        optionEntity.setMenuItem(item);
                        optionEntity.setOptionName(opt.optionName);
                        optionEntity.setOptionPrice(opt.optionPrice);
                        return optionEntity;
                    })
                    .toList();
                item.getOptions().addAll(options);
            }
            if (request.notes != null && !request.notes.isEmpty()) {
                List<MenuItemNoteEntity> notes = request.notes.stream()
                    .map(note -> {
                        MenuItemNoteEntity noteEntity = new MenuItemNoteEntity();
                        noteEntity.setMenuItem(item);
                        noteEntity.setNoteName(note.noteName);
                        noteEntity.setNotePrice(note.notePrice);
                        return noteEntity;
                    })
                    .toList();
                item.getNotes().addAll(notes);
            }

            MenuItemEntity savedItem = menuRepo.save(item);

            // Fetch the saved item with options/notes loaded
            MenuItemEntity createdItem = menuRepo.findById(savedItem.getItemId()).get();
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(createdItem));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Delete a menu item by ID, along with its options and notes via cascade
     */
    @Transactional
    @DeleteMapping("/menu/{id}")
    public ResponseEntity<?> deleteMenuItem(@PathVariable Integer id) {
        try {
            Optional<MenuItemEntity> optionalItem = menuRepo.findById(id);
            if (optionalItem.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            if (orderItemRepo.existsByMenuItem_ItemId(id)) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("This item is used in existing orders and cannot be deleted");
            }

            menuRepo.delete(optionalItem.get());
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
       
    /**
     * Update an existing menu item with options and notes
     */
    @Transactional
    @PutMapping("/menu/{id}")
    public ResponseEntity<MenuItemDTO> updateMenuItem(@PathVariable Integer id, @RequestBody MenuItemRequest request) {
        try {
            Optional<MenuItemEntity> optionalItem = menuRepo.findById(id);
            if (optionalItem.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            MenuItemEntity item = optionalItem.get();
            item.setItemName(request.itemName);
            item.setItemPrice(request.itemPrice);
            item.setSoldout(request.soldout != null ? request.soldout : false);

            // clear and rebuild options via cascade
            item.getOptions().clear();
            if (request.options != null && !request.options.isEmpty()) {
                List<MenuItemOptionEntity> options = request.options.stream()
                    .map(opt -> {
                        MenuItemOptionEntity optionEntity = new MenuItemOptionEntity();
                        optionEntity.setMenuItem(item);
                        optionEntity.setOptionName(opt.optionName);
                        optionEntity.setOptionPrice(opt.optionPrice);
                        return optionEntity;
                    })
                    .toList();
                item.getOptions().addAll(options);
            }

            // clear and rebuild notes via cascade
            item.getNotes().clear();
            if (request.notes != null && !request.notes.isEmpty()) {
                List<MenuItemNoteEntity> notes = request.notes.stream()
                    .map(note -> {
                        MenuItemNoteEntity noteEntity = new MenuItemNoteEntity();
                        noteEntity.setMenuItem(item);
                        noteEntity.setNoteName(note.noteName);
                        noteEntity.setNotePrice(note.notePrice);
                        return noteEntity;
                    })
                    .toList();
                item.getNotes().addAll(notes);
            }

            // persist changes; cascade will handle children
            MenuItemEntity updatedItem = menuRepo.save(item);

            return ResponseEntity.ok(convertToDTO(updatedItem));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Helper method to convert MenuItemEntity to MenuItemDTO
     */
    private MenuItemDTO convertToDTO(MenuItemEntity item) {
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
    }
}
