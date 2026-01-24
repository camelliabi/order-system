# Staff Orders UI - Chosen Option & Note Display Fix

## Problem

The staff orders page was **not displaying chosen option and note values** even though the backend was sending them in the response. Root causes:

1. **Missing Field Extraction**: `allOrdersApi.js` was not extracting `chosenOption` and `note` from OrderItem
2. **Rendering Wrong Source**: OrderCard was trying to render available options/notes (from menuItem) instead of chosen values (from OrderItem)
3. **Field Name Mismatch**: Backend sends `note` field, but frontend didn't know about it

## Backend Structure

Your backend sends OrderItem with:
```java
OrderItem item1 = new OrderItem(menuItem1, 2, "John Doe");
item1.setChosenOption("Chicken");   // ← Backend field: chosenOption
item1.setNote("No onions");         // ← Backend field: note
```

REST API response:
```json
{
  "menuItem": { ... with options/notes maps ... },
  "quantity": 2,
  "customerName": "John Doe",
  "chosenOption": "Chicken",
  "note": "No onions"
}
```

## What Changed

### 1. **src/api/allOrdersApi.js**

**Before:**
```javascript
items: (order.orderItems || []).map(item => ({
  itemId: item.menuItem?.itemId,
  itemName: item.menuItem?.itemName || '',
  unitPrice: item.menuItem?.itemPrice || 0,
  qty: item.quantity || 0,
  customerName: item.customerName || null,
  menuItem: item.menuItem || {},
  // ❌ chosenOption and note LOST!
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
  chosenOption: item.chosenOption || null,      // ✅ Extract chosen option
  chosenNote: item.note || null,                // ✅ Extract chosen note (field is 'note' in backend)
  menuItem: item.menuItem || {},
}))
```

---

### 2. **src/components/OrderCard.jsx**

**Added 4 new rendering functions:**

```javascript
// Show what customer CHOSE
const renderChosenOption = (item) => {
  if (!item.chosenOption || !item.chosenOption.trim()) return null;
  return `Option: ${item.chosenOption.trim()}`;
};

const renderChosenNote = (item) => {
  if (!item.chosenNote || !item.chosenNote.trim()) return null;
  return `Note: ${item.chosenNote.trim()}`;
};

// Show AVAILABLE options/notes (secondary info, grayed out)
const renderAvailableOptions = (item) => {
  const optionsArray = normalizeOptions(item.menuItem?.options);
  if (optionsArray.length === 0) return null;
  const optionsText = optionsArray
    .map((opt) => `${opt.name} ($${parseFloat(opt.price).toFixed(2)})`)
    .join(', ');
  return `Available: ${optionsText}`;
};

const renderAvailableNotes = (item) => {
  const notesArray = normalizeNotes(item.menuItem?.notes);
  if (notesArray.length === 0) return null;
  const notesText = notesArray
    .map((note) => {
      const price = note.price || 0;
      const priceStr = price > 0 ? `+$${parseFloat(price).toFixed(2)}` : `+$0.00`;
      return `${note.name} (${priceStr})`;
    })
    .join(', ');
  return `Available: ${notesText}`;
};
```

**Updated item rendering order** (primary → secondary):
```jsx
<span className="item-name">{item.itemName}</span>
<span className="item-customer-name">For: {customerName}</span>
<span className="item-chosen-option">Option: Chicken</span>        {/* ← What they chose */}
<span className="item-chosen-note">Note: No onions</span>         {/* ← What they chose */}
<span className="item-available-options">Available: ...</span>    {/* ← What was available */}
<span className="item-available-notes">Available: ...</span>      {/* ← What was available */}
```

---

### 3. **src/styles.css**

**Added new CSS classes:**
```css
/* Chosen selections (darker, bold, primary) */
.item-chosen-option {
  font-size: 0.7rem;
  color: #333;
  font-weight: 500;      /* Bold to stand out */
  display: block;
  margin-top: 0.25rem;
}

.item-chosen-note {
  font-size: 0.7rem;
  color: #333;
  font-weight: 500;
  display: block;
  margin-top: 0.15rem;
}

/* Available options (lighter, grayed out, secondary) */
.item-available-options {
  font-size: 0.65rem;
  color: #999;           /* Light gray */
  font-style: italic;
  font-weight: 400;
  display: block;
  margin-top: 0.15rem;
}

.item-available-notes {
  font-size: 0.65rem;
  color: #999;
  font-style: italic;
  font-weight: 400;
  display: block;
  margin-top: 0.15rem;
}
```

---

## Data Flow Example

**Backend OrderItem:**
```json
{
  "menuItem": {
    "itemId": 1,
    "itemName": "Fried Rice",
    "itemPrice": 8.99,
    "options": { "Chicken": 8.99, "Beef": 9.99 },
    "notes": { "Add rice": 1.0, "No onions": 0.0 }
  },
  "quantity": 2,
  "customerName": "John Doe",
  "chosenOption": "Chicken",
  "note": "No onions"
}
```

**After allOrdersApi transformation:**
```javascript
{
  itemId: 1,
  itemName: "Fried Rice",
  unitPrice: 8.99,
  qty: 2,
  customerName: "John Doe",
  chosenOption: "Chicken",           // ← Extracted
  chosenNote: "No onions",           // ← Extracted
  menuItem: {
    itemId: 1,
    itemName: "Fried Rice",
    itemPrice: 8.99,
    options: { "Chicken": 8.99, "Beef": 9.99 },
    notes: { "Add rice": 1.0, "No onions": 0.0 }
  }
}
```

**OrderCard renders:**
```
Fried Rice × 2
For: John Doe
Option: Chicken                        ← Primary (chosen)
Note: No onions                        ← Primary (chosen)
Available: Chicken ($8.99), Beef...    ← Secondary (grayed)
Available: Add rice (+$1.00)...        ← Secondary (grayed)

$8.99 → $17.98
```

---

## Edge Cases Handled

| Scenario | Result |
|----------|--------|
| No chosenOption | "Option:" line doesn't appear |
| No chosenNote | "Note:" line doesn't appear |
| No available options | "Available:" (options) line doesn't appear |
| Empty note/option strings | Treated as null, no line shown |
| Null customerName | "For:" line not shown |
| Missing menuItem | Available options/notes section skipped |

---

## Visual Hierarchy

**Desktop/Tablet View:**
```
┌─ Fried Rice ────────────────────────────────────┬── x2 ──┐
│ For: John Doe                                    │        │
│ Option: Chicken                    (bold, dark)  │ $8.99  │
│ Note: No onions                    (bold, dark)  │        │
│ Available: Chicken ($8.99), Beef   (gray, tiny)  │ $17.98 │
│ Available: Add rice, No onions     (gray, tiny)  │        │
└────────────────────────────────────────────────┴────────┘
```

---

## Polling/Fetching

✅ **No changes** to polling logic (stays 3-second interval)
✅ **No changes** to status update mechanism
✅ **No changes** to backend code

---

## Files Modified

1. ✅ `src/api/allOrdersApi.js` - Extract chosenOption and note
2. ✅ `src/components/OrderCard.jsx` - Add 4 helper functions, update rendering
3. ✅ `src/styles.css` - Add CSS for chosen vs available styling

**Backward Compatible:** Kept old `.item-option`, `.item-options`, `.item-notes` styles for future use.
