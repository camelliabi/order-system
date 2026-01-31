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

        for (OrderEntity oe : orders) {
            OrderResponseDto dto = new OrderResponseDto();
            dto.orderId = oe.getOrderId();
            dto.tableId = oe.getTableId();
            dto.totalPrice = oe.getTotalPrice();
            dto.orderStatus = oe.getOrderStatus();
            dto.createdAt = oe.getCreatedAt();

            for (OrderItemEntity oie : oe.getOrderItems()) {
                OrderResponseDto.OrderItemResponseDto itemDto = new OrderResponseDto.OrderItemResponseDto();
                MenuItemEntity mi = oie.getMenuItem();
                if (mi != null) {
                    itemDto.menuItemId = mi.getItemId();
                    itemDto.itemName = mi.getItemName();
                    itemDto.unitPrice = mi.getItemPrice();
                }
                itemDto.quantity = oie.getQuantity();
                itemDto.chosenOption = oie.getChosenOption();
                itemDto.notesText = oie.getNotesText();
                itemDto.customerName = oie.getCustomerName();
                dto.orderItems.add(itemDto);
            }

            out.add(dto);
        }

        return out;
    }
}
