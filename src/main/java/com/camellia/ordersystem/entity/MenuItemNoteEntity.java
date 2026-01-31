package com.camellia.ordersystem.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "menu_item_note")
public class MenuItemNoteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "note_id")
    private Integer noteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private MenuItemEntity menuItem;

    @Column(name = "note_name", nullable = false)
    private String noteName;

    @Column(name = "note_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal notePrice;

    public Integer getNoteId() { return noteId; }

    public MenuItemEntity getMenuItem() { return menuItem; }
    public void setMenuItem(MenuItemEntity menuItem) { this.menuItem = menuItem; }

    public String getNoteName() { return noteName; }
    public void setNoteName(String noteName) { this.noteName = noteName; }

    public BigDecimal getNotePrice() { return notePrice; }
    public void setNotePrice(BigDecimal notePrice) { this.notePrice = notePrice; }
}
