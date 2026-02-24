package com.camellia.ordersystem.controller;

import com.camellia.ordersystem.entity.OrderEntity;
import com.camellia.ordersystem.entity.OrderItemEntity;
import com.camellia.ordersystem.entity.MenuItemEntity;
import com.camellia.ordersystem.repo.OrderRepository;
import com.camellia.ordersystem.dto.OrderResponseDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AllOrdersController {

    private final OrderRepository orderRepo;

    public AllOrdersController(OrderRepository orderRepo) {
        this.orderRepo = orderRepo;
    }

    @GetMapping("/all_orders")
    public List<OrderResponseDto> allOrders() {
        // Return DTOs to avoid exposing JPA entities and to include menu item details
        List<OrderEntity> orders = orderRepo.findAll();
        List<OrderResponseDto> out = new java.util.ArrayList<>();
        
        // FIX: Add null check for orders list (prevent NoSuchElementException)
        if (orders == null || orders.isEmpty()) {
            return out; // Return empty list instead of null
        }

        for (OrderEntity oe : orders) {
            // FIX: Add null check for individual OrderEntity (prevent NullPointerException)
            if (oe == null) {
                continue; // Skip null orders
            }
            
            OrderResponseDto dto = new OrderResponseDto();
            dto.orderId = oe.getOrderId();
            dto.tableId = oe.getTableId();
            dto.totalPrice = oe.getTotalPrice();
            dto.orderStatus = oe.getOrderStatus();
            dto.createdAt = oe.getCreatedAt();
            
            // FIX: Add null check for orderItems list (prevent NoSuchElementException)
            List<OrderItemEntity> orderItems = oe.getOrderItems();
            if (orderItems != null && !orderItems.isEmpty()) {
                for (OrderItemEntity oie : orderItems) {
                    // FIX: Add null check for individual OrderItemEntity
                    if (oie == null) {
                        continue; // Skip null items
                    }
                    
                    OrderResponseDto.OrderItemResponseDto itemDto = new OrderResponseDto.OrderItemResponseDto();
                    MenuItemEntity mi = oie.getMenuItem();
                    if (mi != null) {
                        itemDto.menuItemId = mi.getItemId();
                        itemDto.itemName = mi.getItemName();
                    }
                    
                    // FIXED: Use the stored unit price (which includes option and notes)
                    // instead of the base menu item price
                    itemDto.unitPrice = oie.getUnitPrice();
                    
                    itemDto.quantity = oie.getQuantity();
                    itemDto.chosenOption = oie.getChosenOption();
                    itemDto.notesText = oie.getNotesText();
                    itemDto.customerName = oie.getCustomerName();
                    dto.orderItems.add(itemDto);
                }
            }

            out.add(dto);
        }

        return out;
    }
}
