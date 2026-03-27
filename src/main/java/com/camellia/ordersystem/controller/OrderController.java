package com.camellia.ordersystem.controller;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import com.camellia.ordersystem.dto.CreateOrderRequest;
import com.camellia.ordersystem.entity.MenuItemEntity;
import com.camellia.ordersystem.entity.MenuItemNoteEntity;
import com.camellia.ordersystem.entity.MenuItemOptionEntity;
import com.camellia.ordersystem.entity.OrderEntity;
import com.camellia.ordersystem.entity.OrderItemEntity;
import com.camellia.ordersystem.repo.MenuItemRepository;
import com.camellia.ordersystem.repo.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api")
public class OrderController {

    private final OrderRepository orderRepo;
    private final MenuItemRepository menuRepo;
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    public OrderController(OrderRepository orderRepo, MenuItemRepository menuRepo) {
        this.orderRepo = orderRepo;
        this.menuRepo = menuRepo;
    }

    @PostMapping("/orders")
    public OrderEntity createOrder(@RequestBody CreateOrderRequest req) {
        // FIX: Add null check for request body
        if (req == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST,
                    "Request body cannot be null"
            );
        }
        
        // Log raw incoming request JSON and per-item details for debugging
        try {
            ObjectMapper mapper = new ObjectMapper();
            logger.info("RAW REQ JSON: {}", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(req));
        } catch (Exception e) {
            logger.warn("Failed to serialize incoming CreateOrderRequest", e);
        }

        if (req.items != null) {
            for (CreateOrderRequest.CreateOrderItem itLog : req.items) {
                // FIX: Add null check for individual items in logging
                if (itLog != null) {
                    logger.info("REQ ITEM menuItemId={}, chosenOption={}, notes={}, notesText={}",
                            itLog.menuItemId, itLog.chosenOption, itLog.notes, itLog.notesText);
                }
            }
        }
        
