package com.camellia.ordersystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OrderSystemApplicationTests {

	@Autowired
	private com.camellia.ordersystem.controller.MenuController menuController;

	@Test
	void contextLoads() {
		// sanity check
	}

	@Test
	void createAndUpdateMenuItem_shouldSucceed() {
		// create
		com.camellia.ordersystem.dto.MenuItemRequest createReq =
			new com.camellia.ordersystem.dto.MenuItemRequest();
		createReq.itemName = "TestItem";
		createReq.itemPrice = java.math.BigDecimal.valueOf(9.99);
		createReq.soldout = false;

		var createResp = menuController.createMenuItem(createReq);
		assertEquals(org.springframework.http.HttpStatus.CREATED, createResp.getStatusCode());
		com.camellia.ordersystem.dto.MenuItemDTO dto = createResp.getBody();
		assertNotNull(dto);
		Integer id = dto.itemId;

		// perform update with options and notes
		com.camellia.ordersystem.dto.MenuItemRequest updateReq =
			new com.camellia.ordersystem.dto.MenuItemRequest();
		updateReq.itemName = "UpdatedItem";
		updateReq.itemPrice = java.math.BigDecimal.valueOf(11.99);
		updateReq.soldout = true;
		updateReq.options = java.util.List.of(
			new com.camellia.ordersystem.dto.MenuItemOptionRequest("OptA", java.math.BigDecimal.ONE)
		);
		updateReq.notes = java.util.List.of(
			new com.camellia.ordersystem.dto.MenuItemNoteRequest("NoteA", java.math.BigDecimal.ZERO)
		);

		var updateResp = menuController.updateMenuItem(id, updateReq);
		assertEquals(org.springframework.http.HttpStatus.OK, updateResp.getStatusCode());
		com.camellia.ordersystem.dto.MenuItemDTO updated = updateResp.getBody();
		assertNotNull(updated);
		assertEquals("UpdatedItem", updated.itemName);
		assertTrue(updated.options.containsKey("OptA"));
		assertTrue(updated.notes.containsKey("NoteA"));
	}

}
