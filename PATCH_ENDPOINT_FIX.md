# PATCH Endpoint Implementation - Order Status Updates

## Problem Statement

The frontend "Accept" and "Mark Ready" buttons were **non-functional** because the backend was missing the PATCH endpoint that the frontend was calling.

### Frontend Expected Endpoint
```javascript
// allOrdersApi.js
await fetch(`/api/all_orders/${orderId}`, {
  method: 'PATCH',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ status: newStatus }),
});
```

### Backend Reality
❌ **No PATCH endpoint existed** - resulted in 404 errors

---

## Solution Implemented

### 1. Added PATCH Endpoint
**File**: `src/main/java/com/camellia/ordersystem/controller/AllOrdersController.java`

```java
@PatchMapping("/all_orders/{orderId}")
public ResponseEntity<?> updateOrderStatus(
        @PathVariable int orderId, 
        @RequestBody Map<String, String> updates) {
    
    // Validates request, finds order, updates status, returns updated order
}
```

### 2. Added In-Memory Order Storage
**Why**: Without a database, orders need to persist between requests

```java
private static final Map<Integer, Order> orderStore = new ConcurrentHashMap<>();

static {
    initializeOrders(); // Populate on startup
}
```

**Benefits**:
- ✅ Orders persist during server runtime
- ✅ Thread-safe (ConcurrentHashMap)
- ✅ Status updates are retained
- ✅ GET requests return updated data
- ⚠️ **Note**: Data still lost on server restart (database needed for true persistence)

### 3. Added Order Timestamps
**File**: `src/main/java/com/camellia/ordersystem/order/Order.java`

```java
private LocalDateTime createdAt;

public LocalDateTime getCreatedAt() {
    return createdAt;
}

public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
}
```

**Purpose**: Frontend displays order creation time

---

## API Specification

### PATCH /api/all_orders/{orderId}

**Request**:
```http
PATCH /api/all_orders/101 HTTP/1.1
Content-Type: application/json

{
  "status": "ACCEPTED"
}
```

**Success Response** (200 OK):
```json
{
  "orderId": 101,
  "tableId": "A1",
  "totalPrice": 47.44,
  "orderStatus": "ACCEPTED",
  "createdAt": "2026-01-28T10:30:00",
  "orderItems": [...]
}
```

**Error Responses**:

**400 Bad Request** - Missing status:
```json
{
  "error": "Missing 'status' field in request body"
}
```

**400 Bad Request** - Invalid status:
```json
{
  "error": "Invalid status. Allowed values: NEW, ACCEPTED, READY, COMPLETED, CANCELLED"
}
```

**404 Not Found** - Order doesn't exist:
```json
{
  "error": "Order not found with ID: 999"
}
```

---

## Valid Status Values

| Status | Description | Usage |
|--------|-------------|-------|
| `NEW` | Order just placed | Initial state |
| `ACCEPTED` | Kitchen accepted order | Click "Accept" button |
| `READY` | Order ready for pickup | Click "Mark Ready" button |
| `COMPLETED` | Order delivered | Future use |
| `CANCELLED` | Order cancelled | Future use |

---

## Status Transition Flow

```
NEW → ACCEPTED → READY → COMPLETED
  ↓
CANCELLED (can cancel from any state)
```

**Frontend Workflow**:
1. Customer places order → Status: `NEW`
2. Staff clicks "Accept" → Status: `ACCEPTED`
3. Staff clicks "Mark Ready" → Status: `READY`
4. Customer picks up → Status: `COMPLETED` (future)

---

## How It Works

### Request Flow

