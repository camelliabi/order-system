# Database Migrations

This folder contains SQL migration scripts for the order system database.

## How to Apply Migrations

### Using psql
```bash
psql -U postgres -d order_system -f database/migrations/002_add_unit_price_to_order_item.sql
```

### Using pgAdmin or other GUI tools
1. Connect to the `order_system` database
2. Open the SQL query tool
3. Copy and paste the contents of the migration file
4. Execute the query

## Migration Files

### 002_add_unit_price_to_order_item.sql
**Purpose**: Adds the `unit_price` column to the `order_item` table to store the calculated price (base price + chosen option price + sum of note prices) at the time the order is placed.

**Why this is needed**: Previously, the system only stored base menu prices, so when customers chose premium options (e.g., Beef instead of Chicken) or added notes with prices (e.g., extra rice $1.00), the orders page would incorrectly show only the base price.

**Breaking changes**: None. This is additive only.

**Deployment steps**:
1. Apply this migration to add the column
2. Deploy the application code changes
3. New orders will automatically have the correct `unit_price` calculated and stored
4. Existing orders (if any) will have `NULL` for `unit_price` and will need manual review/correction if needed

## Notes

- Always backup your database before applying migrations
- Test migrations in a development environment first
- The application expects this column to exist (it's defined in `OrderItemEntity.java`)
