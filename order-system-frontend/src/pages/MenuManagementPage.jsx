import { useState, useEffect } from 'react';
import { fetchMenu, createMenuItem, updateMenuItem } from '../api/menuApi';
import '../styles/MenuManagementPage.css';

export default function MenuManagementPage() {
  const [menuItems, setMenuItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [message, setMessage] = useState('');

  // Form state
  const [selectedItemId, setSelectedItemId] = useState(null);
  const [formData, setFormData] = useState({
    itemName: '',
    itemPrice: '',
    soldout: false,
    options: [],
    notes: [],
  });

  // Load menu on mount
  useEffect(() => {
    loadMenuItems();
  }, []);

  const loadMenuItems = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await fetchMenu();
      setMenuItems(data || []);
    } catch (err) {
      setError('Failed to load menu items');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  // Handle selecting a menu item
  const handleSelectItem = (item) => {
    setSelectedItemId(item.itemId);
    setFormData({
      itemName: item.itemName,
      itemPrice: item.itemPrice.toString(),
      soldout: item.soldout || false,
      options: item.options ? [...item.options] : [],
      notes: item.notes ? [...item.notes] : [],
    });
    setMessage('');
  };

  // Handle creating new item
  const handleNewItem = () => {
    setSelectedItemId(null);
    setFormData({
      itemName: '',
      itemPrice: '',
      soldout: false,
      options: [],
      notes: [],
    });
    setMessage('');
  };

  // Handle form input change
  const handleInputChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value,
    }));
  };

  // Handle option change
  const handleOptionChange = (index, field, value) => {
    setFormData(prev => {
      const newOptions = [...prev.options];
      newOptions[index] = {
        ...newOptions[index],
        [field]: value,
      };
      return { ...prev, options: newOptions };
    });
  };

  // Add new option row
  const handleAddOption = () => {
    setFormData(prev => ({
      ...prev,
      options: [...prev.options, { name: '', price: '' }],
    }));
  };

  // Remove option row
  const handleRemoveOption = (index) => {
    setFormData(prev => ({
      ...prev,
      options: prev.options.filter((_, i) => i !== index),
    }));
  };

  // Handle note change
  const handleNoteChange = (index, field, value) => {
    setFormData(prev => {
      const newNotes = [...prev.notes];
      newNotes[index] = {
        ...newNotes[index],
        [field]: value,
      };
      return { ...prev, notes: newNotes };
    });
  };

  // Add new note row
  const handleAddNote = () => {
    setFormData(prev => ({
      ...prev,
      notes: [...prev.notes, { name: '', price: '' }],
    }));
  };

  // Remove note row
  const handleRemoveNote = (index) => {
    setFormData(prev => ({
      ...prev,
      notes: prev.notes.filter((_, i) => i !== index),
    }));
  };

  // Validate form
  const validateForm = () => {
    if (!formData.itemName.trim()) {
      setMessage('Item name is required');
      return false;
    }
    if (!formData.itemPrice || parseFloat(formData.itemPrice) < 0) {
      setMessage('Item price must be a valid number');
      return false;
    }
    
    // Validate options
    for (let opt of formData.options) {
      if (!opt.name.trim()) {
        setMessage('Option name cannot be empty');
        return false;
      }
      if (isNaN(parseFloat(opt.price))) {
        setMessage('Option price must be a valid number');
        return false;
      }
    }

    // Validate notes
    for (let note of formData.notes) {
      if (!note.name.trim()) {
        setMessage('Note name cannot be empty');
        return false;
      }
      if (isNaN(parseFloat(note.price))) {
        setMessage('Note price must be a valid number');
        return false;
      }
    }

    return true;
  };

  // Handle save
  const handleSave = async () => {
    if (!validateForm()) {
      return;
    }

    try {
      // Convert options and notes to use correct field names for API
      const dataToSend = {
        itemName: formData.itemName.trim(),
        itemPrice: parseFloat(formData.itemPrice),
        soldout: formData.soldout,
        options: formData.options.map(opt => ({
          optionName: opt.name.trim(),
          optionPrice: parseFloat(opt.price),
        })),
        notes: formData.notes.map(note => ({
          noteName: note.name.trim(),
          notePrice: parseFloat(note.price),
        })),
      };

      if (selectedItemId) {
        // Update
        await updateMenuItem(selectedItemId, dataToSend);
        setMessage('Item updated successfully!');
      } else {
        // Create
        await createMenuItem(dataToSend);
        setMessage('Item created successfully!');
      }

      // Reload menu
      await loadMenuItems();
      setSelectedItemId(null);
      setFormData({
        itemName: '',
        itemPrice: '',
        soldout: false,
        options: [],
        notes: [],
      });
    } catch (err) {
      setMessage('Error saving item: ' + (err.message || 'Unknown error'));
      console.error(err);
    }
  };

  if (loading) {
    return <div className="menu-management-container"><p>Loading menu items...</p></div>;
  }

  return (
    <div className="menu-management-container">
      <h1>Menu Management</h1>
      
      <div className="menu-management-layout">
        {/* Left side: Menu items list */}
        <div className="menu-items-section">
          <h2>Menu Items</h2>
          {error && <p className="error-message">{error}</p>}
          
          <button className="btn-new-item" onClick={handleNewItem}>
            + New Item
          </button>

          <div className="items-list">
            {menuItems.map(item => (
              <div
                key={item.itemId}
                className={`item-card ${selectedItemId === item.itemId ? 'active' : ''}`}
                onClick={() => handleSelectItem(item)}
              >
                <div className="item-card-header">
                  <h3>{item.itemName}</h3>
                  {item.soldout && <span className="badge-soldout">Sold Out</span>}
                </div>
                <p className="item-price">${item.itemPrice.toFixed(2)}</p>
              </div>
            ))}
          </div>
        </div>

        {/* Right side: Form */}
        <div className="form-section">
          <h2>{selectedItemId ? 'Edit Item' : 'New Item'}</h2>
          
          {message && (
            <div className={`message ${message.includes('Error') || message.includes('required') || message.includes('must be') ? 'error' : 'success'}`}>
              {message}
            </div>
          )}

          <div className="form-group">
            <label>Item Name *</label>
            <input
              type="text"
              name="itemName"
              value={formData.itemName}
              onChange={handleInputChange}
              placeholder="e.g., Fried Rice"
            />
          </div>

          <div className="form-group">
            <label>Item Price *</label>
            <input
              type="number"
              name="itemPrice"
              value={formData.itemPrice}
              onChange={handleInputChange}
              placeholder="0.00"
              step="0.01"
              min="0"
            />
          </div>

          <div className="form-group">
            <label>
              <input
                type="checkbox"
                name="soldout"
                checked={formData.soldout}
                onChange={handleInputChange}
              />
              Sold Out
            </label>
          </div>

          {/* Options section */}
          <div className="form-section-nested">
            <h3>Options</h3>
            {formData.options.length > 0 && (
              <div className="options-table">
                <div className="table-header">
                  <div className="col-name">Name</div>
                  <div className="col-price">Price</div>
                  <div className="col-action">Action</div>
                </div>
                {formData.options.map((opt, index) => (
                  <div key={index} className="table-row">
                    <input
                      type="text"
                      value={opt.name || ''}
                      onChange={(e) => handleOptionChange(index, 'name', e.target.value)}
                      placeholder="Option name"
                      className="col-name"
                    />
                    <input
                      type="number"
                      value={opt.price || ''}
                      onChange={(e) => handleOptionChange(index, 'price', e.target.value)}
                      placeholder="0.00"
                      step="0.01"
                      min="0"
                      className="col-price"
                    />
                    <button
                      className="btn-remove"
                      onClick={() => handleRemoveOption(index)}
                    >
                      Remove
                    </button>
                  </div>
                ))}
              </div>
            )}
            <button className="btn-add-row" onClick={handleAddOption}>
              + Add Option
            </button>
          </div>

          {/* Notes section */}
          <div className="form-section-nested">
            <h3>Notes</h3>
            {formData.notes.length > 0 && (
              <div className="notes-table">
                <div className="table-header">
                  <div className="col-name">Name</div>
                  <div className="col-price">Price</div>
                  <div className="col-action">Action</div>
                </div>
                {formData.notes.map((note, index) => (
                  <div key={index} className="table-row">
                    <input
                      type="text"
                      value={note.name || ''}
                      onChange={(e) => handleNoteChange(index, 'name', e.target.value)}
                      placeholder="Note name"
                      className="col-name"
                    />
                    <input
                      type="number"
                      value={note.price || ''}
                      onChange={(e) => handleNoteChange(index, 'price', e.target.value)}
                      placeholder="0.00"
                      step="0.01"
                      min="0"
                      className="col-price"
                    />
                    <button
                      className="btn-remove"
                      onClick={() => handleRemoveNote(index)}
                    >
                      Remove
                    </button>
                  </div>
                ))}
              </div>
            )}
            <button className="btn-add-row" onClick={handleAddNote}>
              + Add Note
            </button>
          </div>

          {/* Save button */}
          <button className="btn-save" onClick={handleSave}>
            {selectedItemId ? 'Update Item' : 'Create Item'}
          </button>
        </div>
      </div>
    </div>
  );
}
