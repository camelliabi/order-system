-- ============================================
-- QUICK FIX: Add unit_price column to fix pricing display
-- ============================================
-- Run this script to fix the pricing issue immediately
-- Usage: psql -U postgres -d order_system -f QUICK_FIX.sql

\echo '🔧 Adding unit_price column to order_item table...'

-- Add the column if it doesn't exist
ALTER TABLE order_item
ADD COLUMN IF NOT EXISTS unit_price NUMERIC(10, 2);

\echo '✅ Column added successfully!'

-- Add documentation
COMMENT ON COLUMN order_item.unit_price IS 'Calculated unit price including chosen option and notes at time of order';

\echo '📝 Added column documentation'

-- Optional: Update existing orders with base price (rough approximation)
-- Uncomment the following lines if you want to update historical data
/*
\echo '⚠️  Updating existing orders with base menu prices (approximation)...'

UPDATE order_item oi
SET unit_price = mi.item_price
FROM menu_item mi
WHERE oi.menu_item_id = mi.item_id
AND oi.unit_price IS NULL;

\echo '✅ Existing orders updated!'
*/

-- Verify the column exists
\echo '🔍 Verifying column...'
SELECT 
    column_name, 
    data_type, 
    column_default, 
    is_nullable
FROM information_schema.columns 
WHERE table_name = 'order_item' 
AND column_name = 'unit_price';

\echo ''
\echo '================================================'
\echo '✅ Migration complete!'
\echo '================================================'
\echo ''
\echo 'Next steps:'
\echo '1. Restart your Spring Boot application'
\echo '2. Create a new test order with options and notes'
\echo '3. Check the orders page - prices should now be correct!'
\echo ''
\echo 'Note: New orders will have correct pricing automatically.'
\echo 'Existing orders may show $0.00 or base price only.'
\echo '================================================'
