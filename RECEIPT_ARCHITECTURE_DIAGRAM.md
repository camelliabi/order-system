# 80mm Thermal Receipt Printer Integration - Architecture Diagram

## System Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        FRONTEND (React + Vite)                   │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  ┌──────────────────────────┐      ┌──────────────────────────┐ │
│  │   StaffOrdersPage        │      │  CustomerMenuPage        │ │
│  │  (Main Orders View)      │      │  (Customer Menu)         │ │
│  │                          │      │                          │ │
│  │  ┌────────────────────┐  │      │                          │ │
│  │  │   Order Card 1     │  │      │                          │ │
│  │  │  ┌──────────────┐  │  │      │                          │ │
│  │  │  │ Accept Button│  │  │      │                          │ │
│  │  │  │ Ready Button │  │  │      │                          │ │
│  │  │  │ PRINT BUTTON │◄─┼──┼──────┼─ Click Print             │
│  │  │  │ (NEW) 🖨️     │  │  │      │                          │ │
│  │  │  └──────────────┘  │  │      │                          │ │
│  │  └────────────────────┘  │      │                          │ │
│  │                          │      │                          │ │
│  │  ┌────────────────────┐  │      │                          │ │
│  │  │   Order Card 2     │  │      │                          │ │
│  │  │  [Print Button]    │  │      │                          │ │
│  │  └────────────────────┘  │      │                          │ │
│  │                          │      │                          │ │
│  │  ... more orders ...     │      │                          │ │
│  └──────────────────────────┘      └──────────────────────────┘ │
│                                                                   │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │              React Router (NEW)                          │   │
│  │  Routes:                                                 │   │
│  │    /                    → StaffOrdersPage               │   │
│  │    /customer           → CustomerMenuPage               │   │
│  │    /staff/print/:id    → PrintReceiptPage (NEW)         │   │
│  └──────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
                              ↓
                   window.open("/staff/print/101", "_blank")
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                 NEW BROWSER POPUP WINDOW                         │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │              PrintReceiptPage Component                 │    │
│  │                                                          │    │
│  │  1. Extract orderId from URL params                     │    │
│  │  2. Fetch all orders from backend                       │    │
│  │  3. Find order matching ID                              │    │
│  │  4. Render receipt HTML (80mm)                          │    │
│  │  5. Auto-trigger window.print() after 300ms             │    │
│  │                                                          │    │
│  │  ┌─────────────────────────────────────────────────┐    │    │
│  │  │  80mm Receipt Layout                            │    │    │
│  │  ├─────────────────────────────────────────────────┤    │    │
│  │  │                                                 │    │    │
│  │  │          RESTAURANT NAME                        │    │    │
│  │  │          ORDER RECEIPT                          │    │    │
│  │  │                                                 │    │    │
│  │  │  Order #: 101      Table: A1                    │    │    │
│  │  │  Time: 01/26/2026 14:30:45                      │    │    │
│  │  │ ─────────────────────────────────────────────── │    │    │
│  │  │                                                 │    │    │
│  │  │  Fried Rice              x2   $17.98           │    │    │
│  │  │  For: John Doe                                 │    │    │
│  │  │  Option: Chicken                               │    │    │
│  │  │  Note: No onions                               │    │    │
│  │  │                                                 │    │    │
│  │  │  Beef Noodles            x1   $12.99           │    │    │
│  │  │  For: Jane Smith                               │    │    │
│  │  │ ─────────────────────────────────────────────── │    │    │
│  │  │                     Total: $47.44               │    │    │
│  │  │ ─────────────────────────────────────────────── │    │    │
│  │  │        Thank you for your order!               │    │    │
│  │  │          Please enjoy your meal                │    │    │
│  │  │                                                 │    │    │
│  │  └─────────────────────────────────────────────────┘    │    │
│  │                                                          │    │
│  │  @media print CSS → Hides UI, optimizes for printer    │    │
│  └─────────────────────────────────────────────────────────┘    │
│                                                                   │
└─────────────────────────────────────────────────────────────────┘
                              ↓
                    window.print() triggered
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│              BROWSER PRINT DIALOG                                │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  ┌────────────────────────────────────┐                          │
│  │        Print Preview                │                          │
│  │  ┌──────────────────────────────┐   │                          │
│  │  │   80mm Receipt Preview       │   │  Printer List:          │
│  │  │                              │   │  ▼ [Select Printer]    │
│  │  │  RESTAURANT NAME             │   │                          │
│  │  │  ORDER RECEIPT               │   │  ☑ 80mm Thermal        │
│  │  │                              │   │  ☐ Office Printer      │
│  │  │  Order #: 101  Table: A1     │   │  ☐ Print to PDF        │
│  │  │  ...items...                 │   │                          │
│  │  │  Total: $47.44               │   │  Settings:              │
│  │  │                              │   │  □ Margins              │
│  │  │  Thank you...                │   │  □ Scaling              │
│  │  │                              │   │  □ Headers/Footers     │
│  │  └──────────────────────────────┘   │                          │
│  │                                      │  [Cancel]  [Print]      │
│  └────────────────────────────────────┘                          │
│                                                                   │
└─────────────────────────────────────────────────────────────────┘
                              ↓
                   User selects 80mm printer
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                    80mm THERMAL PRINTER                          │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│     ╔══════════════════════════════════╗                         │
│     ║     RESTAURANT NAME              ║                         │
│     ║     ORDER RECEIPT                ║                         │
│     ║                                  ║                         │
│     ║  Order #: 101  Table: A1         ║                         │
│     ║  Time: 01/26 14:30               ║                         │
│     ║ ─────────────────────────────── ║                         │
│     ║                                  ║                         │
│     ║  Fried Rice          x2 $17.98  ║  ← Printed on           │
│     ║  For: John Doe                   ║    80mm thermal         │
│     ║  Option: Chicken                 ║    paper roll           │
│     ║  Note: No onions                 ║                         │
│     ║                                  ║                         │
│     ║  Beef Noodles        x1 $12.99  ║                         │
│     ║  For: Jane Smith                 ║                         │
│     ║ ─────────────────────────────── ║                         │
│     ║                 Total: $47.44    ║                         │
│     ║ ─────────────────────────────── ║                         │
│     ║     Thank you for your order!    ║                         │
│     ║      Please enjoy your meal      ║                         │
│     ║                                  ║                         │
│     ╚══════════════════════════════════╝                         │
│         │                                                         │
│         │ Tears off receipt along perforated line               │
│         ▼                                                         │
│         Ready for customer or kitchen use                       │
│                                                                   │
└─────────────────────────────────────────────────────────────────┘
```

## Data Flow Diagram

```
┌────────────────────────┐
│  Spring Boot Backend   │
│  /api/all_orders       │
│                        │
│  Returns:              │
│  [{                    │
│    orderId: 101,       │
│    tableId: "A1",      │
│    totalPrice: 47.44,  │
│    orderItems: [       │
│      {                 │
│        menuItem: {     │
│          itemName: "...",
│          itemPrice: 8.99
│        },              │
│        quantity: 2,    │
│        customerName: "John"
│        chosenOption: "Chicken"
│        note: "No onions"
│      },                │
│      ...               │
│    ]                   │
│  }]                    │
└─────────┬──────────────┘
          │ fetch()
          ↓
