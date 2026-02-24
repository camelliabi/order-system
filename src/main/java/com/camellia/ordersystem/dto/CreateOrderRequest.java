package com.camellia.ordersystem.dto;

import java.util.List;

public class CreateOrderRequest {
    public String tableId;
    public String note;  // optional order note
    public List<CreateOrderItem> items;

    public static class CreateOrderItem {
        public Integer menuItemId;    // unique menu item ID (must not be null)
        public Integer quantity;      // item quantity
        public String customerName;   // customer name (optional)
        public String chosenOption;   // chosen option as string (optional)
        public List<Object> notes;    // multi-select notes (strings or objects) (optional)
        public String notesText;      // optional single-string notes (accept either format)

        // Normalize notes into a single string for persistence.
        public String normalizedNotesText() {
            try {
                // FIX: Add null and empty checks for notesText
                if (notesText != null && !notesText.trim().isEmpty()) {
                    return notesText.trim();
                }
                
                // FIX: Add null and empty checks for notes list
                if (notes != null && !notes.isEmpty()) {
                    java.util.List<String> parts = new java.util.ArrayList<>();
                    
                    for (Object o : notes) {
                        // FIX: Skip null objects
                        if (o == null) {
                            continue;
                        }
                        
                        try {
                            if (o instanceof String) {
                                String s = ((String) o).trim();
                                if (!s.isEmpty()) {
                                    parts.add(s);
                                }
                            } else if (o instanceof java.util.Map) {
                                // FIX: Add safer map handling
                                @SuppressWarnings("unchecked")
                                java.util.Map<?, ?> m = (java.util.Map<?, ?>) o;
                                
                                // FIX: Try multiple possible keys safely
                                Object label = null;
                                
                                // Try "label" key
                                if (m.containsKey("label")) {
                                    label = m.get("label");
                                }
                                // Try "name" key if label is null
                                if (label == null && m.containsKey("name")) {
                                    label = m.get("name");
                                }
                                // Try "value" key if still null
                                if (label == null && m.containsKey("value")) {
                                    label = m.get("value");
                                }
                                
                                // FIX: Convert to string safely
                                if (label != null) {
                                    String s = String.valueOf(label).trim();
                                    if (!s.isEmpty() && !s.equals("null")) {
                                        parts.add(s);
                                    }
                                }
                            } else {
                                // FIX: Handle other object types safely
                                String s = String.valueOf(o).trim();
                                if (!s.isEmpty() && !s.equals("null")) {
                                    parts.add(s);
                                }
                            }
                        } catch (ClassCastException e) {
                            // FIX: Log and skip invalid objects rather than failing
                            // Could add logging here if logger is available
                            continue;
                        } catch (Exception e) {
                            // FIX: Catch any other exceptions during processing
                            continue;
                        }
                    }
                    
                    // FIX: Only return if we have actual content
                    if (!parts.isEmpty()) {
                        return String.join(", ", parts);
                    }
                }
            } catch (Exception e) {
                // FIX: Return null on any unexpected error rather than throwing
                // This prevents request failure due to note processing issues
                return null;
            }
            
            return null;
        }
    }
}
