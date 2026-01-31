// src/api/orderApi.js

export async function submitOrder(payload) {
  // Debug: log the exact payload sent to the backend (copy from browser Network tab)
  try {
    console.log('Submitting order payload:', payload);
  } catch (e) {
    // noop
  }
  const response = await fetch('/api/orders', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
  });
  if (!response.ok) {
    const error = await response.json().catch(() => ({}));
    throw new Error(error.message || 'Failed to submit order');
  }
  return response.json();
}