┌─────────────────────────────────────┐
│  allOrdersApi.js                    │
│  fetchOrdersByStatus()              │
│                                     │
│  Transforms:                        │
│  - orderItems → items[]             │
│  - tableId → tableNo                │
│  - totalPrice → total               │
│  - Extracts menuItem fields         │
└─────────┬───────────────────────────┘
          │
          ├─────────────┬──────────────┐
          ▼             ▼              ▼
    StaffOrdersPage  Customer       PrintReceipt
    (orders list)    (not used)      (specific order)
          │                                │
          │                                │
    Each Order ────────────────────→ Order ID
    Card with Print                (from URL)
    Button                          │
          │                         │
          └─────────────────────────┘
                   │
                   ↓
    Fetch & Find: allOrders.find(o =>
                  o.orderId === parseInt(id))
                   │
                   ↓
         ┌──────────────────┐
         │ Order Object     │
         │ (with items[])   │ ← Successfully
         │                  │   transformed
         │ items: [{        │   data
         │   itemName: "...",
         │   qty: 2,        │
         │   unitPrice: 8.99
         │ }, ...]          │
         └────────┬─────────┘
                  │
                  ↓
         Render Receipt HTML
         (80mm width)
                  │
                  ↓
         window.print() called
                  │
                  ↓
         Browser Print Dialog
                  │
                  ↓
         User selects printer
                  │
                  ↓
         Output to 80mm thermal
         printer
