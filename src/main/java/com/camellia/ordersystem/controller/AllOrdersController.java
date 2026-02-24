package com.camellia.ordersystem.controller;

import com.camellia.ordersystem.entity.OrderEntity;
import com.camellia.ordersystem.entity.OrderItemEntity;
import com.camellia.ordersystem.entity.MenuItemEntity;
import com.camellia.ordersystem.repo.OrderRepository;
import com.camellia.ordersystem.dto.OrderResponseDto;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AllOrdersController {

    private final OrderRepository orderRepo;
    private static final Logger logger = LoggerFactory.getLogger(AllOrdersController.class);

    public AllOrdersController(OrderRepository orderRepo) {
        this.orderRepo = orderRepo;
    }

    @GetMapping("/all_orders")
    public List<OrderResponseDto> allOrders() {
        // Return DTOs to avoid exposing JPA entities and to include menu item details
        List<OrderEntity> orders = orderRepo.findAll();
        List<OrderResponseDto> out = new java.util.ArrayList<>();
        
        // FIX: Add null check for orders list
        if (orders == null) {
            logger.warn("Orders list is null from repository");
            return out; // Return empty list
        }

        for (OrderEntity oe : orders) {
            // FIX: Add null check for individual order entity
            if (oe == null) {
                logger.warn("Skipping null order entity");
                continue;
            }
            
            try {
                OrderResponseDto dto = new OrderResponseDto();
                dto.orderId = oe.getOrderId();
                dto.tableId = oe.getTableId();
                dto.totalPrice = oe.getTotalPrice();
                dto.orderStatus = oe.getOrderStatus();
                dto.createdAt = oe.getCreatedAt();
                
                // FIX: Add null check for order items list
                List<OrderItemEntity> orderItems = oe.getOrderItems();
                if (orderItems != null) {
                    for (OrderItemEntity oie : orderItems) {
                        // FIX: Add null check for individual order item
                        if (oie == null) {
                            logger.warn("Skipping null order item for orderId={}", oe.getOrderId());
                            continue;
                        }
                        
                        try {
                            OrderResponseDto.OrderItemResponseDto itemDto = new OrderResponseDto.OrderItemResponseDto();
                            
                            // FIX: Add null check before accessing menu item
                            MenuItemEntity mi = oie.getMenuItem();
                            if (mi != null) {
                                itemDto.menuItemId = mi.getItemId();
                                itemDto.itemName = mi.getItemName();
                            } else {
                                logger.warn("MenuItem is null for orderItemId in orderId={}", oe.getOrderId());
                                // Set default values to prevent null fields
                                itemDto.menuItemId = null;
                                itemDto.itemName = "Unknown Item";
                            }
                            
                            // FIXED: Use the stored unit price (which includes option and notes)
                            // instead of the base menu item price
                            itemDto.unitPrice = oie.getUnitPrice();
                            
                            itemDto.quantity = oie.getQuantity();
                            itemDto.chosenOption = oie.getChosenOption();
                            itemDto.notesText = oie.getNotesText();
                            itemDto.customerName = oie.getCustomerName();
                            
                            dto.orderItems.add(itemDto);
                        } catch (Exception e) {
                            logger.error("Error processing order item for orderId={}: {}", oe.getOrderId(), e.getMessage());
                            // Continue processing other items
                        }
                    }
                }

                out.add(dto);
            } catch (Exception e) {
                logger.error("Error processing order with orderId={}: {}", oe.getOrderId(), e.getMessage());
                // Continue processing other orders
            }
        }

        return out;
    }
}
