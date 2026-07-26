document.addEventListener('DOMContentLoaded', () => {
    const user = typeof getLoggedInUser === 'function' ? getLoggedInUser() : JSON.parse(localStorage.getItem('rowdypantry_user'));

    const customerId = user ? (user.customerId || user.customerID || user.id) : null;

    if (!user || !customerId) {
        console.warn('Profile Access Denied: User session invalid or missing customerId', user);
        window.location.href = 'login.html';
        return;
    }

    loadAddresses(customerId);

    const addressForm = document.getElementById('address-form');
    if (addressForm) {
        addressForm.addEventListener('submit', (e) => handleAddAddress(e, customerId));
    }
});

function loadAddresses(customerId) {
    const container = document.getElementById('address-list-container');

    fetch(`/api/customers/${customerId}/addresses`)
        .then(res => {
            if (!res.ok) throw new Error('Failed to load addresses.');
            return res.json();
        })
        .then(addresses => {
            if (!addresses || addresses.length === 0) {
                container.innerHTML = '<p class="text-muted mb-0">No saved addresses found. Add one on the left!</p>';
                return;
            }

            container.innerHTML = addresses.map(addr => {
                const addressString = typeof addr === 'string' ? addr : (addr.street || JSON.stringify(addr));
                return `
                    <div class="border rounded p-3 bg-white d-flex justify-content-between align-items-center mb-2">
                        <div>
                            <p class="fw-bold mb-0">${addressString}</p>
                        </div>
                        <button class="btn btn-sm btn-outline-danger fw-semibold" onclick="handleRemoveAddress('${encodeURIComponent(addressString)}', ${customerId})">
                            Delete
                        </button>
                    </div>
                `;
            }).join('');
        })
        .catch(err => {
            container.innerHTML = `<p class="text-danger mb-0">${err.message}</p>`;
        });
}

function handleAddAddress(event, customerId) {
    event.preventDefault();
    const alertBox = document.getElementById('address-alert');

    const street = document.getElementById('street').value.trim();
    const city = document.getElementById('city').value.trim();
    const state = document.getElementById('state').value.trim();
    const zipCode = document.getElementById('zipCode').value.trim();

    const fullAddress = `${street}, ${city}, ${state} ${zipCode}`;

    fetch(`/api/customers/${customerId}/addresses`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ address: fullAddress })
    })
        .then(async response => {
            const data = await response.json().catch(() => ({}));
            if (!response.ok) {
                throw new Error(data.error || data.message || 'Failed to save address.');
            }
            return data;
        })
        .then(() => {
            if (alertBox) {
                alertBox.className = 'alert alert-success';
                alertBox.textContent = 'Address added successfully!';
                alertBox.classList.remove('d-none');
            }
            document.getElementById('address-form').reset();
            loadAddresses(customerId);
        })
        .catch(err => {
            if (alertBox) {
                alertBox.className = 'alert alert-danger';
                alertBox.textContent = err.message;
                alertBox.classList.remove('d-none');
            }
        });
}

function handleRemoveAddress(encodedAddress, customerId) {
    const address = decodeURIComponent(encodedAddress);
    const alertBox = document.getElementById('address-alert');

    fetch(`/api/customers/${customerId}/addresses?address=${encodeURIComponent(address)}`, {
        method: 'DELETE'
    })
        .then(async response => {
            const data = await response.json().catch(() => ({}));
            if (!response.ok) {
                throw new Error(data.error || data.message || 'Failed to remove address.');
            }
            return data;
        })
        .then(() => {
            if (alertBox) {
                alertBox.className = 'alert alert-info';
                alertBox.textContent = 'Address removed successfully.';
                alertBox.classList.remove('d-none');
            }
            loadAddresses(customerId);
        })
        .catch(err => {
            if (alertBox) {
                alertBox.className = 'alert alert-danger';
                alertBox.textContent = err.message;
                alertBox.classList.remove('d-none');
            }
        });
}