```
┌─────────────────────────────────────────────────┐
│  Staff Orders Page (Frontend)                   │
│  - Click "Accept" button                        │
│  - Calls: handleAcceptOrder(orderId)           │
└────────────────┬────────────────────────────────┘
                 │
                 ↓
┌─────────────────────────────────────────────────┐
│  allOrdersApi.js                                │
│  - patchOrderStatus(orderId, 'ACCEPTED')       │
│  - Sends: PATCH /api/all_orders/101            │
│           Body: { "status": "ACCEPTED" }       │
└────────────────┬────────────────────────────────┘
                 │
                 ↓
┌─────────────────────────────────────────────────┐
│  AllOrdersController (Backend)                  │
│  - Receives PATCH request                       │
│  - Validates status value                       │
│  - Finds order in orderStore                    │
│  - Updates order.setOrderStatus("ACCEPTED")    │
│  - Returns updated order                        │
└────────────────┬────────────────────────────────┘
                 │
                 ↓
┌─────────────────────────────────────────────────┐
│  Frontend Receives Response                     │
│  - Refreshes order list                         │
│  - Order card now shows "ACCEPTED" status       │
│  - Button changes or order moves to new filter  │
└─────────────────────────────────────────────────┘
```

---

## Testing the Fix

### Prerequisites
1. Start Spring Boot backend:
   ```bash
   cd order-system
   ./mvnw spring-boot:run
   ```
   Backend runs on: http://localhost:8082

2. Start React frontend:
   ```bash
   cd order-system-frontend
   npm run dev
   ```
   Frontend runs on: http://localhost:5173

