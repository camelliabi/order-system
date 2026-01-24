import { useState } from 'react';

export default function AddToCartModal({ item, onClose, onConfirm }) {
  const [selectedOptionName, setSelectedOptionName] = useState(null);
  const [selectedNoteNames, setSelectedNoteNames] = useState([]);
  const [customerName, setCustomerName] = useState('');

  // Calculate price based on selections
  const calculatePrice = () => {
    let basePrice = item.itemPrice;

    // If option selected, use option price instead of base price
    if (selectedOptionName) {
      const option = (item.options || []).find((opt) => opt.name === selectedOptionName);
      if (option) {
        basePrice = option.price;
      }
    }

    // Add note prices
    let notesPrice = 0;
    if (selectedNoteNames.length > 0) {
      notesPrice = selectedNoteNames.reduce((sum, noteName) => {
        const note = (item.notes || []).find((n) => n.name === noteName);
        return sum + (note ? note.price : 0);
      }, 0);
    }

    return basePrice + notesPrice;
  };

  // Toggle note selection
  const handleNoteToggle = (noteName) => {
    setSelectedNoteNames((prev) =>
      prev.includes(noteName) ? prev.filter((name) => name !== noteName) : [...prev, noteName]
    );
  };

  // Handle add to cart
  const handleAddToCart = () => {
    const computedUnitPrice = calculatePrice();
    
    // Find the selected option object
    const selectedOptionObj = selectedOptionName
      ? (item.options || []).find((opt) => opt.name === selectedOptionName)
      : null;

    // Build selected notes array
    const selectedNotesArray = selectedNoteNames.map((noteName) => {
      const note = (item.notes || []).find((n) => n.name === noteName);
      return { name: noteName, price: note?.price || 0 };
    });

    const cartItem = {
      itemId: item.itemId,
      itemName: item.itemName,
      basePriceUsed: selectedOptionObj ? selectedOptionObj.price : item.itemPrice,
      selectedOption: selectedOptionObj
        ? {
            name: selectedOptionObj.name,
            price: selectedOptionObj.price,
          }
        : null,
      selectedNotes: selectedNotesArray,
      customerName: customerName || null,
      computedUnitPrice,
      qty: 1,
    };

    onConfirm(cartItem);
  };

  const computedPrice = calculatePrice();
  const hasOptions = item.options && item.options.length > 0;
  const hasNotes = item.notes && item.notes.length > 0;

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        {/* Modal Header */}
        <div className="modal-header">
          <h2>{item.itemName}</h2>
          <button className="modal-close" onClick={onClose}>
            ✕
          </button>
        </div>

        {/* Modal Body */}
        <div className="modal-body">
          {/* Options Section */}
          {hasOptions && (
            <div className="modal-section">
              <h3 className="modal-section-title">Choose Size/Option</h3>
              <div className="modal-options">
                {item.options.map((option, idx) => (
                  <label key={idx} className="option-label">
                    <input
                      type="radio"
                      name="option"
                      value={option.name}
                      checked={selectedOptionName === option.name}
                      onChange={(e) => setSelectedOptionName(e.target.value)}
                    />
                    <span className="option-text">
                      {option.name}
                      <span className="option-price">
                        ${parseFloat(option.price).toFixed(2)}
                      </span>
                    </span>
                  </label>
                ))}
              </div>
            </div>
          )}

          {/* Notes Section */}
          {hasNotes && (
            <div className="modal-section">
              <h3 className="modal-section-title">Add-ons</h3>
              <div className="modal-notes">
                {item.notes.map((note, idx) => (
                  <label key={idx} className="note-label">
                    <input
                      type="checkbox"
                      checked={selectedNoteNames.includes(note.name)}
                      onChange={() => handleNoteToggle(note.name)}
                    />
                    <span className="note-text">
                      {note.name}
                      {note.price > 0 && (
                        <span className="note-price">+${parseFloat(note.price).toFixed(2)}</span>
                      )}
                    </span>
                  </label>
                ))}
              </div>
            </div>
          )}

          {/* Customer Name Section */}
          <div className="modal-section">
            <label htmlFor="customer-name" className="modal-section-title">
              Customer Name (Optional)
            </label>
            <input
              id="customer-name"
              type="text"
              value={customerName}
              onChange={(e) => setCustomerName(e.target.value)}
              placeholder="Your name"
              className="modal-input"
            />
          </div>
        </div>

        {/* Price Preview */}
        <div className="modal-price-preview">
          <span className="preview-label">Item Total:</span>
          <span className="preview-price">${computedPrice.toFixed(2)}</span>
        </div>

        {/* Modal Footer */}
        <div className="modal-footer">
          <button className="modal-btn modal-btn-cancel" onClick={onClose}>
            Cancel
          </button>
          <button className="modal-btn modal-btn-confirm" onClick={handleAddToCart}>
            Add to Cart
          </button>
        </div>
      </div>
    </div>
  );
}
