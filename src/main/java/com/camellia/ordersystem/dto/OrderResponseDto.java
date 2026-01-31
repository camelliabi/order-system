package com.camellia.ordersystem.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderResponseDto {
    public Integer orderId;
    public String tableId;
    public BigDecimal totalPrice;
    public String orderStatus;
    public LocalDateTime createdAt;
    public List<OrderItemResponseDto> orderItems = new ArrayList<>();

    public static class OrderItemResponseDto {
        public Integer menuItemId;
        public String itemName;
        public BigDecimal unitPrice;
        public Integer quantity;
        public String chosenOption;
        public String notesText;
        public String customerName;
    }
}
