package com.camellia.ordersystem.dto;

import java.math.BigDecimal;

/**
 * DTO for menu item option in create/update requests
 */
public class MenuItemOptionRequest {
    public String optionName;
    public BigDecimal optionPrice;

    public MenuItemOptionRequest() {}

    public MenuItemOptionRequest(String optionName, BigDecimal optionPrice) {
        this.optionName = optionName;
        this.optionPrice = optionPrice;
    }

    // Getters and setters for Jackson
    public String getOptionName() {
        return optionName;
    }

    public void setOptionName(String optionName) {
        this.optionName = optionName;
    }

    public BigDecimal getOptionPrice() {
        return optionPrice;
    }

    public void setOptionPrice(BigDecimal optionPrice) {
        this.optionPrice = optionPrice;
    }
}
