document.addEventListener('DOMContentLoaded', () => {
    const user = typeof getLoggedInUser === 'function' ? getLoggedInUser() : JSON.parse(localStorage.getItem('rowdypantry_user'));
    const customerId = user ? (user.customerId || user.customerID || user.id) : null;

    if (!user || !customerId) {
        console.warn('Access Denied: User session invalid or missing customerId', user);
        window.location.href = 'login.html';
        return;
    }

    loadOrders(customerId);
});

function loadOrders(customerId) {
    const container = document.getElementById('orders-container');

    fetch(`/api/orders/history/${customerId}`)
        .then(async response => {
            const data = await response.json().catch(() => ([]));
            if (!response.ok) {
                throw new Error(data.error || 'Failed to load past orders.');
            }
            return data;
        })
        .then(orders => {
            if (!orders || orders.length === 0) {
                container.innerHTML = '<p class="text-muted mb-0">No past orders found.</p>';
                return;
            }

            container.innerHTML = orders.map(order => {
                const orderId = order.orderID || order.orderId || order.id || 'N/A';
                const orderDate = order.orderDate ? new Date(order.orderDate).toLocaleDateString() : 'N/A';
                const total = typeof order.totalAmount === 'number' ? order.totalAmount.toFixed(2) : '0.00';
                const status = order.status || 'Placed';

                // Render order items if present in the response object
                const itemsHtml = Array.isArray(order.items) && order.items.length > 0
                    ? `<ul class="list-group list-group-flush mt-2">
                        ${order.items.map(item => `
                            <li class="list-group-item d-flex justify-content-between align-items-center px-0 py-1 bg-transparent">
                                <span>${item.productName || item.product?.name || 'Item'} (x${item.quantity || 1})</span>
                                <span class="fw-semibold">$${item.price ? item.price.toFixed(2) : '0.00'}</span>
                            </li>
                        `).join('')}
                       </ul>`
                    : '';

                return `
                    <div class="border rounded p-3 bg-white mb-3 shadow-sm">
                        <div class="d-flex justify-content-between align-items-center mb-2">
                            <div>
                                <h5 class="fw-bold mb-0 text-primary">Order #${orderId}</h5>
                                <small class="text-muted">Date: ${orderDate}</small>
                            </div>
                            <span class="badge bg-success fs-6">${status}</span>
                        </div>
                        ${itemsHtml}
                        <hr class="my-2">
                        <div class="d-flex justify-content-between align-items-center">
                            <span class="fw-semibold">Total Amount:</span>
                            <span class="fw-bold text-dark fs-5">$${total}</span>
                        </div>
                    </div>
                `;
            }).join('');
        })
        .catch(err => {
            container.innerHTML = `<p class="text-danger mb-0">${err.message}</p>`;
        });
}