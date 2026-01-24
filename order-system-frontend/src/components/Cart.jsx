export default function Cart({
  items,
  cartTotal,
  tableNo,
  note,
  onTableNoChange,
  onNoteChange,
  onQuantityChange,
  onPlaceOrder,
}) {
  return (
    <section className="cart-section">
      <h2>Cart</h2>

      {items.length === 0 ? (
        <p className="empty-cart">Your cart is empty</p>
      ) : (
        <>
          <div className="cart-items">
            {items.map((item, idx) => (
              <div
                key={`${item.itemId}-${item.selectedOption?.name || 'none'}-${item.selectedNotes?.map((n) => n.name).join('-') || 'none'}`}
                className="cart-item"
              >
                <div className="cart-item-info">
                  <h4>{item.itemName}</h4>

                  {/* Display customer name if available */}
                  {item.customerName && (
                    <p className="item-customer-name">For: {item.customerName}</p>
                  )}

                  {/* Display selected option */}
                  {item.selectedOption && (
                    <p className="item-option">Option: {item.selectedOption.name}</p>
                  )}

                  {/* Display selected notes */}
                  {item.selectedNotes && item.selectedNotes.length > 0 && (
                    <div className="item-notes">
                      <p className="notes-label">Add-ons:</p>
                      <ul className="notes-list">
                        {item.selectedNotes.map((note, noteIdx) => (
                          <li key={noteIdx} className="note-item">
                            {note.name}
                            {note.price > 0 && <span className="note-price"> +${parseFloat(note.price).toFixed(2)}</span>}
                          </li>
                        ))}
                      </ul>
                    </div>
                  )}

                  <p className="item-unit-price">
                    ${parseFloat(item.computedUnitPrice || item.itemPrice).toFixed(2)} each
                  </p>
                </div>

                <div className="cart-item-controls">
                  <button
                    className="qty-btn"
                    onClick={() => onQuantityChange(item.itemId, item.qty - 1)}
                  >
                    −
                  </button>
                  <span className="qty-display">{item.qty}</span>
                  <button
                    className="qty-btn"
                    onClick={() => onQuantityChange(item.itemId, item.qty + 1)}
                  >
                    +
                  </button>
                </div>

                <div className="cart-item-total">
                  ${((item.computedUnitPrice || item.itemPrice) * item.qty).toFixed(2)}
                </div>
              </div>
            ))}
          </div>

          <div className="cart-summary">
            <div className="total-row">
              <span>Total:</span>
              <span className="total-price">${cartTotal.toFixed(2)}</span>
            </div>
          </div>
        </>
      )}

      {/* Order Form */}
      <div className="order-form">
        <div className="form-group">
          <label htmlFor="table-no">Table Number</label>
          <input
            id="table-no"
            type="text"
            value={tableNo}
            onChange={(e) => onTableNoChange(e.target.value)}
            placeholder="e.g., A1"
          />
        </div>

        <div className="form-group">
          <label htmlFor="note">Special Instructions (Optional)</label>
          <textarea
            id="note"
            value={note}
            onChange={(e) => onNoteChange(e.target.value)}
            placeholder="e.g., no onion, extra spicy"
            rows="3"
          />
        </div>

        <button
          className="place-order-btn"
          onClick={onPlaceOrder}
          disabled={items.length === 0}
        >
          Place Order
        </button>
      </div>
    </section>
  );
}
