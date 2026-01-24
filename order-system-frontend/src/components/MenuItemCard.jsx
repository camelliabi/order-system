import { useState } from 'react';
import AddToCartModal from './AddToCartModal';

export default function MenuItemCard({ item, onAddToCart }) {
  const [showModal, setShowModal] = useState(false);

  const handleAddClick = () => {
    if (!item.soldout) {
      setShowModal(true);
    }
  };

  const handleModalClose = () => {
    setShowModal(false);
  };

  const handleModalConfirm = (cartItem) => {
    onAddToCart(cartItem);
    setShowModal(false);
  };

  return (
    <div className="menu-item-card">
      {/* Image Area */}
      <div className="menu-item-image-area">
        {item.imageUrl ? (
          <img src={item.imageUrl} alt={item.itemName} className="menu-item-image" />
        ) : (
          <div className="menu-item-placeholder">
            <span>No Image</span>
          </div>
        )}
      </div>

      {/* Content Area */}
      <div className="menu-item-content">
        <div className="item-header">
          <h3 className="item-name">{item.itemName}</h3>
          {item.soldout && <span className="soldout-badge">SOLD OUT</span>}
        </div>

        {item.itemDesc && <p className="item-desc">{item.itemDesc}</p>}

        {/* Options Preview */}
        {item.options && item.options.length > 0 && (
          <div className="item-options-preview">
            <p className="preview-label">Options:</p>
            <div className="options-list">
              {item.options.map((option, idx) => (
                <span key={idx} className="option-tag">
                  {option.name} ${parseFloat(option.price).toFixed(2)}
                </span>
              ))}
            </div>
          </div>
        )}

        {/* Notes Preview */}
        {item.notes && item.notes.length > 0 && (
          <div className="item-notes-preview">
            <p className="preview-label">Add-ons:</p>
            <div className="notes-list">
              {item.notes.map((note, idx) => (
                <span key={idx} className="note-tag">
                  {note.name}
                  {note.price > 0 && ` +$${parseFloat(note.price).toFixed(2)}`}
                </span>
              ))}
            </div>
          </div>
        )}

        <div className="item-footer">
          <span className="item-price">
            ${parseFloat(item.itemPrice).toFixed(2)}
          </span>
          <button
            className="add-btn"
            onClick={handleAddClick}
            disabled={item.soldout}
          >
            Add
          </button>
        </div>
      </div>

      {/* Add to Cart Modal */}
      {showModal && (
        <AddToCartModal item={item} onClose={handleModalClose} onConfirm={handleModalConfirm} />
      )}
    </div>
  );
}
