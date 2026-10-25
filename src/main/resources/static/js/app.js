/**
 * Flavor Spot - Central Client Controller & State Management
 */

const API_BASE = '';

// -------------------------------------------------------------
// Authentication State
// -------------------------------------------------------------
const Auth = {
    getUser() {
        try {
            return JSON.parse(localStorage.getItem('fs_user'));
        } catch (e) {
            return null;
        }
    },
    setUser(user) {
        localStorage.setItem('fs_user', JSON.stringify(user));
        this.updateNav();
    },
    logout() {
        localStorage.removeItem('fs_user');
        window.location.href = 'login.html';
    },
    isLoggedIn() {
        return !!this.getUser();
    },
    isAdmin() {
        const u = this.getUser();
        return u && u.role === 'ROLE_ADMIN';
    },
    requireAuth(redirectPage = 'login.html') {
        if (!this.isLoggedIn()) {
            window.location.href = redirectPage;
        }
    },
    requireAdmin() {
        if (!this.isAdmin()) {
            window.location.href = 'login.html';
        }
    },
    updateNav() {
        const user = this.getUser();
        const authContainer = document.getElementById('nav-auth-container');
        if (!authContainer) return;

        if (user) {
            const isAdmin = user.role === 'ROLE_ADMIN';
            authContainer.innerHTML = `
                <div class="dropdown">
                    <button class="btn btn-outline-gold dropdown-toggle d-flex align-items-center gap-2" type="button" data-bs-toggle="dropdown">
                        <i class="bi bi-person-circle"></i>
                        <span>${escapeHtml(user.fullName.split(' ')[0])}</span>
                        ${isAdmin ? '<span class="badge bg-warning text-dark ms-1">Admin</span>' : ''}
                    </button>
                    <ul class="dropdown-menu dropdown-menu-dark dropdown-menu-end shadow">
                        ${isAdmin ? '<li><a class="dropdown-item" href="admin.html"><i class="bi bi-speedometer2 me-2"></i>Admin Dashboard</a></li>' : ''}
                        <li><a class="dropdown-item" href="dashboard.html"><i class="bi bi-journal-text me-2"></i>My Reservations & Orders</a></li>
                        <li><hr class="dropdown-divider"></li>
                        <li><a class="dropdown-item text-danger" href="javascript:void(0)" onclick="Auth.logout()"><i class="bi bi-box-arrow-right me-2"></i>Logout</a></li>
                    </ul>
                </div>
            `;
        } else {
            authContainer.innerHTML = `
                <a href="login.html" class="btn btn-outline-gold me-2">Log In</a>
                <a href="register.html" class="btn btn-gold">Register</a>
            `;
        }
    }
};

// -------------------------------------------------------------
// Cart State Management
// -------------------------------------------------------------
const Cart = {
    getItems() {
        try {
            return JSON.parse(localStorage.getItem('fs_cart')) || [];
        } catch (e) {
            return [];
        }
    },
    save(items) {
        localStorage.setItem('fs_cart', JSON.stringify(items));
        this.updateBadge();
    },
    addItem(item) {
        const items = this.getItems();
        const existing = items.find(i => i.id === item.id);
        if (existing) {
            existing.quantity += 1;
        } else {
            items.push({
                id: item.id,
                name: item.name,
                price: parseFloat(item.price),
                imageUrl: item.imageUrl,
                categoryName: item.categoryName,
                quantity: 1
            });
        }
        this.save(items);
        showToast(`Added "${item.name}" to cart!`, 'success');
    },
    updateQuantity(id, change) {
        let items = this.getItems();
        const item = items.find(i => i.id === id);
        if (item) {
            item.quantity += change;
            if (item.quantity <= 0) {
                items = items.filter(i => i.id !== id);
            }
        }
        this.save(items);
    },
    removeItem(id) {
        const items = this.getItems().filter(i => i.id !== id);
        this.save(items);
    },
    clear() {
        localStorage.removeItem('fs_cart');
        this.updateBadge();
    },
    getCount() {
        return this.getItems().reduce((sum, item) => sum + item.quantity, 0);
    },
    getTotal() {
        return this.getItems().reduce((sum, item) => sum + (item.price * item.quantity), 0);
    },
    updateBadge() {
        const badge = document.getElementById('cart-count-badge');
        if (badge) {
            const count = this.getCount();
            badge.textContent = count;
            badge.style.display = count > 0 ? 'flex' : 'none';
        }
    }
};

// -------------------------------------------------------------
// Toast Notification
// -------------------------------------------------------------
function showToast(message, type = 'info') {
    let container = document.getElementById('toast-container');
    if (!container) {
        container = document.createElement('div');
        container.id = 'toast-container';
        container.className = 'position-fixed bottom-0 end-0 p-3';
        container.style.zIndex = '9999';
        document.body.appendChild(container);
    }

    const toastId = 'toast-' + Date.now();
    const bgClass = type === 'success' ? 'bg-success' : type === 'error' ? 'bg-danger' : 'bg-primary';

    const toastHtml = `
        <div id="${toastId}" class="toast align-items-center text-white ${bgClass} border-0 show shadow-lg mb-2" role="alert">
            <div class="d-flex">
                <div class="toast-body fw-medium">${escapeHtml(message)}</div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
            </div>
        </div>
    `;

    container.insertAdjacentHTML('beforeend', toastHtml);
    setTimeout(() => {
        const el = document.getElementById(toastId);
        if (el) el.remove();
    }, 4000);
}

// -------------------------------------------------------------
// Shared Fetch Helper
// -------------------------------------------------------------
async function apiFetch(endpoint, options = {}) {
    try {
        const res = await fetch(API_BASE + endpoint, {
            headers: {
                'Content-Type': 'application/json',
                ...(options.headers || {})
            },
            ...options
        });

        const data = await res.json();
        if (!res.ok) {
            throw new Error(data.message || 'Request failed with status ' + res.status);
        }
        return data;
    } catch (err) {
        console.error('API Error:', err);
        throw err;
    }
}

function escapeHtml(text) {
    if (!text) return '';
    const map = { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#039;' };
    return String(text).replace(/[&<>"']/g, m => map[m]);
}

// Global bootstrap initialization
document.addEventListener('DOMContentLoaded', () => {
    Auth.updateNav();
    Cart.updateBadge();
});
