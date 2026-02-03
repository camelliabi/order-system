package com.camellia.ordersystem.dto;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Data Transfer Object for menu items.
 * FIXED: All fields now properly encapsulated with consistent getters/setters.
 */
public class MenuItemDTO {
    // FIXED: Made all fields private for proper encapsulation
    private Integer itemId;
    private String itemName;
    private BigDecimal itemPrice;
    private boolean soldout;  // FIXED: Consistent boolean type
    private Map<String, BigDecimal> options;
    private Map<String, BigDecimal> notes;

    public MenuItemDTO(){}

    // FIXED: Added missing getters for JavaBeans compliance
    public Integer getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public BigDecimal getItemPrice() {
        return itemPrice;
    }

    public boolean isSoldout() {  // FIXED: boolean getter uses "is" prefix
        return soldout;
    }

    public Map<String, BigDecimal> getOptions() {
        return options;
    }

    public Map<String, BigDecimal> getNotes() {
        return notes;
    }

    // Setters (maintaining compatibility)
    public void setItemId(Integer id) {  // FIXED: Changed to Integer for consistency
        this.itemId = id;
    }

    public void setItemName(String name) {
        this.itemName = name;
    }

    public void setItemPrice(BigDecimal price) {
        this.itemPrice = price;
    }

    public void setSoldout(boolean soldout) {  // FIXED: Changed to boolean for consistency
        this.soldout = soldout;
    }

    public void setOptions(Map<String, BigDecimal> options) {
        this.options = options;
    }

    public void setNotes(Map<String, BigDecimal> notes) {
        this.notes = notes;
    }
}
