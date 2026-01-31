package com.camellia.ordersystem.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "menu_item_option")
public class MenuItemOptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_id")
    private Integer optionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private MenuItemEntity menuItem;

    @Column(name = "option_name", nullable = false)
    private String optionName;

    @Column(name = "option_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal optionPrice;

    public Integer getOptionId() { return optionId; }

    public MenuItemEntity getMenuItem() { return menuItem; }
    public void setMenuItem(MenuItemEntity menuItem) { this.menuItem = menuItem; }

    public String getOptionName() { return optionName; }
    public void setOptionName(String optionName) { this.optionName = optionName; }

    public BigDecimal getOptionPrice() { return optionPrice; }
    public void setOptionPrice(BigDecimal optionPrice) { this.optionPrice = optionPrice; }
}
