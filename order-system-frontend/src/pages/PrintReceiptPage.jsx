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
