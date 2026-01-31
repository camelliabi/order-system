package com.camellia.ordersystem.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "menu_item")
public class MenuItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Integer itemId;

    @Column(name = "item_name", nullable = false)
    private String itemName;

    @Column(name = "item_desc")
    private String itemDesc;

    @Column(name = "item_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal itemPrice;

    @Column(name = "soldout", nullable = false)
    private Boolean soldout;

    @Column(name = "item_picture_url")
    private String itemPictureUrl;

    @OneToMany(mappedBy = "menuItem", fetch = FetchType.LAZY)
    private java.util.List<MenuItemOptionEntity> options;
    
    @OneToMany(mappedBy = "menuItem", fetch = FetchType.LAZY)
    private java.util.List<MenuItemNoteEntity> notes;

    public MenuItemEntity() {}

    // getters/setters
    public Integer getItemId() { return itemId; }
    public void setItemId(Integer itemId) { this.itemId = itemId; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getItemDesc() { return itemDesc; }
    public void setItemDesc(String itemDesc) { this.itemDesc = itemDesc; }

    public BigDecimal getItemPrice() { return itemPrice; }
    public void setItemPrice(BigDecimal itemPrice) { this.itemPrice = itemPrice; }

    public Boolean getSoldout() { return soldout; }
    public void setSoldout(Boolean soldout) { this.soldout = soldout; }

    public String getItemPictureUrl() { return itemPictureUrl; }
    public void setItemPictureUrl(String itemPictureUrl) { this.itemPictureUrl = itemPictureUrl; }

    public java.util.List<MenuItemOptionEntity> getOptions() {
    return options;
    }
    
    public java.util.List<MenuItemNoteEntity> getNotes() {
    return notes;
    }
}
