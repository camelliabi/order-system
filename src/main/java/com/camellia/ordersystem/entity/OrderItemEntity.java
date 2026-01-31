package com.camellia.ordersystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

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

    public void setOrder(OrderEntity order) { this.order = order; }
    public void setMenuItem(MenuItemEntity menuItem) { this.menuItem = menuItem; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setChosenOption(String chosenOption) { this.chosenOption = chosenOption; }
    public void setNotesText(String notesText) { this.notesText = notesText; }

    public MenuItemEntity getMenuItem() { return menuItem; }
    public Integer getQuantity() { return quantity; }
    public String getCustomerName() { return customerName; }
    public String getChosenOption() { return chosenOption; }
    public String getNotesText() { return notesText; }
}