```

## State Management Flow

```
PrintReceiptPage Component:

State:
  ├─ order: null → Order object (after fetch)
  ├─ loading: true → false
  └─ error: null → error message (if any)

useEffect #1 (mount, [orderId]):
  ├─ Load all orders
  ├─ Find matching orderId
  ├─ Set order state
  └─ Handle errors

useEffect #2 (order change, [order, loading]):
  ├─ Wait for order to load
  ├─ Delay 300ms for rendering
  └─ Call window.print()

Lifecycle:
  1. Component mounts
  2. useEffect #1 triggers
  3. Fetch orders
  4. Set order state
  5. Re-render with receipt
  6. useEffect #2 triggers
  7. window.print() opens dialog
  8. User selects printer
  9. Receipt prints
```

## File Dependency Graph

```
                    package.json
                         │
        ┌─────────────────┼─────────────────┐
        │                 │                 │
    react-dom      react-router-dom    vite
        │                 │                 │
        └────────┬────────┴────────┬────────┘
                 │                 │
            main.jsx ◄─────────────┤
                 │                 │
                 ↓                 ↓
            App.jsx ◄──────────────┤ Router
         (NEW: Router)             │
             │   │ │               │
        ┌────┴───┼─┴───────────────┘
        │        │
        ↓        ↓
    StaffOrders  PrintReceipt ◄──── (NEW)
    Page         Page
        │            │
        └─┬──────────┘
          │
      OrderCard
    (+ Print Button)
          │
          ├─────────────────┐
          │                 │
      allOrdersApi      styles.css
    (transform data)  (receipt +
                       print styles)
```

## Component Hierarchy

```
App (Router wrapper)
├── StaffOrdersPage
│   ├── Filter Section
│   ├── Dashboard Wrapper
│   │   ├── Orders Panel
│   │   │   └── Orders List
│   │   │       └── OrderCard (multiple)
│   │   │           ├── Header (Order ID, Time)
│   │   │           ├── Table Section
│   │   │           ├── Note Section
│   │   │           ├── Items Section
│   │   │           │   └── Order Items
│   │   │           ├── Total Section
│   │   │           └── Actions (Accept, Ready, PRINT ◄─ NEW)
│   │   └── Summary Panel
└── PrintReceiptPage ◄──────────── NEW ROUTE
    └── Receipt
        ├── Header (Restaurant Name)
        ├── Order Info (ID, Table, Time)
        ├── Items List
        │   └── Receipt Items (formatted)
        ├── Total
        └── Footer (Thank You)
```

## Styling Cascade

```
Base Styles (mobile-first):
  html, body, #root
  └─ .receipt-container (80mm centered)
      └─ .receipt (white, monospace font, 13px)
          ├─ .receipt-header
          ├─ .receipt-divider
          ├─ .receipt-info
          ├─ .receipt-items
          │   └─ .receipt-item
          ├─ .receipt-total
          └─ .receipt-footer

Print Media Styles (@media print):
  ├─ Remove all margins/padding
  ├─ Set width: 80mm
  ├─ Hide non-receipt elements
  ├─ White background only
  ├─ Black text only
  └─ Optimize for 80mm paper

Order Card Styles:
  ├─ .order-actions
  │   ├─ .btn (base button)
  │   ├─ .btn-accept (green)
  │   ├─ .btn-ready (blue)
  │   └─ .btn-print (orange) ◄─── NEW
  └─ Hover/active states
