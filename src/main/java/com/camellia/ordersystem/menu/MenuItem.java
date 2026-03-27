package com.camellia.ordersystem.menu;

import java.util.HashMap;
import java.util.Map;

public class MenuItem {
	
	private int itemId;
	private String itemName;
	private String itemDesc;
	private double itemPrice;
	private boolean isSoldout;
	//picture for item
	private String itemPictureUrl;

	Map<String, Double> options = new HashMap<>();
	Map<String, Double> notes = new HashMap<>();
	
	
	public MenuItem() {
		// FIX: Ensure maps are initialized
		this.options = new HashMap<>();
		this.notes = new HashMap<>();
	}

	public MenuItem(int itemId, String itemName, double itemPrice) {
		// FIX: Add validation for price
		if (itemPrice < 0) {
			throw new IllegalArgumentException("Item price cannot be negative: " + itemPrice);
		}
		
		this.itemId = itemId;
		this.itemName = itemName;	
		this.itemPrice = itemPrice;
		// FIX: Ensure maps are initialized
		this.options = new HashMap<>();
		this.notes = new HashMap<>();
	}

	public MenuItem(int itemId, String itemName, double itemPrice, Map<String, Double> options, Map<String, Double> notes) {
		// FIX: Add validation for price
		if (itemPrice < 0) {
			throw new IllegalArgumentException("Item price cannot be negative: " + itemPrice);
		}
		
		this.itemId = itemId;
		this.itemName = itemName;	
		this.itemPrice = itemPrice;
		
		// FIX: Defensive copy and null check for options
		if (options != null) {
			this.options = new HashMap<>(options);
		} else {
			this.options = new HashMap<>();
		}
		
		// FIX: Defensive copy and null check for notes
		if (notes != null) {
			this.notes = new HashMap<>(notes);
		} else {
			this.notes = new HashMap<>();
		}
	}
	
	//getters and setters
	
	public int getItemId() {
		return itemId;
	}
	
	public String getItemName() {
		return itemName;
	}
	
	public String getItemDesc() {
		return itemDesc;
	}
	
	public double getItemPrice() {
		return itemPrice;
	}
	
	public boolean isSoldout() {
		return isSoldout;
	}
	
	public void setItemId(int id) {
		this.itemId = id;
	}
	
	public void setItemDesc(String desc) {
		this.itemDesc = desc;
	}
	
	public void setItemPrice(double price) {
		// FIX: Add validation for negative price
		if (price < 0) {
			throw new IllegalArgumentException("Item price cannot be negative: " + price);
		}
		this.itemPrice = price;
	}
	
	public void setSoldout(boolean soldout) {
		this.isSoldout = soldout;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getItemPictureUrl() {
		return itemPictureUrl;
	}

	public void setItemPictureUrl(String itemPictureUrl) {
		this.itemPictureUrl = itemPictureUrl;
	}

	public Map<String, Double> getOptions() {
		// FIX: Return defensive copy to prevent external modification
		return options != null ? new HashMap<>(options) : new HashMap<>();
	}
	
	public void setOptions(Map<String, Double> options) {
		// FIX: Defensive copy and null check
		if (options != null) {
			this.options = new HashMap<>(options);
		} else {
			this.options = new HashMap<>();
		}
	}
	
	public Map<String, Double> getNotes() {
		// FIX: Return defensive copy to prevent external modification
		return notes != null ? new HashMap<>(notes) : new HashMap<>();
	}
	
	public void setNotes(Map<String, Double> notes) {
		// FIX: Defensive copy and null check
		if (notes != null) {
			this.notes = new HashMap<>(notes);
		} else {
			this.notes = new HashMap<>();
		}
	}
	
	public void addOption(String optionName, double optionPrice) {
		// FIX: Add validation for option name and price
		if (optionName == null || optionName.trim().isEmpty()) {
			throw new IllegalArgumentException("Option name cannot be null or empty");
		}
		if (optionPrice < 0) {
			throw new IllegalArgumentException("Option price cannot be negative: " + optionPrice);
		}
		
		// FIX: Ensure options map is initialized
		if (this.options == null) {
			this.options = new HashMap<>();
		}
		
		this.options.put(optionName.trim(), optionPrice);
	}
	
	public void addNote(String noteName, double notePrice) {
		// FIX: Add validation for note name and price
		if (noteName == null || noteName.trim().isEmpty()) {
			throw new IllegalArgumentException("Note name cannot be null or empty");
		}
		if (notePrice < 0) {
			throw new IllegalArgumentException("Note price cannot be negative: " + notePrice);
		}
		
		// FIX: Ensure notes map is initialized
		if (this.notes == null) {
			this.notes = new HashMap<>();
		}
		
		this.notes.put(noteName.trim(), notePrice);
	}
}
