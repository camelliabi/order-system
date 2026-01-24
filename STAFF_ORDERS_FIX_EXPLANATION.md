# Staff Orders UI - Fix Explanation

## Problem

The staff orders page was **not displaying options and notes** for order items. Root causes:

1. **Wrong Data Nesting**: Frontend was looking for `item.options` and `item.notes`, but the backend nests them inside `item.menuItem.options` and `item.menuItem.notes`
2. **Map Objects, Not Arrays**: Backend returns options/notes as map objects like `{ "Chicken": 8.99, "Beef": 9.99 }`, but frontend code tried to use `.map()` as if they were arrays
3. **Data Loss During Transformation**: `allOrdersApi.js` was flattening the structure and discarding the options/notes entirely

## What Changed

### 1. **src/api/allOrdersApi.js**

**Before:**
```javascript
items: (order.orderItems || []).map(item => ({
  itemId: item.itemId,
  itemName: item.menuItem?.itemName || '',
  qty: item.quantity || 0,
  unitPrice: item.menuItem?.itemPrice || 0,
  customerName: item.customerName || null,
  // Options/notes were LOST here!
}))
```

**After:**
```javascript
items: (order.orderItems || []).map(item => ({
  itemId: item.menuItem?.itemId,
  itemName: item.menuItem?.itemName || '',
  unitPrice: item.menuItem?.itemPrice || 0,
  qty: item.quantity || 0,
  customerName: item.customerName || null,
  menuItem: item.menuItem || {},  // ← PRESERVE full menuItem with options/notes
}))
```

**Key changes:**
- ✅ Preserve full `menuItem` object (which contains `options` and `notes` maps)
- ✅ Use `order.totalPrice` directly (not calculated from items)
- ✅ Debug log shows full structure

---

### 2. **src/components/OrderCard.jsx**

**Before:**
```javascript
const normalizeNotes = (notes) => { /* ... */ };
const renderOption = (item) => {
  if (!item.optionName) return null;  // Looking in wrong place
  // ...
};
const renderNotes = (item) => {
  const notes = normalizeNotes(item.notes);  // Looking in wrong place
  // ...
};
```

**After:**
```javascript
// NEW: Handle options map conversion
const normalizeOptions = (optionsMap) => {
  if (!optionsMap || typeof optionsMap !== 'object') return [];
  const entries = Object.entries(optionsMap);
  if (entries.length === 0) return [];
  return entries.map(([name, price]) => ({
    name,
    price: typeof price === 'number' ? price : parseFloat(price) || 0,
  }));
};

// UPDATED: Handle notes map conversion (now reads from menuItem)
const normalizeNotes = (notesMap) => {
  if (!notesMap || typeof notesMap !== 'object') return [];
  const entries = Object.entries(notesMap);
  if (entries.length === 0) return [];
  return entries.map(([name, price]) => ({
    name,
    price: typeof price === 'number' ? price : parseFloat(price) || 0,
  }));
};

// UPDATED: Read from menuItem.options (the nested path)
const renderOptions = (item) => {
  const optionsArray = normalizeOptions(item.menuItem?.options);
  if (optionsArray.length === 0) return null;
  const optionsText = optionsArray
    .map((opt) => `${opt.name} ($${parseFloat(opt.price).toFixed(2)})`)
    .join(', ');
  return `Options: ${optionsText}`;
};

// UPDATED: Read from menuItem.notes (the nested path)
const renderNotes = (item) => {
  const notesArray = normalizeNotes(item.menuItem?.notes);
  if (notesArray.length === 0) return null;
  const notesText = notesArray
    .map((note) => {
      const price = note.price || 0;
      const priceStr = price > 0 ? `+$${parseFloat(price).toFixed(2)}` : `+$0.00`;
      return `${note.name} (${priceStr})`;
    })
    .join(', ');
  return `Notes: ${notesText}`;
};
```

**Key changes:**
- ✅ Read from `item.menuItem?.options` (correct nesting)
- ✅ Read from `item.menuItem?.notes` (correct nesting)
- ✅ Convert map objects to arrays using `Object.entries()`
- ✅ Return `null` when empty (no placeholder text appears)
- ✅ Separate `renderOptions()` and `renderNotes()` functions

---

### 3. **src/components/OrderCard.jsx - Item Rendering**

**Before:**
```jsx
{renderOption(item) && (
  <span className="item-option">{renderOption(item)}</span>
)}
```

**After:**
```jsx
{renderOptions(item) && (
  <span className="item-options">{renderOptions(item)}</span>
)}
```

---

### 4. **src/styles.css**

**Added:**
```css
.item-options {
  font-size: 0.7rem;
  color: #666;
  font-style: italic;
  font-weight: 400;
  display: block;
  margin-top: 0.25rem;
}
```

**Updated existing styles to add `display: block` and `margin-top`:**
- `.item-customer-name`
- `.item-option`
- `.item-notes`

This ensures secondary lines (customer name, options, notes) stack vertically without overlap.

---

## Data Flow Example

**Backend Response:**
```json
{
  "orderId": 101,
  "tableId": "A1",
  "totalPrice": 47.44,
  "orderItems": [
    {
      "menuItem": {
        "itemId": 1,
        "itemName": "Fried Rice",
        "itemPrice": 8.99,
        "options": { "Chicken": 8.99, "Beef": 9.99 },
        "notes": { "Add rice": 1.0, "No onions": 0.0 }
      },
      "quantity": 2,
      "customerName": "John Doe"
    }
  ]
}
```

**After allOrdersApi.js transformation:**
```javascript
{
  orderId: 101,
  tableNo: "A1",
  total: 47.44,
  items: [
    {
      itemId: 1,
      itemName: "Fried Rice",
      unitPrice: 8.99,
      qty: 2,
      customerName: "John Doe",
      menuItem: {
        itemId: 1,
        itemName: "Fried Rice",
        itemPrice: 8.99,
        options: { "Chicken": 8.99, "Beef": 9.99 },  // ← Preserved!
        notes: { "Add rice": 1.0, "No onions": 0.0 }  // ← Preserved!
      }
    }
  ]
}
```

**OrderCard.jsx renders:**
```
Fried Rice x2
For: John Doe
Options: Chicken ($8.99), Beef ($9.99)
Notes: Add rice (+$1.00), No onions (+$0.00)
$8.99 → $17.98
```

---

## Edge Cases Handled

| Scenario | Result |
|----------|--------|
| Empty options `{}` | No "Options:" label shown |
| Empty notes `{}` | No "Notes:" label shown |
| Null options/notes | No label shown |
| Missing customerName | No "For:" line shown |
| Price $0.00 | Shows as "+$0.00" for notes |

---

## Files Modified

1. ✅ `src/api/allOrdersApi.js` - Preserve menuItem object
2. ✅ `src/components/OrderCard.jsx` - Read from menuItem, convert maps to arrays
3. ✅ `src/styles.css` - Add display styles for secondary lines

**No changes needed to:**
- `src/pages/StaffOrdersPage.jsx` (already correct)
- Backend (already returns correct structure)
