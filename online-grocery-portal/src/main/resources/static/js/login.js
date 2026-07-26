document.addEventListener('DOMContentLoaded', () => {
    updateCartBadge();

    // Redirect if already logged in
    const existingUser = localStorage.getItem('rowdypantry_user');
    if (existingUser) {
        window.location.href = 'index.html';
    }

    const loginForm = document.getElementById('login-form');
    if (loginForm) {
        loginForm.addEventListener('submit', handleLogin);
    }
});

function handleLogin(event) {
    event.preventDefault();

    const email = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value.trim();
    const alertBox = document.getElementById('login-alert');

    alertBox.classList.add('d-none');

    fetch('/api/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ email, password })
    })
        .then(async response => {
            const data = await response.json();
            if (!response.ok) {
                throw new Error(data.error || 'Invalid email or password');
            }
            return data;
        })
        .then(customerData => {
            // Save customer details so cart/checkout can access customerId
            localStorage.setItem('rowdypantry_user', JSON.stringify(customerData));
            window.location.href = 'index.html';
        })
        .catch(error => {
            alertBox.textContent = error.message;
            alertBox.classList.remove('d-none');
        });
}

function updateCartBadge() {
    const cart = JSON.parse(localStorage.getItem('grocery_cart')) || [];
    const badge = document.getElementById('cart-badge');
    if (badge) {
        const totalItems = cart.reduce((sum, item) => sum + item.quantity, 0);
        badge.textContent = totalItems;
    }
}