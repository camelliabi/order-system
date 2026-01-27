# Receipt Printing - Complete Implementation Code Reference

## File 1: src/pages/PrintReceiptPage.jsx (NEW - 177 lines)

```jsx
import { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { fetchOrdersByStatus } from '../api/allOrdersApi';

export default function PrintReceiptPage() {
  const { orderId } = useParams();
  const [order, setOrder] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const loadOrder = async () => {
      try {
        // Fetch all orders and find the matching one
        const allOrders = await fetchOrdersByStatus('NEW');
        const foundOrder = allOrders.find(o => o.orderId === parseInt(orderId));
        
        if (!foundOrder) {
          setError('Order not found');
          setLoading(false);
          return;
        }
        
        setOrder(foundOrder);
      } catch (err) {
        console.error('Error loading order:', err);
        setError('Failed to load order');
      } finally {
        setLoading(false);
      }
    };

    loadOrder();
  }, [orderId]);

  // Auto-print after content is rendered
  useEffect(() => {
    if (order && !loading) {
      setTimeout(() => {
        window.print();
      }, 300);
    }
  }, [order, loading]);

  if (loading) {
    return <div className="receipt-loading">Loading receipt...</div>;
  }

  if (error) {
    return <div className="receipt-error">{error}</div>;
  }

  if (!order) {
    return <div className="receipt-error">Order not found</div>;
  }

  const getCurrentTime = () => {
    const now = new Date();
    return now.toLocaleString([], { 
      year: 'numeric',
      month: '2-digit', 
      day: '2-digit',
      hour: '2-digit', 
      minute: '2-digit',
      second: '2-digit'
    });
  };

  const getItemSubtotal = (unitPrice, qty) => {
    return (unitPrice * qty).toFixed(2);
  };

  return (
    <div className="receipt-container">
      <div className="receipt">
        {/* Receipt Header */}
        <div className="receipt-header">
          <h2 className="receipt-title">Restaurant Name</h2>
          <p className="receipt-subtitle">Order Receipt</p>
        </div>

        {/* Receipt Divider */}
        <div className="receipt-divider"></div>

        {/* Order Info */}
        <div className="receipt-info">
          <div className="receipt-line">
            <span className="receipt-label">Order #:</span>
            <span className="receipt-value">{order.orderId}</span>
          </div>
          <div className="receipt-line">
            <span className="receipt-label">Table:</span>
            <span className="receipt-value">{order.tableNo || order.tableId}</span>
          </div>
          <div className="receipt-line">
            <span className="receipt-label">Time:</span>
            <span className="receipt-value">{getCurrentTime()}</span>
          </div>
        </div>

        {/* Receipt Divider */}
        <div className="receipt-divider"></div>

        {/* Items */}
        <div className="receipt-items">
          {order.items && order.items.length > 0 ? (
            order.items.map((item, index) => (
              <div key={index} className="receipt-item">
                <div className="receipt-item-header">
                  <span className="receipt-item-name">{item.itemName}</span>
                  <span className="receipt-item-qty">x{item.qty}</span>
                </div>

                {/* Customer Name */}
                {item.customerName && (
                  <div className="receipt-item-detail">
                    <span className="receipt-detail-label">For:</span>
                    <span>{item.customerName}</span>
                  </div>
                )}

                {/* Chosen Option */}
                {item.chosenOption && (
                  <div className="receipt-item-detail">
                    <span className="receipt-detail-label">Option:</span>
                    <span>{item.chosenOption}</span>
                  </div>
                )}

                {/* Note */}
                {item.note && (
                  <div className="receipt-item-detail">
                    <span className="receipt-detail-label">Note:</span>
                    <span>{item.note}</span>
                  </div>
                )}

                {/* Price */}
                <div className="receipt-item-price">
                  <span className="receipt-item-unit">
                    ${parseFloat(item.unitPrice).toFixed(2)} × {item.qty}
                  </span>
                  <span className="receipt-item-subtotal">
                    ${getItemSubtotal(item.unitPrice, item.qty)}
                  </span>
                </div>
              </div>
            ))
          ) : (
            <p className="receipt-no-items">No items</p>
          )}
        </div>

        {/* Receipt Divider */}
        <div className="receipt-divider"></div>

        {/* Total */}
        <div className="receipt-total">
          <span className="receipt-total-label">Total:</span>
          <span className="receipt-total-amount">
            ${parseFloat(order.total).toFixed(2)}
          </span>
        </div>

        {/* Receipt Divider */}
        <div className="receipt-divider"></div>

        {/* Footer */}
        <div className="receipt-footer">
          <p>Thank you for your order!</p>
          <p>Please enjoy your meal</p>
        </div>
      </div>
    </div>
  );
}
```

