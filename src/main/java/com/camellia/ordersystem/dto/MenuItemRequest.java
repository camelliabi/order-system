package com.camellia.ordersystem.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for creating/updating menu items from frontend
 */
public class MenuItemRequest {
    public String itemName;
    public BigDecimal itemPrice;
    public Boolean soldout;
    public List<MenuItemOptionRequest> options;
    public List<MenuItemNoteRequest> notes;

    public MenuItemRequest() {}

    public MenuItemRequest(String itemName, BigDecimal itemPrice, Boolean soldout) {
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.soldout = soldout;
    }

    // Getters and setters for Jackson
    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public BigDecimal getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(BigDecimal itemPrice) {
        this.itemPrice = itemPrice;
    }

    public Boolean getSoldout() {
        return soldout;
    }

    public void setSoldout(Boolean soldout) {
        this.soldout = soldout;
    }

    public List<MenuItemOptionRequest> getOptions() {
        return options;
    }

    public void setOptions(List<MenuItemOptionRequest> options) {
        this.options = options;
    }

    public List<MenuItemNoteRequest> getNotes() {
        return notes;
    }

    public void setNotes(List<MenuItemNoteRequest> notes) {
        this.notes = notes;
    }
}

