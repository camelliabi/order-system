package com.camellia.ordersystem.entity;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="order_item")
public class OrderItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="order_item_id")
    private Integer orderItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private OrderEntity order;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id")
    @JsonIgnore   
    private MenuItemEntity menuItem;

    @Column(name="quantity", nullable=false)
    private Integer quantity;

    @Column(name="customer_name")
    private String customerName;

    @Column(name="chosen_option")
    private String chosenOption;

    @Column(name="notes_text")
    private String notesText;

    // Store the calculated unit price (base + option + notes) at the time of order
    @Column(name="unit_price", precision=10, scale=2)
    private BigDecimal unitPrice;

    public void setOrder(OrderEntity order) { this.order = order; }
    public void setMenuItem(MenuItemEntity menuItem) { this.menuItem = menuItem; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setChosenOption(String chosenOption) { this.chosenOption = chosenOption; }
    public void setNotesText(String notesText) { this.notesText = notesText; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public MenuItemEntity getMenuItem() { return menuItem; }
    public Integer getQuantity() { return quantity; }
    public String getCustomerName() { return customerName; }
    public String getChosenOption() { return chosenOption; }
    public String getNotesText() { return notesText; }
    public BigDecimal getUnitPrice() { return unitPrice; }
}
