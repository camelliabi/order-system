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
                if (notesText != null && !notesText.trim().isEmpty()) {
                    return notesText.trim();
                }
                if (notes != null && !notes.isEmpty()) {
                    java.util.List<String> parts = new java.util.ArrayList<>();
                    for (Object o : notes) {
                        if (o == null) continue;
                        if (o instanceof String) {
                            String s = ((String) o).trim();
                            if (!s.isEmpty()) parts.add(s);
                        } else if (o instanceof java.util.Map) {
                            java.util.Map<?,?> m = (java.util.Map<?,?>) o;
                            Object label = m.get("label");
                            if (label == null) label = m.get("name");
                            if (label == null) label = m.get("value");
                            String s = label != null ? String.valueOf(label).trim() : String.valueOf(o).trim();
                            if (!s.isEmpty()) parts.add(s);
                        } else {
                            String s = String.valueOf(o).trim();
                            if (!s.isEmpty()) parts.add(s);
                        }
                    }
                    if (!parts.isEmpty()) return String.join(", ", parts);
                }
            } catch (Exception e) {
                // ignore and fall through to return null
            }
            return null;
        }
    }
}

