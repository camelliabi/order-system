package com.camellia.ordersystem.dto;

import java.math.BigDecimal;

/**
 * DTO for menu item note in create/update requests
 */
public class MenuItemNoteRequest {
    public String noteName;
    public BigDecimal notePrice;

    public MenuItemNoteRequest() {}

    public MenuItemNoteRequest(String noteName, BigDecimal notePrice) {
        this.noteName = noteName;
        this.notePrice = notePrice;
    }

    // Getters and setters for Jackson
    public String getNoteName() {
        return noteName;
    }

    public void setNoteName(String noteName) {
        this.noteName = noteName;
    }

    public BigDecimal getNotePrice() {
        return notePrice;
    }

    public void setNotePrice(BigDecimal notePrice) {
        this.notePrice = notePrice;
    }
}
