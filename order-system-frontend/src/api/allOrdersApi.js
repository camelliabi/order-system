export async function fetchOrdersByStatus(status = 'NEW') {
  try {
    const response = await fetch(`/api/all_orders`);
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    const data = await response.json();
    // Transform orders: map tableId to tableNo, preserve nested menuItem with options/notes
    const transformedOrders = (data || []).map(order => ({
      ...order,
      orderId: order.orderId,
      tableNo: order.tableId,  // Map tableId to tableNo
      createdAt: order.createdAt || new Date().toISOString(),
      items: (order.orderItems || []).map(item => ({
        // Flatten backend DTO properties to item level for easy access
        itemId: item.menuItemId || null,
        itemName: item.itemName || '',
        unitPrice: item.unitPrice || 0,
        qty: item.quantity || 0,
        customerName: item.customerName || null,
        // Extract the CHOSEN option and notesText from backend DTO
        chosenOption: item.chosenOption || null,
        chosenNote: item.notesText || null,
        notesText: item.notesText || null,
        // Keep a placeholder for menuItem structure (backend no longer returns full menuItem entity)
        menuItem: {},
      })),
      total: order.totalPrice || 0,  // Use totalPrice directly from backend
    }));
    // DEBUG: Log first order to verify full structure
    if (transformedOrders.length > 0 && transformedOrders[0].items.length > 0) {
      console.log('[DEBUG allOrdersApi] First item with full structure:', transformedOrders[0].items[0]);
    }
    return transformedOrders;
  } catch (error) {
    console.error('Error fetching orders:', error);
    throw error;
  }
}

export async function patchOrderStatus(orderId, newStatus) {
  try {
    const response = await fetch(`/api/all_orders/${orderId}`, {
      method: 'PATCH',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ status: newStatus }),
    });
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    const data = await response.json();
    return data;
  } catch (error) {
    console.error('Error updating order status:', error);
    throw error;
  }
}
