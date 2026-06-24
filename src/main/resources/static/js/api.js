const API_BASE = '/api';

const Auth = {
    getToken: () => localStorage.getItem('token'),
    setToken: (token) => localStorage.setItem('token', token),
    getRole: () => localStorage.getItem('role'),
    setRole: (role) => localStorage.setItem('role', role),
    getFullName: () => localStorage.getItem('fullName') || 'User',
    setFullName: (name) => localStorage.setItem('fullName', name),
    logout: () => {
        localStorage.removeItem('token');
        localStorage.removeItem('role');
        localStorage.removeItem('fullName');
        window.location.href = 'login.html';
    },
    login: async (username, password) => {
        try {
            const res = await fetch(`${API_BASE}/auth/login`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, password })
            });
            if (res.ok) {
                const data = await res.json();
                Auth.setToken(data.accessToken);
                Auth.setRole(data.role); // Save role for UI hiding
                Auth.setFullName(data.fullName); // Save full name
                return true;
            }
            return false;
        } catch (e) {
            console.error(e);
            return false;
        }
    },
    checkAuth: () => {
        if (!Auth.getToken() && !window.location.href.includes('login.html')) {
            window.location.href = 'login.html';
        }
    }
};

const ApiClient = {
    fetch: async (endpoint, options = {}) => {
        const token = Auth.getToken();
        const headers = {
            'Content-Type': 'application/json',
            ...(token ? { 'Authorization': `Bearer ${token}` } : {}),
            ...options.headers
        };
        try {
            const response = await fetch(`${API_BASE}${endpoint}`, { ...options, headers });
            if (response.status === 401 || response.status === 403) {
                console.warn('Session expired or unauthorized, redirecting to login...');
                Auth.logout();
                return null;
            }
            return response;
        } catch (networkErr) {
            console.error('Network error:', networkErr);
            throw new Error('Không thể kết nối đến server. Hãy kiểm tra Spring Boot có đang chạy không.');
        }
    }
};

// Check auth on load for secured pages
document.addEventListener('DOMContentLoaded', () => {
    Auth.checkAuth();
    
    // Auto-hide elements based on roles
    const currentRole = Auth.getRole();
    if (currentRole !== 'ADMIN') {
        // Hide elements that require ADMIN (e.g., Roles & Permissions menu)
        document.querySelectorAll('.require-admin').forEach(el => el.style.display = 'none');
    }

    // Inject Header Dropdowns
    const headerActions = document.querySelector('.header-actions');
    if (headerActions) {
        const fullName = Auth.getFullName();
        const initial = fullName ? fullName.charAt(0).toUpperCase() : 'U';
        const role = Auth.getRole() || 'USER';

        headerActions.innerHTML = `
            <div class="dropdown-container">
                <button class="icon-btn has-notification" id="notifToggle" onclick="toggleDropdown('notifDropdown')">
                    <i data-lucide="bell"></i>
                </button>
                <div class="dropdown-menu notification-dropdown" id="notifDropdown">
                    <div class="dropdown-header">Notifications</div>
                    <div class="dropdown-item notification-item">
                        <span class="notification-title">Maintenance Alert</span>
                        <span class="notification-time">Just now</span>
                    </div>
                    <div class="dropdown-item notification-item">
                        <span class="notification-title">System Update</span>
                        <span class="notification-time">2 hours ago</span>
                    </div>
                </div>
            </div>

            <div class="dropdown-container">
                <div class="avatar" id="userToggle" onclick="toggleDropdown('userDropdown')">${initial}</div>
                <div class="dropdown-menu" id="userDropdown">
                    <div class="dropdown-header" style="padding-bottom: 4px;">${fullName}</div>
                    <div style="padding: 0 16px 12px; font-size: 12px; color: var(--text-muted); border-bottom: 1px solid var(--border-color);">${role}</div>
                    
                    <a href="#" class="dropdown-item" style="margin-top: 4px;">
                        <i data-lucide="user" style="width: 16px;"></i> Profile
                    </a>
                    <a href="#" class="dropdown-item">
                        <i data-lucide="settings" style="width: 16px;"></i> Settings
                    </a>
                    <a href="#" class="dropdown-item text-danger" onclick="Auth.logout()">
                        <i data-lucide="log-out" style="width: 16px;"></i> Logout
                    </a>
                </div>
            </div>
        `;
        if (window.lucide) window.lucide.createIcons();
    }

    // Close dropdowns when clicking outside
    document.addEventListener('click', (e) => {
        if (!e.target.closest('.dropdown-container')) {
            document.querySelectorAll('.dropdown-menu').forEach(menu => menu.classList.remove('show'));
        }
    });
});

// Helper for toggle
window.toggleDropdown = function(id) {
    // Close others
    document.querySelectorAll('.dropdown-menu').forEach(menu => {
        if (menu.id !== id) menu.classList.remove('show');
    });
    // Toggle current
    document.getElementById(id).classList.toggle('show');
};
