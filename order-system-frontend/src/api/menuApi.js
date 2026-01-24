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
