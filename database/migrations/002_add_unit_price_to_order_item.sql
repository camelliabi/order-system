-- Migration: Add unit_price column to order_item table
-- This stores the calculated price (base + option + notes) at the time of order
-- Fixes issue where orders page only showed base price instead of actual price

ALTER TABLE order_item
ADD COLUMN IF NOT EXISTS unit_price NUMERIC(10, 2);

-- For existing rows, set unit_price to NULL or base menu price
-- (Admin should manually review existing orders if needed)
COMMENT ON COLUMN order_item.unit_price IS 'Calculated unit price including chosen option and notes at time of order';
