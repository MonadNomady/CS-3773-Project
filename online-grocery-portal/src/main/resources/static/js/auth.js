document.addEventListener('DOMContentLoaded', () => {
    updateNavbarAuth();
    updateGlobalCartBadge();
});

function getLoggedInUser() {
    const userJson = localStorage.getItem('rowdypantry_user');
    if (!userJson) return null;

    try {
        const user = JSON.parse(userJson);
        const idValue = user.customerID || user.customerId || user.id;
        user.id = idValue;
        user.customerId = idValue;
        return user;
    } catch (e) {
        return null;
    }
}

function updateNavbarAuth() {
    const user = getLoggedInUser();
    const authContainer = document.getElementById('navbar-auth-container');

    if (!authContainer) return;

    if (user) {
        authContainer.innerHTML = `
            <div class="d-flex align-items-center gap-2">
                <span class="text-white small fw-semibold me-2">Hi, ${user.name || 'Runner'}</span>
                <a href="profile.html" class="btn btn-outline-light btn-sm fw-semibold">Profile</a>
                <a href="orders.html" class="btn btn-outline-light btn-sm fw-semibold">Orders</a>
                <button onclick="handleLogout()" class="btn btn-danger btn-sm fw-semibold">Logout</button>
            </div>
        `;
    } else {
        authContainer.innerHTML = `
            <div class="d-flex align-items-center gap-2">
                <a href="login.html" class="btn btn-outline-light btn-sm fw-semibold">Login</a>
                <a href="register.html" class="btn btn-warning btn-sm fw-bold">Register</a>
            </div>
        `;
    }
}

function handleLogout() {
    localStorage.removeItem('rowdypantry_user');
    window.location.href = 'login.html';
}

function updateGlobalCartBadge() {
    const cart = JSON.parse(localStorage.getItem('grocery_cart')) || [];
    const badge = document.getElementById('cart-badge');
    if (badge) {
        const totalItems = cart.reduce((sum, item) => sum + parseInt(item.quantity || 1, 10), 0);
        badge.textContent = totalItems;
    }
}