---

## File 2: src/App.jsx (UPDATED)

```jsx
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import CustomerMenuPage from './pages/CustomerMenuPage';
import StaffOrdersPage from './pages/StaffOrdersPage';
import PrintReceiptPage from './pages/PrintReceiptPage';

export default function App() {
  return (
    <Router>
      <Routes>
        <Route path="/staff/print/:orderId" element={<PrintReceiptPage />} />
        <Route path="/" element={<StaffOrdersPage />} />
        <Route path="/customer" element={<CustomerMenuPage />} />
      </Routes>
    </Router>
  );
}
```

---

## File 3: src/components/OrderCard.jsx (UPDATED - changes only)

### Change 1: Add handlePrint function
```jsx
  // Open print receipt in new window
  const handlePrint = () => {
    window.open(`/staff/print/${order.orderId}`, '_blank', 'width=400,height=600');
  };
```

### Change 2: Add Print button to action buttons
```jsx
      {/* Action Buttons */}
      <div className="order-actions">
        <button
          className="btn btn-accept"
          onClick={handleAccept}
          disabled={isProcessing}
        >
          {isProcessing ? 'Processing...' : 'Accept'}
        </button>
        <button
          className="btn btn-ready"
          onClick={handleMarkReady}
          disabled={isProcessing}
        >
          {isProcessing ? 'Processing...' : 'Mark Ready'}
        </button>
        <button
          className="btn btn-print"
          onClick={handlePrint}
        >
          Print
        </button>
      </div>
```

---

## File 4: src/styles.css (UPDATED - new sections)

### Addition 1: Receipt Component Styles (~200 lines, added before @media print)

```css
/* ============================================
   Receipt Printing Styles (80mm Thermal)
   ============================================ */

.receipt-container {
  width: 100%;
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding: 2rem 1rem;
  background-color: #f5f5f5;
  font-family: 'Courier New', monospace;
}

.receipt {
  width: 80mm;
  background-color: white;
  padding: 2rem 1.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  font-size: 13px;
  line-height: 1.6;
  color: #000;
}

.receipt-loading,
.receipt-error {
  width: 100%;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  font-family: system-ui, sans-serif;
  font-size: 16px;
  color: #333;
}

.receipt-error {
  color: #d32f2f;
}

/* Receipt Header */
.receipt-header {
  text-align: center;
  margin-bottom: 1.5rem;
}

.receipt-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 0.25rem;
  letter-spacing: 1px;
}

.receipt-subtitle {
  font-size: 11px;
  color: #666;
  margin: 0;
}

/* Receipt Divider */
.receipt-divider {
  border-top: 1px dashed #000;
  margin: 1rem 0;
  height: 0;
}

/* Receipt Info Section */
.receipt-info {
  margin-bottom: 1rem;
  font-size: 12px;
}

.receipt-line {
  display: flex;
  justify-content: space-between;
  margin-bottom: 0.4rem;
}

.receipt-label {
  font-weight: bold;
  flex: 0 0 auto;
  margin-right: 1rem;
}

.receipt-value {
  flex: 1;
  text-align: right;
}

/* Receipt Items */
.receipt-items {
  margin-bottom: 1rem;
  font-size: 12px;
}

.receipt-item {
  margin-bottom: 1rem;
  padding-bottom: 0.75rem;
}

.receipt-item-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 0.25rem;
  font-weight: bold;
}

.receipt-item-name {
  flex: 1;
  word-wrap: break-word;
}

.receipt-item-qty {
  flex: 0 0 auto;
  margin-left: 0.5rem;
  text-align: right;
}

.receipt-item-detail {
  font-size: 11px;
  color: #555;
  margin-bottom: 0.25rem;
  display: flex;
  gap: 0.5rem;
}

.receipt-detail-label {
  font-weight: bold;
  min-width: 40px;
}

.receipt-item-price {
  display: flex;
  justify-content: space-between;
  margin-top: 0.4rem;
  border-top: 1px solid #ddd;
  padding-top: 0.4rem;
}

.receipt-item-unit {
  font-size: 11px;
  color: #666;
}

.receipt-item-subtotal {
  font-weight: bold;
  text-align: right;
}

.receipt-no-items {
  font-size: 11px;
  color: #999;
  text-align: center;
  padding: 0.5rem 0;
}

/* Receipt Total */
.receipt-total {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  font-size: 14px;
  font-weight: bold;
}

.receipt-total-label {
  flex: 1;
}

.receipt-total-amount {
  flex: 1;
  text-align: right;
  font-size: 16px;
  padding-left: 0.5rem;
}

/* Receipt Footer */
.receipt-footer {
  text-align: center;
  font-size: 11px;
  color: #666;
}

.receipt-footer p {
  margin: 0.3rem 0;
}
```

