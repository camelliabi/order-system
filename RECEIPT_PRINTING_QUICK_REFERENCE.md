# Receipt Printing Feature - Quick Reference

## What Was Added

### 1. New Page Component
- **File**: `src/pages/PrintReceiptPage.jsx`
- **Purpose**: Renders and auto-prints 80mm thermal receipts
- **Triggered by**: Print button on each order card
- **Route**: `/staff/print/:orderId`

### 2. Routing Setup
- **File**: `src/App.jsx`
- **Changed from**: Simple component rendering
- **Changed to**: React Router with 3 routes:
  - `/` → StaffOrdersPage (main orders)
  - `/staff/print/:orderId` → PrintReceiptPage (print page)
  - `/customer` → CustomerMenuPage (customer view)

### 3. Print Button
- **File**: `src/components/OrderCard.jsx`
- **Location**: Order action buttons (next to Accept & Mark Ready)
- **Color**: Orange (#e67e22)
- **Action**: Opens print page in new popup window

### 4. Receipt Styles
- **File**: `src/styles.css`
- **Added**:
  - Receipt layout styles (~120 lines)
  - Print media styles (@media print)
  - Print button styling (.btn-print)

### 5. Dependency
- **Installed**: `react-router-dom` (npm install react-router-dom)
- **Purpose**: Client-side routing without page reloads

## Usage

### Printing a Receipt
1. View orders on Staff Orders Page
2. Click **Print** button on desired order
3. New window opens with receipt preview
4. Print dialog automatically appears
5. Select 80mm thermal printer from dropdown
6. Click Print

### Receipt Content
The 80mm receipt displays:
- Restaurant name (placeholder)
- Order number and table ID
- Order timestamp
- All items with:
  - Item name and quantity
  - Customer name (if available)
  - Chosen option (if available)
  - Special notes (if available)
  - Unit price and line subtotal
- Order total
- Thank you message

## Technical Flow

```
Order Card with Print Button
              ↓
        handlePrint()
              ↓
window.open("/staff/print/:orderId")
              ↓
        PrintReceiptPage
              ↓
   fetchOrdersByStatus()
              ↓
    Find order by ID
              ↓
   Render Receipt HTML
              ↓
  Auto-trigger window.print()
              ↓
   Print Dialog Opens
              ↓
   Select Printer & Print
```

## Styling Highlights

### Screen View (normal browsing)
- Receipt centered on page
- 80mm width container
- White background with shadow
- Readable font size (13px)
- Monospace font for thermal aesthetic

### Print View (@media print)
- All margins removed
- Background colors hidden
- Only receipt content visible
- No UI buttons or header
- Optimized for 80mm paper width
- No shadows or borders

## Code Overview

### OrderCard Changes
```jsx
const handlePrint = () => {
  window.open(`/staff/print/${order.orderId}`, '_blank', 'width=400,height=600');
};

// In JSX:
<button className="btn btn-print" onClick={handlePrint}>
  Print
</button>
```

### PrintReceiptPage Setup
```jsx
const { orderId } = useParams();  // Get order ID from URL

// Auto-print after page loads
useEffect(() => {
  if (order && !loading) {
    setTimeout(() => {
      window.print();  // Open print dialog
    }, 300);
  }
}, [order, loading]);
```

### Receipt Layout (80mm)
```
═════════════════════════════════
      RESTAURANT NAME
         ORDER RECEIPT
═════════════════════════════════
Order #: 101        Table: A1
Time: 01/26/2026 14:30:45
───────────────────────────────── (dashed line)
Fried Rice                    x2
For: John Doe
Option: Chicken
Note: No onions
$8.99 × 2                 $17.98
───────────────────────────────── (dashed line)
Total                     $47.44
───────────────────────────────── (dashed line)
    Thank you for your order!
      Please enjoy your meal
═════════════════════════════════
```

## Browser Compatibility

| Browser | Auto-Print | Print Dialog | Popup | Notes |
|---------|-----------|--------------|-------|-------|
| Chrome  | ✅ Yes    | ✅ Yes       | ✅ Yes | Best experience |
| Firefox | ✅ Yes    | ✅ Yes       | ✅ Yes | Works well |
| Edge    | ✅ Yes    | ✅ Yes       | ✅ Yes | Same as Chrome |
| Safari  | ⚠️ Maybe  | ✅ Yes       | ⚠️ Maybe | May need popup permission |

## Printer Setup

### For 80mm Thermal Printer
1. Install printer drivers on system
2. Configure printer as default or select in print dialog
3. Set paper size to 80mm width (or custom size)
4. Disable scaling and margins in print dialog
5. Print!

### Alternative: PDF Export
Users can also select "Print to PDF" from the dialog to save receipts as files.

## Testing

### Manual Testing Steps
1. Start Spring Boot backend
2. Open Staff Orders Page
3. Verify orders display
4. Click Print on an order
5. Verify new window opens with receipt
6. Verify auto-print dialog appears
7. Verify receipt content is correct
8. Verify print styling hides UI elements
9. Select printer and print test receipt
10. Verify receipt prints correctly on 80mm printer

### Expected Output
- Receipt prints on 80mm width
- All text clearly visible
- No UI elements printed
- Item details aligned properly
- Total price visible at bottom
- Receipt fits on thermal roll

## Troubleshooting

### Issue: Print dialog doesn't auto-open
- **Cause**: Browser security settings
- **Fix**: Check popup permissions for app domain
- **Alternative**: User can use Ctrl+P / Cmd+P manually

### Issue: Receipt width not 80mm
- **Cause**: Browser print scaling
- **Fix**: Disable scaling in print dialog ("Print to fit" = Off)
- **Alternative**: Adjust paper size in system printer settings

### Issue: Content cut off at edges
- **Cause**: Printer margins
- **Fix**: Disable margins in print dialog
- **Alternative**: Adjust receipt container padding

## Files Modified Summary

| File | Type | Changes |
|------|------|---------|
| src/App.jsx | Updated | Added Router, Routes, 3 routes |
| src/pages/PrintReceiptPage.jsx | New | 177 lines, receipt component |
| src/components/OrderCard.jsx | Updated | Added handlePrint, Print button |
| src/styles.css | Updated | +~300 lines (receipt + print styles) |
| package.json | Updated | Added react-router-dom dependency |

## Total Implementation
- **Lines of Code Added**: ~500+
- **Files Created**: 1
- **Files Modified**: 4
- **Dependencies Added**: 1
- **No Backend Changes Required**: ✅

## Next Steps (Optional)

### Enhancements to Consider
1. Add restaurant logo/image to receipt
2. Include customer order notes at top
3. Add order confirmation code
4. Include QR code for order tracking
5. Kitchen printer direct printing (bypass dialog)
6. Receipt history/reprint functionality
7. Custom receipt templates by restaurant
8. Discount/promotion information
9. Barcode for inventory tracking
10. Multi-language support

### Monitoring
- Track print button clicks
- Monitor receipt print success
- Collect printer error logs
- Analyze thermal printer usage patterns
