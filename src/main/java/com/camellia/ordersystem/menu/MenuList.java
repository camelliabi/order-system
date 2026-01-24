package com.camellia.ordersystem.menu;

import java.util.ArrayList;
import java.util.List;

public class MenuList {
	List<MenuItem> menu = new ArrayList<MenuItem>();
	
	public MenuList() {
		
	}
	
	public void addItem(MenuItem it) {
		menu.add(it);
	}
	
	public void deleteItem(MenuItem it) {
		menu.remove(it);
	}


}
