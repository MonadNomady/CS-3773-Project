document.addEventListener('DOMContentLoaded', () => {
    const registerForm = document.getElementById('register-form');
    if (registerForm) {
        registerForm.addEventListener('submit', handleRegister);
    }
});

function handleRegister(event) {
    event.preventDefault();

    const alertBox = document.getElementById('register-alert');
    const name = document.getElementById('reg-name').value.trim();
    const email = document.getElementById('reg-email').value.trim();
    const password = document.getElementById('reg-password').value.trim();

    if (alertBox) alertBox.classList.add('d-none');

    const payload = { name, email, password };

    fetch('/api/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    })
        .then(async response => {
            const data = await response.json().catch(() => ({}));
            if (!response.ok) {
                throw new Error(data.error || data.message || 'Registration failed.');
            }
            return data;
        })
        .then(registeredCustomer => {
            registeredCustomer.customerId = registeredCustomer.customerID || registeredCustomer.customerId || registeredCustomer.id;

            localStorage.setItem('rowdypantry_user', JSON.stringify(registeredCustomer));

            if (alertBox) {
                alertBox.className = 'alert alert-success';
                alertBox.textContent = 'Account created successfully! Redirecting...';
                alertBox.classList.remove('d-none');
            }

            setTimeout(() => {
                window.location.href = 'index.html';
            }, 1200);
        })
        .catch(err => {
            if (alertBox) {
                alertBox.className = 'alert alert-danger';
                alertBox.textContent = err.message;
                alertBox.classList.remove('d-none');
            }
        });
}