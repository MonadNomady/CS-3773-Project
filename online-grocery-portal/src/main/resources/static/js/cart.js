document.addEventListener('DOMContentLoaded', () => {
    renderCart();

    const deliverySelect = document.getElementById('delivery-type');
    if (deliverySelect) {
        deliverySelect.addEventListener('change', calculateTotals);
    }

    const promoInput = document.getElementById('promo-code');
    if (promoInput) {
        promoInput.addEventListener('input', calculateTotals);
    }

    const checkoutBtn = document.getElementById('checkout-btn');
    if (checkoutBtn) {
        checkoutBtn.addEventListener('click', handleCheckout);
    }
});

function getCart() {
    return JSON.parse(localStorage.getItem('grocery_cart')) || [];
}

function saveCart(cart) {
    localStorage.setItem('grocery_cart', JSON.stringify(cart));
    renderCart();
}

function renderCart() {
    const cart = getCart();
    const container = document.getElementById('cart-items-container');
    const badge = document.getElementById('cart-badge');

    const totalItems = cart.reduce((sum, item) => sum + (item.quantity || 1), 0);
    if (badge) badge.textContent = totalItems;

    if (!container) return;

    if (cart.length === 0) {
        container.innerHTML = `
            <div class="text-center py-5">
                <h5 class="text-muted">Your cart is empty.</h5>
                <a href="index.html" class="btn btn-outline-success mt-3 fw-semibold">Browse Products</a>
            </div>
        `;
        const checkoutBtn = document.getElementById('checkout-btn');
        if (checkoutBtn) checkoutBtn.disabled = true;

        // Reset totals display when cart is empty
        updateTotalsDisplay(0, 0, 0, 0);
        return;
    }

    const checkoutBtn = document.getElementById('checkout-btn');
    if (checkoutBtn) checkoutBtn.disabled = false;

    let html = '';
    cart.forEach(item => {
        const itemId = item.id || item.productId;
        const itemPrice = parseFloat(item.price || 0);
        const itemQty = parseInt(item.quantity || 1, 10);
        const itemTotal = (itemPrice * itemQty).toFixed(2);
        const itemImg = item.pictureUrl || item.picture_url || item.imageUrl || item.image || 'https://via.placeholder.com/80';

        html += `
            <div class="d-flex align-items-center justify-content-between py-3 border-bottom">
                <div class="d-flex align-items-center gap-3">
                    <img src="${itemImg}" 
                         alt="${item.name}" 
                         style="width: 60px; height: 60px; object-fit: contain;"
                         onError="this.src='https://via.placeholder.com/80'">
                    <div>
                        <h6 class="fw-bold mb-1">${item.name}</h6>
                        <small class="text-muted">$${itemPrice.toFixed(2)} each</small>
                    </div>
                </div>
                
                <div class="d-flex align-items-center gap-3">
                    <div class="input-group input-group-sm" style="width: 110px;">
                        <button class="btn btn-outline-secondary" onclick="updateQuantity(${itemId}, ${itemQty - 1})">-</button>
                        <input type="text" class="form-control text-center" value="${itemQty}" readonly>
                        <button class="btn btn-outline-secondary" onclick="updateQuantity(${itemId}, ${itemQty + 1})">+</button>
                    </div>
                    <span class="fw-bold text-end" style="min-width: 60px;">$${itemTotal}</span>
                    <button class="btn btn-sm btn-outline-danger" onclick="removeItem(${itemId})">🗑</button>
                </div>
            </div>
        `;
    });

    container.innerHTML = html;
    calculateTotals();
}

function updateQuantity(productId, newQty) {
    let cart = getCart();
    if (newQty <= 0) {
        removeItem(productId);
        return;
    }
    const item = cart.find(i => (i.id || i.productId) === productId);
    if (item) {
        item.quantity = newQty;
        saveCart(cart);
    }
}