        // VALIDATION: items list must not be empty
        if (req.items == null || req.items.isEmpty()) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST,
                    "Order items cannot be empty"
            );
        }
        
        // FIX: Add validation for tableId
        if (req.tableId == null || req.tableId.trim().isEmpty()) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST,
                    "Table ID is required"
            );
        }

        OrderEntity order = new OrderEntity();
        order.setTableId(req.tableId.trim());
        order.setOrderStatus("NEW");

        BigDecimal total = BigDecimal.ZERO;

        for (CreateOrderRequest.CreateOrderItem it : req.items) {
            // FIX: Add null check for individual order item
            if (it == null) {
                logger.warn("Skipping null order item");
                continue;
            }
            
            // VALIDATION: menuItemId must not be null
            if (it.menuItemId == null) {
                throw new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.BAD_REQUEST,
                        "menuItemId is required for each order item"
                );
            }
            
            // FIX: Add validation for quantity
            if (it.quantity == null || it.quantity <= 0) {
                throw new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.BAD_REQUEST,
                        "Quantity must be a positive number for menuItemId: " + it.menuItemId
                );
            }

            // Use Integer id for repository lookup
            Integer menuItemId = it.menuItemId;

            // Fetch menu item with 400 error if not found (not 500)
            MenuItemEntity menuItem = menuRepo.findById(menuItemId)
                    .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                            org.springframework.http.HttpStatus.BAD_REQUEST,
                            "Menu item not found: " + menuItemId
                    ));
            
            // FIX: Additional validation after fetching menu item
            if (menuItem == null) {
                throw new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR,
                        "Menu item data is corrupted for ID: " + menuItemId
                );
            }

            OrderItemEntity oi = new OrderItemEntity();
            oi.setMenuItem(menuItem);
            oi.setQuantity(it.quantity);
            oi.setCustomerName(it.customerName);
            oi.setChosenOption(it.chosenOption);

            // Normalize notes via DTO helper (accepts notes array or notesText)
            String notesTextToStore = null;
            try {
                notesTextToStore = it.normalizedNotesText();
            } catch (Exception e) {
                logger.warn("Failed to normalize notes for menuItemId={}: {}", menuItemId, e.getMessage());
            }
            
            logger.debug("Normalized notes for menuItemId={}: {}", menuItemId, notesTextToStore);
            if (notesTextToStore != null && !notesTextToStore.trim().isEmpty()) {
                oi.setNotesText(notesTextToStore.trim());
            }

            // FIXED: Calculate actual unit price including option and notes
            BigDecimal unitPrice = calculateUnitPrice(menuItem, it.chosenOption, notesTextToStore);
            
            // FIX: Validate unit price is not null or negative
            if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0) {
                logger.error("Invalid unit price calculated for menuItemId={}: {}", menuItemId, unitPrice);
                unitPrice = BigDecimal.ZERO;
            }
            
            oi.setUnitPrice(unitPrice);
            
            logger.info("Item pricing: menuItemId={}, basePrice={}, chosenOption={}, notes={}, calculatedUnitPrice={}", 
                    menuItemId, menuItem.getItemPrice(), it.chosenOption, notesTextToStore, unitPrice);

            order.addItem(oi);

            // Calculate total using the actual unit price
            // FIX: Add null check and validation before BigDecimal operations
            try {
                BigDecimal itemTotal = unitPrice.multiply(BigDecimal.valueOf(it.quantity));
                total = total.add(itemTotal);
            } catch (ArithmeticException e) {
                logger.error("Arithmetic error calculating total for menuItemId={}: {}", menuItemId, e.getMessage());
                throw new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR,
                        "Error calculating order total"
                );
            }
        }

        order.setTotalPrice(total);

        return orderRepo.save(order);
    }

    // @PatchMapping("/orders/{orderId}")
    // public OrderEntity updateOrderStatus(@PathVariable Integer orderId, @RequestBody String newStatus) {
    //     OrderEntity order = orderRepo.findById(orderId)
    //             .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
    //                     org.springframework.http.HttpStatus.NOT_FOUND,
    //                     "Order not found: " + orderId
    //             ));
    //     order.setOrderStatus(newStatus);
    //     return orderRepo.save(order);
    // }

    /**
     * Calculate the actual unit price for an order item.
     * Price = chosen option price (or base price if no option) + sum of all note prices
     */
    private BigDecimal calculateUnitPrice(MenuItemEntity menuItem, String chosenOption, String notesText) {
        // FIX: Add null check for menuItem
        if (menuItem == null) {
            logger.error("MenuItem is null in calculateUnitPrice");
            return BigDecimal.ZERO;
        }
        
        BigDecimal price = BigDecimal.ZERO;
        
        // FIX: Add null check for base price
        BigDecimal basePrice = menuItem.getItemPrice();
        if (basePrice == null) {
            logger.warn("Base price is null for menuItemId={}", menuItem.getItemId());
            basePrice = BigDecimal.ZERO;
        }

        // 1. Get the option price (if option is chosen, use its price; otherwise use base price)
        if (chosenOption != null && !chosenOption.trim().isEmpty()) {
            String trimmedOption = chosenOption.trim();
            
            // FIX: Add null check for options list
            List<MenuItemOptionEntity> options = menuItem.getOptions();
            if (options != null && !options.isEmpty()) {
                try {
                    BigDecimal optionPrice = options.stream()
                            .filter(opt -> opt != null && opt.getOptionName() != null) // FIX: Filter out null options
                            .filter(opt -> opt.getOptionName().equals(trimmedOption))
                            .findFirst()
                            .map(MenuItemOptionEntity::getOptionPrice)
                            .orElse(basePrice); // Fallback to base price if option not found
                    
                    // FIX: Validate option price is not null
                    if (optionPrice != null) {
                        price = price.add(optionPrice);
                        logger.debug("Found option '{}' with price: {}", trimmedOption, optionPrice);
                    } else {
                        price = price.add(basePrice);
                        logger.warn("Option price is null for option '{}', using base price", trimmedOption);
                    }
                } catch (Exception e) {
                    logger.error("Error finding option '{}': {}", trimmedOption, e.getMessage());
                    price = price.add(basePrice);
                }
            } else {
                // No options available, use base price
                price = price.add(basePrice);
                logger.debug("No options available for menuItemId={}, using base price", menuItem.getItemId());
            }
        } else {
            // No option chosen, use base price
            price = price.add(basePrice);
        }

        // 2. Add prices for all selected notes
        if (notesText != null && !notesText.trim().isEmpty()) {
            // FIX: Add null check for notes list
            List<MenuItemNoteEntity> notes = menuItem.getNotes();
            if (notes != null && !notes.isEmpty()) {
                // Split notes by comma (as they're stored as "note1, note2, note3")
                String[] noteNames = notesText.split(",");
                
                // FIX: Add null check for split result
                if (noteNames != null) {
                    for (String noteName : noteNames) {
                        // FIX: Add null check for individual note name
                        if (noteName == null) {
                            continue;
                        }
                        
                        String trimmedNoteName = noteName.trim();
                        if (!trimmedNoteName.isEmpty()) {
                            try {
                                // Find matching note and add its price
                                BigDecimal notePrice = notes.stream()
                                        .filter(note -> note != null && note.getNoteName() != null) // FIX: Filter out null notes
                                        .filter(note -> note.getNoteName().equals(trimmedNoteName))
                                        .findFirst()
                                        .map(MenuItemNoteEntity::getNotePrice)
                                        .orElse(BigDecimal.ZERO);
                                
                                // FIX: Validate note price
                                if (notePrice != null && notePrice.compareTo(BigDecimal.ZERO) > 0) {
                                    price = price.add(notePrice);
                                    logger.debug("Found note '{}' with price: {}", trimmedNoteName, notePrice);
                                }
                            } catch (Exception e) {
                                logger.error("Error processing note '{}': {}", trimmedNoteName, e.getMessage());
                            }
                        }
                    }
                }
            } else {
                logger.debug("No notes available for menuItemId={}", menuItem.getItemId());
            }
        }

        return price;
    }
}

