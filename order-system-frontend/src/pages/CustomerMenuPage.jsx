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

  // Add item to cart
  const handleAddToCart = (cartItem) => {
    if (cartItem.itemId && !cartItem.itemId.soldout) {
      setCart((prevCart) => {
        // Find existing item with same itemId, option, and notes
        const existingItemIndex = prevCart.findIndex(
          (ci) =>
            ci.itemId === cartItem.itemId &&
            JSON.stringify(ci.selectedOption) === JSON.stringify(cartItem.selectedOption) &&
            JSON.stringify(ci.selectedNotes) === JSON.stringify(cartItem.selectedNotes) &&
            ci.customerName === cartItem.customerName
        );

        if (existingItemIndex > -1) {
          // Increment quantity for identical selections
          const updatedCart = [...prevCart];
          updatedCart[existingItemIndex] = {
            ...updatedCart[existingItemIndex],
            qty: updatedCart[existingItemIndex].qty + cartItem.qty,
          };
          return updatedCart;
        }

        // Add as new cart item
        return [...prevCart, cartItem];
      });
    }
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

  // Handle place order
  const handlePlaceOrder = () => {
    if (cart.length === 0) {
      alert('Cart is empty. Please add items before placing order.');
      return;
    }

    const total = parseFloat(cartTotal.toFixed(2));
    const confirmed = confirm(
      `Order Total: $${total.toFixed(2)}\n\nConfirm order?`
    );

    if (confirmed) {
      const orderPayload = {
        tableNo,
        note: note || undefined,
        items: cart.map((item) => ({
          itemId: item.itemId,
          qty: item.qty,
          selectedOptionId: item.selectedOption?.id || null,
          selectedNoteIds: item.selectedNotes?.map((n) => n.id) || [],
          customerName: item.customerName || null,
        })),
        total,
      };

      console.log('Order placed:', orderPayload);
      alert('Order placed successfully! Check console for details.');
      // Reset form
      setCart([]);
      setTableNo('A1');
      setNote('');
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
