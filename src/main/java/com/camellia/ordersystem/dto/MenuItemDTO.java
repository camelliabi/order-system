package com.camellia.ordersystem.dto;

import java.math.BigDecimal;
import java.util.Map;

public class MenuItemDTO {
    public Integer itemId;
    public String itemName;
    public BigDecimal itemPrice;
    private boolean soldout;

    public Map<String, BigDecimal> options;
    public Map<String, BigDecimal> notes;

    public MenuItemDTO(){

    }

    //getters and setters
    public void setItemId(int id){
        itemId = id;
    }

    public void setItemName(String name){
        itemName = name;
    }

    public void setItemPrice(BigDecimal price){
        itemPrice = price;
    }

    public void setSoldout(Boolean soldout) {
        this.soldout = soldout;
    }

    public Boolean getSoldout() {
        return soldout;
    }


    public void setOptions(Map<String, BigDecimal> opt){
        this.options = opt;

    }

    public void setNotes(Map<String, BigDecimal> nts){
        this.notes = nts;
    }
}