### Addition 2: Print Button Styling (in button styles section)

```css
.btn-print {
  background-color: #e67e22;
  color: white;
}

.btn-print:hover {
  background-color: #d35400;
  transform: translateY(-2px);
  box-shadow: 0 2px 8px rgba(230, 126, 34, 0.3);
}

.btn-print:active {
  transform: translateY(0);
}
```

### Addition 3: Print Media Styles

```css
/* ============================================
   Print Media Styles
   ============================================ */

@media print {
  * {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
    background: none !important;
  }

  html,
  body {
    width: 80mm;
    height: auto;
    background: white !important;
    color: black !important;
  }

  #root {
    width: 80mm;
    height: auto;
    overflow: visible;
  }

  .receipt-container {
    width: 80mm;
    padding: 0;
    margin: 0;
    background: white !important;
    box-shadow: none;
  }

  .receipt {
    width: 80mm;
    box-shadow: none;
    padding: 0;
    margin: 0;
    page-break-after: avoid;
  }

  /* Hide everything except receipt */
  body > *:not(#root) {
    display: none !important;
  }

  #root > *:not(.receipt-container) {
    display: none !important;
  }

  /* Page setup for thermal printer */
  @page {
    width: 80mm;
    height: auto;
    margin: 0;
    padding: 0;
  }
}
```

---

## File 5: package.json (UPDATED - dependency added)

```json
{
  "name": "order-system-frontend",
  "private": true,
  "version": "0.0.0",
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "vite build",
    "lint": "eslint .",
    "preview": "vite preview"
  },
  "dependencies": {
    "react": "^19.2.0",
    "react-dom": "^19.2.0",
    "react-router-dom": "^6.x.x"
  },
  "devDependencies": {
    "@eslint/js": "^9.39.1",
    "@types/react": "^19.2.5",
    "@types/react-dom": "^19.2.3",
    "@vitejs/plugin-react": "^5.1.1",
    "eslint": "^9.39.1",
    "eslint-plugin-react-hooks": "^7.0.1",
    "eslint-plugin-react-refresh": "^0.4.24",
    "globals": "^16.5.0",
    "vite": "^7.2.4"
  }
}
```

---

## Verification Checklist

### Code Changes ✓
- [ ] PrintReceiptPage.jsx created with full implementation
- [ ] App.jsx updated with Router and Routes
- [ ] OrderCard.jsx updated with handlePrint function
- [ ] OrderCard.jsx updated with Print button
- [ ] styles.css updated with receipt styles
- [ ] styles.css updated with print media styles
- [ ] styles.css updated with print button styles
- [ ] package.json shows react-router-dom as dependency

### Dependencies ✓
- [ ] Ran: `npm install react-router-dom`
- [ ] No errors during installation
- [ ] node_modules/react-router-dom/ exists

