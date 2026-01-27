export default function OrderCard({ order, onAccept, onMarkReady, isProcessing }) {
  const handleAccept = () => {
    if (!isProcessing) {
      onAccept(order.orderId);
    }
  };

  const handleMarkReady = () => {
    if (!isProcessing) {
      onMarkReady(order.orderId);
    }
  };

  // Open print receipt in new window
  const handlePrint = () => {
    window.open(`/staff/print/${order.orderId}`, '_blank', 'width=400,height=600');
  };

  // Format time
  const formatTime = (dateString) => {
    try {
      const date = new Date(dateString);
      return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    } catch (e) {
      return dateString;
    }
  };

  // Calculate item subtotal
  const getItemSubtotal = (unitPrice, qty) => {
    return (unitPrice * qty).toFixed(2);
  };

  /**
   * Convert options map to array if needed
   * Backend returns: { "Chicken": 8.99, "Beef": 9.99 }
   * Converts to: [ { name: "Chicken", price: 8.99 }, ... ]
   */
  const normalizeOptions = (optionsMap) => {
    if (!optionsMap || typeof optionsMap !== 'object') return [];
    const entries = Object.entries(optionsMap);
    if (entries.length === 0) return [];
    
    return entries.map(([name, price]) => ({
      name,
      price: typeof price === 'number' ? price : parseFloat(price) || 0,
    }));
  };

  /**
   * Convert notes map to array if needed
   * Backend returns: { "Add rice": 1.0, "No onions": 0.0 }
   * Converts to: [ { name: "Add rice", price: 1.0 }, ... ]
   */
  const normalizeNotes = (notesMap) => {
    if (!notesMap || typeof notesMap !== 'object') return [];
    const entries = Object.entries(notesMap);
    if (entries.length === 0) return [];
    
    return entries.map(([name, price]) => ({
      name,
      price: typeof price === 'number' ? price : parseFloat(price) || 0,
    }));
  };

  /**
   * Render chosen option: "Option: Chicken"
   * Reads from item.chosenOption (what customer actually selected)
   */
  const renderChosenOption = (item) => {
    if (!item.chosenOption || !item.chosenOption.trim()) return null;
    return `Option: ${item.chosenOption.trim()}`;
  };

  /**
   * Format available options display: "Available: Chicken ($8.99), Beef ($9.99)"
   * Reads from menuItem.options (map object from backend)
   * Shows only if there are options to display (secondary info)
   */
   
  const renderAvailableOptions = (item) => {
    const optionsArray = normalizeOptions(item.menuItem?.options);
    if (optionsArray.length === 0) return null;
    
    const optionsText = optionsArray
      .map((opt) => `${opt.name} ($${parseFloat(opt.price).toFixed(2)})`)
      .join(', ');
    
    return `Available: ${optionsText}`;
  };

  /**
   * Render chosen note: "Note: No onions"
   * Reads from item.chosenNote (what customer actually selected)
   */
  const renderChosenNote = (item) => {
    if (!item.chosenNote || !item.chosenNote.trim()) return null;
    return `Note: ${item.chosenNote.trim()}`;
  };

  /**
   * Format available notes display: "Available: Add rice (+$1.00), No onions (+$0.00)"
   * Reads from menuItem.notes (map object from backend)
   * Shows only if there are notes to display (secondary info)
   */
  
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

  return (
    <div className="order-card">
      {/* Header */}
      <div className="order-card-header">
        <div className="order-id-section">
          <span className="order-label">Order</span>
          <span className="order-id">#{order.orderId}</span>
        </div>
        <div className="order-time">
          {formatTime(order.createdAt)}
        </div>
      </div>

      {/* Table Number - Prominent */}
      <div className="table-section">
        <span className="table-label">Table</span>
        <span className="table-number">{order.tableNo}</span>
      </div>

      {/* Note */}
      {order.note && (
        <div className="order-note">
          <span className="note-label">Note:</span>
          <span className="note-text">{order.note}</span>
        </div>
      )}

      {/* Items */}
      <div className="order-items">
        <div className="items-header">
          <span className="items-title">Items</span>
        </div>
        {order.items && order.items.length > 0 ? (
          <div className="items-list">
            {order.items.map((item, index) => (
              <div key={index} className="order-item">
                <div className="item-name-qty">
                  <div className="item-name-wrapper">
                    <span className="item-name">{item.itemName}</span>
                    {item.customerName && item.customerName.trim() && (
                      <span className="item-customer-name">For: {item.customerName.trim()}</span>
                    )}
                    {renderChosenOption(item) && (
                      <span className="item-chosen-option">{renderChosenOption(item)}</span>
                    )}
                    {renderChosenNote(item) && (
                      <span className="item-chosen-note">{renderChosenNote(item)}</span>
                    )}
                    {/*{renderAvailableOptions(item) && (
                      <span className="item-available-options">{renderAvailableOptions(item)}</span>
                    )}*/}
                    {/*renderAvailableNotes(item) && (
                      <span className="item-available-notes">{renderAvailableNotes(item)}</span>
                    )*/}
                  </div>
                  <span className="item-qty">x{item.qty}</span>
                </div>
                <div className="item-price-info">
                  <span className="item-unit-price">
                    ${parseFloat(item.unitPrice).toFixed(2)}
                  </span>
                  <span className="item-subtotal">
                    ${getItemSubtotal(item.unitPrice, item.qty)}
                  </span>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <p className="no-items">No items</p>
        )}
      </div>

      {/* Total */}
      <div className="order-total">
        <span>Total</span>
        <span className="total-amount">${parseFloat(order.total).toFixed(2)}</span>
      </div>

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
    </div>
  );
}
