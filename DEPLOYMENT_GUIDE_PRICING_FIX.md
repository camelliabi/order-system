# 🚀 Deployment Guide: Complete Pricing Fix

## Current Status

Your code already has the pricing logic fixed in both backend and frontend (from PR #4), but **the database migration hasn't been applied yet**. This is why prices still don't line up.

## The Problem

### What's Happening Now:

1. ✅ **Backend calculates correct price** (option price + note prices)
2. ❌ **Backend tries to save `unitPrice` to database** → FAILS because column doesn't exist
3. ❌ **Backend returns `unitPrice: null` when fetching orders**
4. ❌ **Frontend displays fallback value** → Shows `$0.00` or incorrect price

### Why:

The `unit_price` column doesn't exist in your `order_item` table yet!

---

## ✅ Solution: Apply the Database Migration

### Step 1: Run the Migration Script

The migration file already exists in your repository at:
```
database/migrations/002_add_unit_price_to_order_item.sql
```

**Apply it using psql:**

```bash
psql -U postgres -d order_system -f database/migrations/002_add_unit_price_to_order_item.sql
```

**Or manually via pgAdmin/SQL client:**

```sql
-- Add unit_price column to order_item table
ALTER TABLE order_item
ADD COLUMN IF NOT EXISTS unit_price NUMERIC(10, 2);

-- Add documentation comment
COMMENT ON COLUMN order_item.unit_price IS 'Calculated unit price including chosen option and notes at time of order';
```

### Step 2: Restart Your Spring Boot Application

After adding the column, restart the backend:

```bash
./mvnw spring-boot:run
```

Or if running in IDE, just restart the application.

### Step 3: Test with a New Order

1. Go to customer menu page
2. Select an item with options (e.g., "Fried Rice")
3. Choose "Beef" option ($9.99)
4. Add note "Add rice" ($1.00)
5. Place order
6. **Expected result**: 
   - Backend logs: `calculatedUnitPrice=10.99`
   - Database stores: `unit_price = 10.99`
   - Orders page shows: `$10.99` per unit

---

## 📊 How the Fix Works

### Backend (Already Fixed in Master)

**OrderController.java** - `calculateUnitPrice()` method:

```java
private BigDecimal calculateUnitPrice(MenuItemEntity menuItem, String chosenOption, String notesText) {
    BigDecimal price = BigDecimal.ZERO;
    
    // 1. Use option price OR base price
    if (chosenOption != null && !chosenOption.trim().isEmpty()) {
        price = menuItem.getOptions().stream()
            .filter(opt -> opt.getOptionName().equals(chosenOption.trim()))
            .map(MenuItemOptionEntity::getOptionPrice)
            .orElse(menuItem.getItemPrice());
    } else {
        price = menuItem.getItemPrice();
    }
    
    // 2. Add all note prices
    if (notesText != null && !notesText.trim().isEmpty()) {
        String[] noteNames = notesText.split(",");
        for (String noteName : noteNames) {
            BigDecimal notePrice = menuItem.getNotes().stream()
                .filter(note -> note.getNoteName().equals(noteName.trim()))
                .map(MenuItemNoteEntity::getNotePrice)
                .orElse(BigDecimal.ZERO);
            price = price.add(notePrice);
        }
    }
    
    return price;
}
```

**AllOrdersController.java** - Returns stored `unitPrice`:

```java
itemDto.unitPrice = oie.getUnitPrice();  // ✅ Returns calculated price
```

### Frontend (Already Fixed in Master)

**allOrdersApi.js** - Extracts `unitPrice` from backend:

```javascript
unitPrice: item.unitPrice || 0,  // ✅ Uses backend-calculated price
```

**OrderCard.jsx** - Displays the unit price:

```javascript
${parseFloat(item.unitPrice).toFixed(2)}  // ✅ Shows correct price
```

---

## 🧪 Testing Checklist

After applying the migration, test these scenarios:

### Test 1: Option Only
- **Item**: Fried Rice (base $8.99)
- **Choose**: Beef option ($9.99)
- **Expected**: Unit price = $9.99

### Test 2: Notes Only  
- **Item**: Fried Rice (base $8.99)
- **Notes**: "Add rice" ($1.00)
- **Expected**: Unit price = $9.99 ($8.99 + $1.00)

### Test 3: Option + Multiple Notes
- **Item**: Fried Rice (base $8.99)
- **Choose**: Beef option ($9.99)
- **Notes**: "Add rice" ($1.00), "No onions" ($0.00)
- **Expected**: Unit price = $10.99 ($9.99 + $1.00 + $0.00)

### Test 4: No Option, No Notes
- **Item**: Spring Rolls (base $5.49)
- **No selections**
- **Expected**: Unit price = $5.49

---

## 🔍 Debugging

If prices still don't work after migration:

### 1. Check Database Column Exists

```sql
SELECT column_name, data_type 
FROM information_schema.columns 
WHERE table_name = 'order_item' AND column_name = 'unit_price';
```

Expected result:
```
column_name | data_type
unit_price  | numeric
```

### 2. Check Backend Logs

When creating an order, you should see:

```
Item pricing: menuItemId=1, basePrice=8.99, chosenOption=Beef, notes=Add rice, calculatedUnitPrice=10.99
Found option 'Beef' with price: 9.99
Found note 'Add rice' with price: 1.00
```

### 3. Check Database Values

```sql
SELECT order_item_id, menu_item_id, quantity, chosen_option, notes_text, unit_price 
FROM order_item 
ORDER BY order_item_id DESC 
LIMIT 5;
```

Expected result:
```
order_item_id | menu_item_id | quantity | chosen_option | notes_text | unit_price
1             | 1            | 2        | Beef          | Add rice   | 10.99
```

### 4. Check Frontend API Response

Open browser console and check the logged response:

```javascript
[DEBUG allOrdersApi] First item with full structure: {
  itemId: 1,
  itemName: "Fried Rice",
  unitPrice: 10.99,  // ← Should NOT be 0 or null
  qty: 2,
  chosenOption: "Beef",
  notesText: "Add rice"
}
```

---

## ⚠️ Important Notes

### For Existing Orders

If you have existing orders in the database (created before the migration):
- They will have `unit_price = NULL`
- They will display as `$0.00` on the orders page
- You can manually update them if needed:

```sql
-- Set unit_price for existing orders to base menu price (rough approximation)
UPDATE order_item oi
SET unit_price = mi.item_price
FROM menu_item mi
WHERE oi.menu_item_id = mi.item_id
AND oi.unit_price IS NULL;
```

**Note**: This sets old orders to base price only. For accurate pricing, you'd need to manually calculate based on their `chosen_option` and `notes_text`.

### For New Orders

All new orders (after migration) will automatically:
- Calculate correct price including options and notes
- Store the calculated `unitPrice` in database
- Display correct prices on orders page

---

## 📝 Summary

Your code is already fixed! You just need to:

1. **Apply the database migration** (add `unit_price` column)
2. **Restart the backend**
3. **Create new test orders**
4. **Verify prices are correct**

That's it! The pricing logic is already implemented and working - it just needs the database column to exist.

---

## 🆘 If You Still Have Issues

If prices still don't line up after applying the migration:

1. Check the debugging steps above
2. Share the backend logs when creating an order
3. Share a screenshot of the database `order_item` table
4. Share the browser console output from `[DEBUG allOrdersApi]`

This will help identify if it's a data issue, configuration issue, or something else.