function removeItem(productId) {
    let cart = getCart();
    cart = cart.filter(i => (i.id || i.productId) !== productId);
    saveCart(cart);

    const user = typeof getLoggedInUser === 'function' ? getLoggedInUser() : null;
    const customerId = user ? (user.customerId || user.customerID || user.id) : null;
    if (customerId) {
        fetch(`/api/cart/${customerId}/remove/${productId}`, { method: 'DELETE' })
            .catch(err => console.error('Failed to sync item removal:', err));
    }
}

async function calculateTotals() {
    const cart = getCart();

    // Calculate raw subtotal from cart items
    const rawSubtotal = cart.reduce((sum, item) => {
        const price = parseFloat(item.price || 0);
        const qty = parseInt(item.quantity || 1, 10);
        return sum + (price * qty);
    }, 0);

    if (rawSubtotal <= 0) {
        updateTotalsDisplay(0, 0, 0, 0);
        return;
    }

    const promoInput = document.getElementById('promo-code');
    const promoCode = promoInput ? promoInput.value.trim() : '';

    const deliverySelect = document.getElementById('delivery-type');
    const deliveryType = deliverySelect ? deliverySelect.value : 'STANDARD_DELIVERY';

    try {
        const response = await fetch('/api/cart/calculate', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                subtotal: rawSubtotal,
                promoCode: promoCode,
                deliveryType: deliveryType
            })
        });

        if (!response.ok) {
            throw new Error('Failed to fetch calculation from backend.');
        }

        const data = await response.json();

        // Update DOM elements using calculated fields from backend API
        updateTotalsDisplay(
            data.discountedSubtotal ?? data.subtotal,
            data.tax ?? data.taxAmount ?? 0,
            data.deliveryFee ?? 0,
            data.total ?? 0
        );

    } catch (error) {
        console.error('API calculation error:', error);
    }
}

function updateTotalsDisplay(subtotal, tax, deliveryFee, grandTotal) {
    const subtotalEl = document.getElementById('summary-subtotal');
    const taxEl = document.getElementById('summary-tax');
    const deliveryEl = document.getElementById('summary-delivery');
    const totalEl = document.getElementById('summary-total');

    if (subtotalEl) subtotalEl.textContent = `$${parseFloat(subtotal).toFixed(2)}`;
    if (taxEl) taxEl.textContent = `$${parseFloat(tax).toFixed(2)}`;
    if (deliveryEl) deliveryEl.textContent = `$${parseFloat(deliveryFee).toFixed(2)}`;
    if (totalEl) totalEl.textContent = `$${parseFloat(grandTotal).toFixed(2)}`;
}

function handleCheckout() {
    const user = typeof getLoggedInUser === 'function' ? getLoggedInUser() : JSON.parse(localStorage.getItem('rowdypantry_user'));
    const alertBox = document.getElementById('checkout-alert');

    const customerId = user ? (user.customerID || user.customerId || user.id) : null;
    if (!user || !customerId) {
        alertBox.className = 'alert alert-warning';
        alertBox.innerHTML = 'You must be logged in to place an order. <a href="login.html" class="alert-link">Click here to log in</a>.';
        alertBox.classList.remove('d-none');
        return;
    }

    const deliveryType = document.getElementById('delivery-type')?.value || 'STANDARD_DELIVERY';
    const promoCode = document.getElementById('promo-code')?.value.trim() || '';

    fetch(`/api/checkout/${customerId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ deliveryType, promoCode })
    })
        .then(async response => {
            const data = await response.json().catch(() => ({}));
            if (!response.ok) {
                throw new Error(data.error || data.message || 'Failed to place order.');
            }
            return data;
        })
        .then(order => {
            localStorage.removeItem('grocery_cart');
            alertBox.className = 'alert alert-success';
            alertBox.textContent = `🎉 Order placed successfully! Order ID: #${order.id || order.orderID}`;
            alertBox.classList.remove('d-none');
            renderCart();
        })
        .catch(error => {
            alertBox.className = 'alert alert-danger';
            alertBox.textContent = error.message;
            alertBox.classList.remove('d-none');
        });
}