### Test Case 1: Accept Order
1. Open http://localhost:5173/
2. You should see 2 orders (Order #101 and #102)
3. Click **"Accept"** button on Order #101
4. **Expected**: Button shows "Processing..." then order updates
5. **Verify**: 
   - Check browser console for success message
   - Order should still appear if filter is "NEW" → "ACCEPTED"
   - Change filter dropdown to "ACCEPTED" to see the order

### Test Case 2: Mark Order Ready
1. Filter by status: "ACCEPTED"
2. Click **"Mark Ready"** button on an accepted order
3. **Expected**: Order status changes to "READY"
4. **Verify**: Change filter to "READY" to see the order

### Test Case 3: Error Handling
**Test Invalid Order ID**:
```bash
curl -X PATCH http://localhost:8082/api/all_orders/999 \
  -H "Content-Type: application/json" \
  -d '{"status": "ACCEPTED"}'
```
**Expected**: 404 error with message "Order not found"

**Test Invalid Status**:
```bash
curl -X PATCH http://localhost:8082/api/all_orders/101 \
  -H "Content-Type: application/json" \
  -d '{"status": "INVALID_STATUS"}'
```
**Expected**: 400 error with allowed values message

**Test Missing Status**:
```bash
curl -X PATCH http://localhost:8082/api/all_orders/101 \
  -H "Content-Type: application/json" \
  -d '{}'
```
**Expected**: 400 error "Missing 'status' field"

### Test Case 4: Verify Persistence
1. Click "Accept" on Order #101
2. Wait for auto-refresh (3 seconds)
3. **Expected**: Order still shows as "ACCEPTED"
4. Manually refresh browser (F5)
5. **Expected**: Order still "ACCEPTED" (persists in memory)
6. **Note**: If you restart the backend, orders reset to "NEW"

---

## Browser Console Testing

Open browser DevTools (F12) and monitor:

**Successful PATCH**:
```
[ORDER STATUS UPDATE] Order #101: NEW -> ACCEPTED
```

**Frontend logs**:
```
[DEBUG StaffOrdersPage] First order: {orderId: 101, orderStatus: "ACCEPTED", ...}
```

---

## Verification Checklist

- [ ] Backend starts without errors
- [ ] Frontend starts without errors
- [ ] Orders display on staff page
- [ ] Click "Accept" button
- [ ] Button shows "Processing..." during request
- [ ] Order status updates successfully
- [ ] No console errors
- [ ] Click "Mark Ready" button
- [ ] Status changes to "READY"
- [ ] Filter dropdown works (can filter by status)
- [ ] Auto-refresh maintains updated status
- [ ] Browser refresh maintains status (until server restart)

---

## Known Limitations

### ⚠️ In-Memory Storage Limitations

1. **Data Loss on Restart**: Orders reset when server restarts
   - **Impact**: All status updates lost
   - **Solution**: Implement database persistence (see Phase 1 of improvement plan)

2. **Not Scalable**: Single server instance only
   - **Impact**: Cannot run multiple backend instances
   - **Solution**: Use shared database

3. **No Persistence**: Orders cannot be retrieved after server shutdown
   - **Impact**: Not suitable for production use
   - **Solution**: Implement JPA with PostgreSQL

### Current Scope
This implementation is a **temporary solution** to make the buttons functional. It's suitable for:
- ✅ Development and testing
- ✅ Demo purposes
- ✅ Understanding the workflow
- ❌ **NOT for production use**

---

## Next Steps

### Immediate (This Works Now)
1. ✅ Test the Accept and Mark Ready buttons
2. ✅ Verify status updates persist during runtime
3. ✅ Use the system for development/testing

### Short-term (Next Priority)
1. Implement database persistence (JPA + PostgreSQL)
2. Add POST endpoint for creating orders
3. Fix price calculation for options/notes
4. Add proper error handling

### Long-term (Production Ready)
1. Add authentication/authorization
2. Implement WebSocket for real-time updates
3. Add comprehensive testing
4. Add monitoring and logging

---

## Code Changes Summary

| File | Changes | Lines Changed |
|------|---------|---------------|
| AllOrdersController.java | Added PATCH endpoint, in-memory storage | +95 lines |
| Order.java | Added createdAt field and methods | +11 lines |

**Total**: 106 lines added, 0 lines removed

---

## Files Modified

1. ✅ `src/main/java/com/camellia/ordersystem/controller/AllOrdersController.java`
   - Added `orderStore` (ConcurrentHashMap)
   - Added `initializeOrders()` method
   - Added `createSampleOrder1()` and `createSampleOrder2()` helper methods
   - Added `@PatchMapping("/all_orders/{orderId}")` endpoint
   - Added validation logic
   - Added helper methods for future use

2. ✅ `src/main/java/com/camellia/ordersystem/order/Order.java`
   - Added `private LocalDateTime createdAt` field
   - Added `getCreatedAt()` getter
   - Added `setCreatedAt()` setter
   - Added import for `java.time.LocalDateTime`

---

## API Examples

### Update Order to ACCEPTED
```bash
curl -X PATCH http://localhost:8082/api/all_orders/101 \
  -H "Content-Type: application/json" \
  -d '{"status": "ACCEPTED"}'
```

### Update Order to READY
```bash
curl -X PATCH http://localhost:8082/api/all_orders/101 \
  -H "Content-Type: application/json" \
  -d '{"status": "READY"}'
```

### Get All Orders (verify status)
```bash
curl http://localhost:8082/api/all_orders
```

---

## Troubleshooting

### Issue: "Order not found" error
**Cause**: Order ID doesn't exist in orderStore
**Solution**: Check available order IDs (101, 102 by default)

### Issue: Status doesn't update in UI
**Cause**: Frontend filter might be hiding the order
**Solution**: Change filter dropdown to match new status

### Issue: Status resets after server restart
**Cause**: In-memory storage is cleared on restart
**Solution**: This is expected behavior until database is implemented

### Issue: 400 error "Invalid status"
**Cause**: Status value not in VALID_STATUSES list
**Solution**: Use one of: NEW, ACCEPTED, READY, COMPLETED, CANCELLED

---

## Future Enhancements

When implementing database persistence, this endpoint will:
1. Query database instead of in-memory map
2. Persist status changes permanently
3. Support multiple backend instances
4. Enable order history and analytics
5. Allow status change auditing (who changed, when)

---

## Status: ✅ READY FOR TESTING

The PATCH endpoint is now implemented and functional. Staff can:
- ✅ Accept new orders
- ✅ Mark orders as ready
- ✅ See status updates in real-time (3-second polling)
- ✅ Filter orders by status

**Test it now** and let me know if you encounter any issues!
