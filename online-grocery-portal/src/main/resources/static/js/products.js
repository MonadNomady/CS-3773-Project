// State management
let allProducts = [];
let cart = JSON.parse(localStorage.getItem('grocery_cart')) || [];

document.addEventListener('DOMContentLoaded', () => {
    updateCartBadge();
    fetchProducts();
    setupEventListeners();
});

// Fetch Products from Backend API
function fetchProducts() {
    fetch('/api/products')
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(products => {
            allProducts = products;
            applyFiltersAndRender();
        })
        .catch(error => {
            console.error('Error loading products:', error);
            const container = document.getElementById('product-grid');
            if (container) {
                container.innerHTML = `
                    <div class="col-12 text-center py-5">
                        <p class="text-danger fw-bold">Failed to load products. Please try again later.</p>
                    </div>`;
            }
        });
}

// Render Products to DOM
function renderProducts(products) {
    const container = document.getElementById('product-grid');
    if (!container) return;

    if (products.length === 0) {
        container.innerHTML = `
        <div class="col-12 no-items-found my-5">
            <p class="fs-4 text-muted">No items found matching your criteria.</p>
        </div>
        `;
        return;
    }

    container.innerHTML = products.map(product => {
        const id = product.productID || product.id || product.productId;
        const name = product.name || 'Unnamed Item';
        const description = product.description || '';
        const price = typeof product.price === 'number' ? product.price.toFixed(2) : '0.00';
        const pictureUrl = product.pictureUrl || product.picture_url || 'https://via.placeholder.com/200?text=No+Image';

        // Flexible property checking for availability
        const isAvailable = product.available === true || product.is_available === true || product.isAvailable === true;

        // Apply dynamic opacity and grayscale if out of stock
        const cardStyle = isAvailable ? '' : 'style="opacity: 0.65;"';
        const imgStyle = isAvailable ? 'style="height: 180px; object-fit: contain;"' : 'style="height: 180px; object-fit: contain; filter: grayscale(80%);"';

        return `
            <div class="col">
                <div class="card h-100 shadow-sm border-0 position-relative" ${cardStyle}>
                    <img src="${pictureUrl}" class="card-img-top p-3" alt="${name}" ${imgStyle} onError="this.src='https://via.placeholder.com/200?text=Image+Unavailable'">
                    <div class="card-body d-flex flex-column">
                        <div class="d-flex justify-content-between align-items-start mb-1">
                            <h5 class="card-title fs-6 fw-bold mb-0">${name}</h5>
                            ${!isAvailable ? '<span class="badge bg-secondary ms-2">Out of Stock</span>' : ''}
                        </div>
                        <p class="card-text text-muted small flex-grow-1">${description}</p>
                        <div class="d-flex justify-content-between align-items-center mt-3">
                            <span class="fs-5 fw-bold ${isAvailable ? 'text-primary' : 'text-muted'}">$${price}</span>
                            ${isAvailable
            ? `<button class="btn btn-sm btn-primary fw-semibold" onclick="addToCart(${id})">Add to Cart</button>`
            : `<button class="btn btn-sm btn-secondary fw-semibold" disabled>Unavailable</button>`
        }
                        </div>
                    </div>
                </div>
            </div>
        `;
    }).join('');
}

// Search, Filter, and Sort Handler
function applyFiltersAndRender() {
    const searchInput = document.getElementById('search-input');
    const sortSelect = document.getElementById('sort-select');
    const inStockToggle = document.getElementById('available-only');

    let filtered = [...allProducts];

    // Search Filter
    if (searchInput && searchInput.value.trim() !== '') {
        const query = searchInput.value.toLowerCase().trim();
        filtered = filtered.filter(p =>
            (p.name && p.name.toLowerCase().includes(query)) ||
            (p.description && p.description.toLowerCase().includes(query))
        );
    }

    // In Stock Only Toggle Filter
    if (inStockToggle && inStockToggle.checked) {
        filtered = filtered.filter(p => p.available === true || p.is_available === true || p.isAvailable === true);
    }

    // Sorting
    if (sortSelect) {
        const sortVal = sortSelect.value;
        if (sortVal === 'priceAsc') {
            filtered.sort((a, b) => a.price - b.price);
        } else if (sortVal === 'priceDesc') {
            filtered.sort((a, b) => b.price - a.price);
        } else if (sortVal === 'availability') {
            filtered.sort((a, b) => {
                const availA = (a.available === true || a.is_available === true || a.isAvailable === true) ? 1 : 0;
                const availB = (b.available === true || b.is_available === true || b.isAvailable === true) ? 1 : 0;
                return availB - availA;
            });
        }
    }

    renderProducts(filtered);
}

// Search & Filters Listeners
function setupEventListeners() {
    const searchInput = document.getElementById('search-input');
    const sortSelect = document.getElementById('sort-select');
    const inStockToggle = document.getElementById('available-only');

    if (searchInput) searchInput.addEventListener('input', applyFiltersAndRender);
    if (sortSelect) sortSelect.addEventListener('change', applyFiltersAndRender);
    if (inStockToggle) inStockToggle.addEventListener('change', applyFiltersAndRender);
}

// Cart Functions
function addToCart(productId) {
    const user = typeof getLoggedInUser === 'function' ? getLoggedInUser() : null;
    const customerId = user ? (user.customerId || user.customerID || user.id) : null;

    const product = allProducts.find(p => (p.productID || p.id || p.productId) === productId);
    if (!product) return;

    // Save to localStorage
    const existingItem = cart.find(item => item.productId === productId);
    if (existingItem) {
        existingItem.quantity += 1;
    } else {
        cart.push({
            productId: productId,
            name: product.name,
            price: product.price,
            pictureUrl: product.pictureUrl || product.picture_url,
            quantity: 1
        });
    }
    localStorage.setItem('grocery_cart', JSON.stringify(cart));
    updateCartBadge();

    // Sync with backend API if user is logged in
    if (customerId) {
        fetch(`/api/cart/${customerId}/add?productId=${productId}&quantity=1`, {
            method: 'POST'
        }).catch(err => console.error('Failed to sync backend cart:', err));
    }
}

function updateCartBadge() {
    cart = JSON.parse(localStorage.getItem('grocery_cart')) || [];
    const badge = document.getElementById('cart-badge');
    if (badge) {
        const totalItems = cart.reduce((sum, item) => sum + item.quantity, 0);
        badge.textContent = totalItems;
    }
}