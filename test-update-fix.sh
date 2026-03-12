#!/bin/bash
# Test script to verify the menu item update fix

echo "================================"
echo "Menu Item Update Fix Test"
echo "================================"
echo ""

# Set the base URL (adjust if needed)
BASE_URL="http://localhost:8080"

# First, let's get the menu to find an item to update
echo "1. Fetching current menu items..."
GET_RESPONSE=$(curl -s "$BASE_URL/api/menu")
echo "Response: $GET_RESPONSE"
echo ""

# Extract the first item ID (assuming API returns items)
ITEM_ID=$(echo "$GET_RESPONSE" | grep -o '"itemId":[0-9]*' | head -1 | grep -o '[0-9]*')

if [ -z "$ITEM_ID" ]; then
    echo "No items found in menu. Creating a test item first..."
    
    echo "2. Creating a new menu item..."
    CREATE_RESPONSE=$(curl -s -X POST "$BASE_URL/api/menu" \
      -H "Content-Type: application/json" \
      -d '{
        "itemName": "Test Item",
        "itemPrice": 10.99,
        "soldout": false,
        "options": [
          {"optionName": "Size Small", "optionPrice": 0.00},
          {"optionName": "Size Large", "optionPrice": 2.00}
        ],
        "notes": [
          {"noteName": "No onions", "notePrice": 0.00}
        ]
      }')
    echo "Create Response: $CREATE_RESPONSE"
    ITEM_ID=$(echo "$CREATE_RESPONSE" | grep -o '"itemId":[0-9]*' | head -1 | grep -o '[0-9]*')
    echo "New Item ID: $ITEM_ID"
    echo ""
fi

# Now update the item
if [ ! -z "$ITEM_ID" ]; then
    echo "3. Updating menu item (ID: $ITEM_ID)..."
    UPDATE_RESPONSE=$(curl -s -X PUT "$BASE_URL/api/menu/$ITEM_ID" \
      -H "Content-Type: application/json" \
      -d '{
        "itemName": "Updated Test Item",
        "itemPrice": 12.99,
        "soldout": false,
        "options": [
          {"optionName": "Size Medium", "optionPrice": 1.00},
          {"optionName": "Size X-Large", "optionPrice": 3.00}
        ],
        "notes": [
          {"noteName": "Extra sauce", "notePrice": 1.00}
        ]
      }')
    
    HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" -X PUT "$BASE_URL/api/menu/$ITEM_ID" \
      -H "Content-Type: application/json" \
      -d '{
        "itemName": "Updated Test Item",
        "itemPrice": 12.99,
        "soldout": false,
        "options": [
          {"optionName": "Size Medium", "optionPrice": 1.00},
          {"optionName": "Size X-Large", "optionPrice": 3.00}
        ],
        "notes": [
          {"noteName": "Extra sauce", "notePrice": 1.00}
        ]
      }')
    
    echo "HTTP Status Code: $HTTP_CODE"
    echo "Response: $UPDATE_RESPONSE"
    echo ""
    
    if [ "$HTTP_CODE" = "200" ]; then
        echo "✓ UPDATE SUCCESSFUL (HTTP 200)"
        echo ""
        echo "4. Verifying update by fetching the menu again..."
        VERIFY_RESPONSE=$(curl -s "$BASE_URL/api/menu")
        echo "Menu after update: $VERIFY_RESPONSE"
        echo ""
        echo "✓ FIX VERIFIED - Menu item update is working!"
    else
        echo "✗ UPDATE FAILED (HTTP $HTTP_CODE)"
        echo "The fix may need additional work"
    fi
else
    echo "Could not find or create an item to test"
fi

echo ""
echo "================================"
echo "Test Complete"
echo "================================"
