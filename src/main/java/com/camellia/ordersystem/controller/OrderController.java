package com.camellia.ordersystem.controller;

import com.camellia.ordersystem.dto.CreateOrderRequest;
import com.camellia.ordersystem.entity.OrderEntity;
import com.camellia.ordersystem.entity.OrderItemEntity;
import com.camellia.ordersystem.entity.MenuItemEntity;
import com.camellia.ordersystem.repo.MenuItemRepository;
import com.camellia.ordersystem.repo.OrderRepository;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        // Log raw incoming request JSON and per-item details for debugging
        try {
            ObjectMapper mapper = new ObjectMapper();
            logger.info("RAW REQ JSON: {}", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(req));
        } catch (Exception e) {
            logger.warn("Failed to serialize incoming CreateOrderRequest", e);
        }

        if (req.items != null) {
            for (CreateOrderRequest.CreateOrderItem itLog : req.items) {
                logger.info("REQ ITEM menuItemId={}, chosenOption={}, notes={}, notesText={}",
                        itLog.menuItemId, itLog.chosenOption, itLog.notes, itLog.notesText);
            }
        }
        // VALIDATION: items list must not be empty
        if (req.items == null || req.items.isEmpty()) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST,
                    "Order items cannot be empty"
            );
        }

        OrderEntity order = new OrderEntity();
        order.setTableId(req.tableId);
        order.setOrderStatus("NEW");

        BigDecimal total = BigDecimal.ZERO;

        for (CreateOrderRequest.CreateOrderItem it : req.items) {
            // VALIDATION: menuItemId must not be null
            if (it.menuItemId == null) {
                throw new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.BAD_REQUEST,
                        "menuItemId is required for each order item"
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

            OrderItemEntity oi = new OrderItemEntity();
            oi.setMenuItem(menuItem);
            oi.setQuantity(it.quantity);
            oi.setCustomerName(it.customerName);
            oi.setChosenOption(it.chosenOption);

            // Normalize notes via DTO helper (accepts notes array or notesText)
            String notesTextToStore = it.normalizedNotesText();
            logger.debug("Normalized notes for menuItemId={}: {}", menuItemId, notesTextToStore);
            if (notesTextToStore != null) {
                oi.setNotesText(notesTextToStore);
            }

            order.addItem(oi);

            // MVP 总价：先用 base price * qty
           total = total.add(menuItem.getItemPrice().multiply(BigDecimal.valueOf(it.quantity)));
           
        }

       order.setTotalPrice(total);

        return orderRepo.save(order);
    }
}

