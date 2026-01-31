import { useState, useEffect } from 'react';
import { fetchMenu } from '../api/menuApi';
import MenuList from '../components/MenuList';
import Cart from '../components/Cart';

export default function CustomerMenuPage() {
  const [menuItems, setMenuItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [cart, setCart] = useState([]);
  const [tableNo, setTableNo] = useState('A1');
  const [note, setNote] = useState('');

  // Fetch menu on component mount
  useEffect(() => {
    const loadMenu = async () => {
      try {
        setLoading(true);
        setError(null);
        const data = await fetchMenu();
        setMenuItems(data || []);
      } catch (err) {
        setError('Failed to load menu. Please try again later.');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    loadMenu();
  }, []);


  const handleAddToCart = (cartItem) => {
  // itemId 必须存在
  if (!cartItem?.itemId) return;

  // soldout 判断：看 cartItem 上有没有 soldout 字段
  if (cartItem.soldout === true || cartItem.isSoldout === true) return;

  setCart((prevCart) => {
    const existingItemIndex = prevCart.findIndex(
      (ci) =>
        ci.itemId === cartItem.itemId &&
        JSON.stringify(ci.selectedOption) === JSON.stringify(cartItem.selectedOption) &&
        JSON.stringify(ci.selectedNotes) === JSON.stringify(cartItem.selectedNotes) &&
        ci.customerName === cartItem.customerName
    );

    if (existingItemIndex > -1) {
      const updatedCart = [...prevCart];
      updatedCart[existingItemIndex] = {
        ...updatedCart[existingItemIndex],
        qty: updatedCart[existingItemIndex].qty + cartItem.qty,
      };
      return updatedCart;
    }

    return [...prevCart, cartItem];
  });
};


  // Update quantity
  const handleQuantityChange = (itemId, newQty) => {
    if (newQty <= 0) {
      // Remove item from cart if qty is 0
      setCart((prevCart) => prevCart.filter((item) => item.itemId !== itemId));
    } else {
      setCart((prevCart) =>
        prevCart.map((item) =>
          item.itemId === itemId ? { ...item, qty: newQty } : item
        )
      );
    }
  };

  // Calculate total
  const cartTotal = cart.reduce(
    (sum, item) => sum + (item.computedUnitPrice || item.itemPrice) * item.qty,
    0
  );

  // Helper function to extract menuItemId from cart item
  // Handles different possible field names: itemId, menuItemId, or nested menuItem.itemId
  const getMenuItemId = (cartItem) => {
    if (cartItem?.itemId !== undefined && cartItem.itemId !== null) {
      return cartItem.itemId;
    }
    if (cartItem?.menuItemId !== undefined && cartItem.menuItemId !== null) {
      return cartItem.menuItemId;
    }
    if (cartItem?.menuItem?.itemId !== undefined && cartItem.menuItem.itemId !== null) {
      return cartItem.menuItem.itemId;
    }
    return null;
  };

  // Helper function to extract chosenOption as a string
  // Converts from string or object { label: "...", price: ... }
  const getChosenOptionString = (option) => {
    if (typeof option === 'string') {
      return option || null;
    }
    if (option?.label) {
      return option.label;
    }
    if (option?.name) {
      return option.name;
    }
    return null;
  };

  // Helper function to extract notes as string[]
  // Converts from array of strings or array of objects { label: "...", price: ... }
  const getNotesArray = (notes) => {
    if (!Array.isArray(notes)) {
      return [];
    }
    return notes.map((note) => {
      if (typeof note === 'string') {
        return note;
      }
      if (note?.label) {
        return note.label;
      }
      return '';
    }).filter((n) => n !== '');
  };

  // Handle place order
  const handlePlaceOrder = async () => {
    // Defensive check: cart is empty
    if (!cart || cart.length === 0) {
      alert('Your cart is empty. Please add items before placing an order.');
      return;
    }

    // Build items payload from cart
    const itemsPayload = [];
    for (const cartItem of cart) {
      const menuItemId = getMenuItemId(cartItem);

      // Defensive check: menuItemId is missing
      if (menuItemId === null || menuItemId === undefined) {
        console.error('Invalid menu item detected in cart:', cartItem);
        console.error('Full cart dump:', cart);
        alert('Invalid menu item detected, please refresh the page.');
        return;
      }

      // Ensure menuItemId is numeric
      const numericId = Number(menuItemId);
      if (isNaN(numericId)) {
        console.error('menuItemId is not numeric:', menuItemId, 'from cart item:', cartItem);
        alert('Invalid menu item detected, please refresh the page.');
        return;
      }

      itemsPayload.push({
        menuItemId: numericId,
        quantity: cartItem.qty || 1,
        customerName: cartItem.customerName || null,
        chosenOption: getChosenOptionString(cartItem.selectedOption),
        notes: getNotesArray(cartItem.selectedNotes),
      });
    }

    // Visual inspection: log items table before sending
    console.table(itemsPayload);

    // Build order payload matching backend DTO
    const orderPayload = {
      tableId: tableNo,
      items: itemsPayload,
      notes: note || null,
      totalPrice: Number(cartTotal.toFixed(2)),
    };

    // Log final payload for debugging
    console.log('Submitting order payload:', JSON.stringify(orderPayload, null, 2));

    try {
      const res = await fetch('/api/orders', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(orderPayload),
      });

      if (!res.ok) {
        const text = await res.text();
        throw new Error(`Submit failed: ${res.status} ${text}`);
      }

      const savedOrder = await res.json();
      console.log('Order saved:', savedOrder);

      // Clear cart and note after successful submit
      setCart([]);
      setNote('');
      alert('Order placed successfully!');
    } catch (err) {
      console.error('Submit order failed:', err);
      alert(err.message || 'Order submission failed');
    }
  };



  return (
    <div className="app-shell customer-menu-page">
      <header className="page-header">
        <h1>Order Now</h1>
      </header>

      <main className="container container--customer page-content">
        {/* Menu Section */}
        <section className="menu-section">
          <h2>Menu</h2>
          <MenuList
            items={menuItems}
            loading={loading}
            error={error}
            onAddToCart={handleAddToCart}
          />
        </section>

        {/* Cart Section */}
        <Cart
          items={cart}
          cartTotal={cartTotal}
          tableNo={tableNo}
          note={note}
          onTableNoChange={setTableNo}
          onNoteChange={setNote}
          onQuantityChange={handleQuantityChange}
          onPlaceOrder={handlePlaceOrder}
        />
      </main>
    </div>
  );
}
