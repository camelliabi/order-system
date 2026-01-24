import MenuItemCard from './MenuItemCard';

export default function MenuList({ items, loading, error, onAddToCart }) {
  if (loading) {
    return <p className="loading">Loading menu...</p>;
  }

  if (error) {
    return <p className="error">{error}</p>;
  }

  if (items.length === 0) {
    return <p className="no-items">No menu items available.</p>;
  }

  return (
    <div className="menu-list">
      {items.map((item) => (
        <MenuItemCard
          key={item.itemId}
          item={item}
          onAddToCart={onAddToCart}
        />
      ))}
    </div>
  );
}
