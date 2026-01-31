package com.camellia.ordersystem.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="orders")
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="order_id")
    private Integer orderId;

    @Column(name="table_id", nullable=false)
    private String tableId;

    @Column(name="total_price", nullable=false, precision=10, scale=2)
    private BigDecimal totalPrice = BigDecimal.ZERO;

    @Column(name="order_status", nullable=false)
    private String orderStatus = "NEW";

    @Column(name="created_at", nullable=false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy="order", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<OrderItemEntity> orderItems = new ArrayList<>();

    @PrePersist
    void onCreate() { createdAt = LocalDateTime.now(); }

    public void addItem(OrderItemEntity it) {
        it.setOrder(this);
        orderItems.add(it);
    }

    public Integer getOrderId() { return orderId; }
    public String getTableId() { return tableId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setTableId(String tableId) { this.tableId = tableId; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }
    public List<OrderItemEntity> getOrderItems() { return orderItems; }
}