### Functionality ✓
- [ ] Print button visible on each order card
- [ ] Click Print opens new window with receipt
- [ ] Receipt displays order data correctly
- [ ] Auto-print dialog opens after page loads
- [ ] Print preview shows 80mm width
- [ ] Print CSS hides UI elements
- [ ] Receipt prints correctly on 80mm printer

### Browser Testing ✓
- [ ] Works in Chrome
- [ ] Works in Firefox
- [ ] Works in Edge
- [ ] Print dialog auto-opens
- [ ] Print preview shows correct layout
- [ ] Popup window opens properly

### Printer Testing ✓
- [ ] Select 80mm thermal printer
- [ ] Receipt prints full width
- [ ] Text is readable
- [ ] All items display
- [ ] Total is correct
- [ ] No UI elements print

---

## Installation & Testing Steps

1. **Install dependency:**
   ```bash
   cd order-system-frontend
   npm install react-router-dom
   ```

2. **Create PrintReceiptPage.jsx:**
   - Copy code from File 1 above
   - Save to: `src/pages/PrintReceiptPage.jsx`

3. **Update App.jsx:**
   - Replace with code from File 2 above

4. **Update OrderCard.jsx:**
   - Add handlePrint function
   - Add Print button to order-actions

5. **Update styles.css:**
   - Add receipt styles section
   - Add print button styles
   - Add @media print section

6. **Test in browser:**
   ```bash
   npm run dev
   ```
   - Open http://localhost:5173
   - Click Print on any order
   - Verify receipt displays
   - Verify print dialog opens

7. **Test with printer:**
   - Select 80mm thermal printer
   - Print test receipt
   - Verify output quality
   - Adjust receipt padding if needed

---

## Troubleshooting Code Issues

### Issue: "Module not found: react-router-dom"
```
Solution: npm install react-router-dom
```

### Issue: Print button doesn't work
```
Check:
1. handlePrint function exists in OrderCard
2. onClick={handlePrint} on button
3. window.open() is not blocked by popup blocker
4. Route /staff/print/:orderId exists in App.jsx
```

### Issue: Receipt doesn't display
```
Check:
1. PrintReceiptPage.jsx exists in src/pages/
2. useParams() correctly gets orderId
3. fetchOrdersByStatus returns data
4. Order ID in URL matches order in state
5. Console for fetch errors
```

### Issue: Print dialog doesn't auto-open
```
Check:
1. setTimeout delay is 300ms
2. useEffect depends on [order, loading]
3. window.print() is called in useEffect
4. Browser allows window.print()
5. No console errors
```

### Issue: Receipt styling wrong
```
Check:
1. .receipt-container has width: 80mm
2. .receipt has width: 80mm
3. @media print styles applied
4. Font size is readable (13px)
5. Colors show in preview
```

---

## Quick Reference: Adding Print to New Components

If you need to add print to other order displays:

```jsx
// 1. Add handler
const handlePrint = () => {
  window.open(`/staff/print/${order.orderId}`, '_blank', 'width=400,height=600');
};

// 2. Add button
<button className="btn btn-print" onClick={handlePrint}>
  Print
</button>

// 3. Ensure styles.css has:
//    - .btn-print styles
//    - Receipt styles
//    - @media print styles
```

---

## Complete File Structure After Implementation

```
order-system-frontend/
├── src/
│   ├── pages/
│   │   ├── StaffOrdersPage.jsx       (existing)
│   │   ├── CustomerMenuPage.jsx      (existing)
│   │   └── PrintReceiptPage.jsx      (NEW)
│   ├── components/
│   │   ├── OrderCard.jsx             (UPDATED: +Print button)
│   │   └── ...
│   ├── api/
│   │   ├── allOrdersApi.js           (existing)
│   │   └── ...
│   ├── App.jsx                        (UPDATED: +Router)
│   ├── main.jsx                       (existing)
│   ├── styles.css                     (UPDATED: +receipt styles)
│   └── ...
├── package.json                       (UPDATED: +react-router-dom)
├── vite.config.js
├── index.html
└── ...
```