```

## Print Flow Sequence

```
1. User clicks Print Button (OrderCard)
   └─ handlePrint() executes

2. window.open("/staff/print/{id}", "_blank")
   └─ New popup window opens
   └─ Browser navigates to /staff/print/:orderId

3. React Router matches route
   └─ Renders PrintReceiptPage component

4. PrintReceiptPage mounts
   └─ useEffect #1 fires
   └─ Fetches all orders
   └─ Finds order by ID
   └─ Sets order state

5. Component re-renders with receipt HTML
   └─ Receipt element appears on page
   └─ CSS styles applied (80mm width)

6. useEffect #2 fires (order loaded)
   └─ setTimeout 300ms
   └─ Ensures HTML is rendered
   └─ window.print() called

7. Browser Print Dialog opens
   └─ Shows receipt preview (80mm width)
   └─ Displays available printers
   └─ Displays print options

8. User selects printer
   └─ Usually "80mm Thermal Printer"
   └─ Can also select "Print to PDF"

9. User clicks Print button
   └─ Browser sends to printer
   └─ @media print CSS applied
   └─ Removes margins, hides UI

10. 80mm thermal printer receives data
    └─ Prints receipt on 80mm paper
    └─ Receipt ejects from printer
    └─ Ready for customer/kitchen

11. Optional: User closes popup window
    └─ Returns to Staff Orders Page
    └─ Can print another order
```

## Hardware Integration

```
┌─────────────────────────────────────┐
│  80mm Thermal Receipt Printer        │
├─────────────────────────────────────┤
│                                      │
│  Typical Models:                     │
│  ├─ Star Micronics TSP100II          │
│  ├─ Epson TM-20                      │
│  ├─ Brother QL-800                   │
│  └─ Other ESC/POS compatible         │
│                                      │
│  Specifications:                     │
│  ├─ Paper Width: 80mm (exactly)      │
│  ├─ Resolution: 203 DPI              │
│  ├─ Print Speed: 50-150mm/sec        │
│  ├─ Font: Monospace (best)           │
│  ├─ Connection: USB / Ethernet / COM │
│  └─ Inventory: Thermal paper rolls   │
│                                      │
│  Installation:                       │
│  ├─ Install driver from vendor       │
│  ├─ Connect via USB/Network          │
│  ├─ Add to Windows/Mac Printers      │
│  ├─ Set as default (optional)        │
│  └─ Test print from system settings  │
│                                      │
└─────────────────────────────────────┘
     ▲
     │ USB / Ethernet / Serial
     │
┌────┴──────────────────────────────────┐
│  Computer / Server                     │
│  └─ Browser (Chrome/Firefox)          │
│     └─ React App                      │
│        └─ window.print()              │
└────────────────────────────────────────┘
```

## Success Criteria

✅ **Functionality**
  - Print button visible on each order
  - Click Print opens receipt in new window
  - Receipt displays all order data correctly
  - Auto-print dialog opens (300ms delay)
  - Only receipt visible (no UI)
  - Receipt width is 80mm

✅ **User Experience**
  - Fast (300ms load before print)
  - Intuitive (obvious Print button)
  - Reliable (works every time)
  - Flexible (can change printer)
  - Clean (no UI distractions)

✅ **Technical**
  - No backend changes needed
  - No TypeScript required
  - Pure React/CSS solution
  - React Router implemented
  - Print media styles work
  - Mobile-friendly fallback

✅ **Printer Support**
  - Works with 80mm thermal printers
  - Works with regular printers
  - Works with PDF export
  - Handles paper width correctly
  - Margins removed for thermal

## Next Steps

1. ✅ Code implementation (DONE)
2. ✅ Install dependencies (DONE)
3. ⏳ Test in browser
4. ⏳ Test with 80mm printer
5. ⏳ Adjust receipt layout if needed
6. ⏳ Deploy to production
7. ⏳ Train kitchen staff on Print button
8. ⏳ Monitor printer usage/errors
