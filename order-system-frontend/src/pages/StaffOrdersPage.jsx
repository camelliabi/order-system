import { useState, useEffect, useRef } from 'react';
import { fetchOrdersByStatus, patchOrderStatus } from '../api/allOrdersApi';
import OrderCard from '../components/OrderCard';

const STATUS_OPTIONS = ['NEW', 'ACCEPTED', 'READY'];
const POLL_INTERVAL = 3000; // 3 seconds

export default function StaffOrdersPage() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [status, setStatus] = useState('NEW');
  const [processingOrderId, setProcessingOrderId] = useState(null);
  const [lastUpdated, setLastUpdated] = useState(null);

  const abortControllerRef = useRef(null);
  const pollIntervalRef = useRef(null);

  // Load orders
  const loadOrders = async (selectedStatus) => {
    try {
      // Cancel previous request if in progress
      if (abortControllerRef.current) {
        abortControllerRef.current.abort();
      }

      setError(null);
      const data = await fetchOrdersByStatus(selectedStatus);
      // DEBUG: Log orders to verify customerName is present
      if (data.length > 0 && data[0].items.length > 0) {
        console.log('[DEBUG StaffOrdersPage] First order:', data[0]);
      }
      setOrders(data);
      setLastUpdated(new Date());
    } catch (err) {
      if (err.name !== 'AbortError') {
        setError('Failed to load orders. Please try again.');
        console.error(err);
      }
    } finally {
      setLoading(false);
    }
  };


  // Setup auto-refresh poll
  useEffect(() => {
    // Initial load
    loadOrders(status);

    // Setup poll
    pollIntervalRef.current = setInterval(() => {
      loadOrders(status);
    }, POLL_INTERVAL);

    // Cleanup
    return () => {
      if (pollIntervalRef.current) {
        clearInterval(pollIntervalRef.current);
      }
      if (abortControllerRef.current) {
        abortControllerRef.current.abort();
      }
    };
  }, [status]);

  // Handle status change
  const handleStatusChange = (e) => {
    const newStatus = e.target.value;
    setStatus(newStatus);
    setLoading(true);
  };

  // Accept order
  const handleAcceptOrder = async (orderId) => {
    setProcessingOrderId(orderId);
    try {
      await patchOrderStatus(orderId, 'ACCEPTED');
      // Refresh orders after successful update
      await loadOrders(status);
    } catch (err) {
      setError('Failed to accept order. Please try again.');
      console.error(err);
    } finally {
      setProcessingOrderId(null);
    }
  };

  // Mark order ready
  const handleMarkOrderReady = async (orderId) => {
    setProcessingOrderId(orderId);
    try {
      await patchOrderStatus(orderId, 'READY');
      // Refresh orders after successful update
      await loadOrders(status);
    } catch (err) {
      setError('Failed to mark order ready. Please try again.');
      console.error(err);
    } finally {
      setProcessingOrderId(null);
    }
  };

  // Format time for display
  const formatTime = (dateObj) => {
    if (!dateObj) return 'Never';
    try {
      return dateObj.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', second: '2-digit' });
    } catch (e) {
      return 'N/A';
    }
  };

  return (
    <div className="app-shell staff-orders-page">
      {/* Header */}
      <header className="staff-header">
        <h1>New Orders</h1>
        <p className="staff-subtitle">Kitchen Management</p>
      </header>

      {/* Main Content */}
      <main className="container container--staff staff-content">
        {/* Filter Section */}
        <div className="filter-section">
          <label htmlFor="status-filter" className="filter-label">
            Filter by Status:
          </label>
          <select
            id="status-filter"
            className="status-filter"
            value={status}
            onChange={handleStatusChange}
          >
            {STATUS_OPTIONS.map((opt) => (
              <option key={opt} value={opt}>
                {opt}
              </option>
            ))}
          </select>
          <span className="order-count">
            {orders.length} order{orders.length !== 1 ? 's' : ''}
          </span>
        </div>

        {/* Dashboard Layout: Left (Orders) + Right (Summary) */}
        <div className="dashboard-wrapper">
          {/* Left Panel: Orders List */}
          <div className="orders-panel">
            {/* Error Message */}
            {error && <div className="error-message">{error}</div>}

            {/* Loading State */}
            {loading && !orders.length && (
              <div className="loading-state">
                <p>Loading orders...</p>
              </div>
            )}

            {/* Orders List */}
            {!loading && orders.length === 0 && (
              <div className="no-orders">
                <p>No orders found for status "{status}".</p>
              </div>
            )}
            {!loading && orders.length > 0 && (
              <div className="orders-list">
                {orders.map((order) => ( 
                    <OrderCard 
                      key={order.orderId} 
                      order={order}
                      onAccept={handleAcceptOrder}
                      onMarkReady={handleMarkOrderReady}
                      isProcessing={processingOrderId === order.orderId}
                    />
                ))}
              </div>
            )}
          </div>

          {/* Right Panel: Summary (visible on desktop only) */}
          <div className="summary-panel">
            <h3>Summary</h3>
            <div className="summary-stat">
              <span className="summary-label">Total Orders</span>
              <span className="summary-value">{orders.length}</span>
            </div>
            <div className="summary-stat">
              <span className="summary-label">Current Status</span>
              <span className="summary-value">{status}</span>
            </div>
            <div className="summary-stat">
              <span className="summary-label">Auto-Refresh</span>
              <span className="summary-value">3s</span>
            </div>
            <div className="summary-stat">
              <span className="summary-label">Last Updated</span>
              <span className="summary-value">{formatTime(lastUpdated)}</span>
            </div>
          </div>
        </div>
      </main>
    </div>
  );
}
