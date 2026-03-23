export async function fetchMenu() {
  try {
    const response = await fetch('/api/menu');
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    const data = await response.json();
    // Normalize menu items: convert map objects to arrays
    return normalizeMenuItems(data);
  } catch (error) {
    console.error('Error fetching menu:', error);
    throw error;
  }
}

/**
 * Create a new menu item
 */
export async function createMenuItem(itemData) {
  try {
    const response = await fetch('/api/menu', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(convertToRequestFormat(itemData)),
    });
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    const data = await response.json();
    return normalizeMenuItems([data])[0];
  } catch (error) {
    console.error('Error creating menu item:', error);
    throw error;
  }
}

/**
 * Update an existing menu item
 */
export async function updateMenuItem(id, itemData) {
  try {
    const response = await fetch(`/api/menu/${id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(convertToRequestFormat(itemData)),
    });
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    const data = await response.json();
    return normalizeMenuItems([data])[0];
  } catch (error) {
    console.error('Error updating menu item:', error);
    throw error;
  }
}

/**
 * Delete a menu item by ID
 */
export async function deleteMenuItem(id) {
  try {
    const response = await fetch(`/api/menu/${id}`, {
      method: 'DELETE',
    });
    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(errorText || `HTTP error! status: ${response.status}`);
    }
    return true;
  } catch (error) {
    console.error('Error deleting menu item:', error);
    throw error;
  }
}

/**
 * Convert frontend format (with array options/notes) to backend format
 */
function convertToRequestFormat(itemData) {
  return {
    itemName: itemData.itemName,
    itemPrice: parseFloat(itemData.itemPrice),
    soldout: itemData.soldout || false,
    options: (itemData.options || []).map(opt => ({
      optionName: opt.name || opt.optionName,
      optionPrice: parseFloat(opt.price || opt.optionPrice),
    })),
    notes: (itemData.notes || []).map(note => ({
      noteName: note.name || note.noteName,
      notePrice: parseFloat(note.price || note.notePrice),
    })),
  };
}

/**
 * Normalize menu items to convert map objects to arrays
 * Backend returns: options/notes as { "name": price, ... }
 * Frontend needs: [ { name: "name", price: price }, ... ]
 */
function normalizeMenuItems(items) {
  return (items || []).map((item) => ({
    ...item,
    // Convert options map object to array: { "Chicken": 8.99 } → [ { name: "Chicken", price: 8.99 } ]
    options: convertMapToArray(item.options),
    // Convert notes map object to array: { "No onions": 0.0 } → [ { name: "No onions", price: 0.0 } ]
    notes: convertMapToArray(item.notes),
  }));
}

/**
 * Convert map object to array of objects with name and price
 * @param {Object} mapObj - Map object like { "Chicken": 8.99, "Beef": 9.99 }
 * @returns {Array} Array like [ { name: "Chicken", price: 8.99 }, { name: "Beef", price: 9.99 } ]
 */
function convertMapToArray(mapObj) {
  // Handle null, undefined, or empty map
  if (!mapObj || typeof mapObj !== 'object' || Object.keys(mapObj).length === 0) {
    return [];
  }
  
  return Object.entries(mapObj).map(([name, price]) => ({
    name,
    price: typeof price === 'number' ? price : parseFloat(price) || 0,
  }));
}
