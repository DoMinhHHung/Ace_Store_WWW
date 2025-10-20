// admins.js - Client-side JavaScript for Admin Management

// Get CSRF token from meta tags
function getCsrfToken() {
    const token = document.querySelector('meta[name="_csrf"]');
    const header = document.querySelector('meta[name="_csrf_header"]');
    return token && header ? { token: token.content, header: header.content } : null;
}

// Escape HTML to prevent XSS
function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// Show/hide elements
function showElement(id) {
    document.getElementById(id).style.display = 'block';
}

function hideElement(id) {
    document.getElementById(id).style.display = 'none';
}

// Show messages
function showError(message) {
    const errorDiv = document.getElementById('error');
    errorDiv.textContent = escapeHtml(message);
    showElement('error');
    setTimeout(() => hideElement('error'), 5000);
}

function showSuccess(message) {
    const successDiv = document.getElementById('success');
    successDiv.textContent = escapeHtml(message);
    showElement('success');
    setTimeout(() => hideElement('success'), 3000);
}

// Load admins from API
async function loadAdmins() {
    showElement('loading');
    hideElement('error');
    hideElement('success');
    
    try {
        // Try to get all users first
        const response = await fetch('/api/users?page=0&size=100');
        
        if (!response.ok) {
            // If endpoint doesn't exist or requires auth, show a message
            if (response.status === 404) {
                throw new Error('API endpoint /api/users không tồn tại. Cần implement GET /api/users endpoint.');
            }
            if (response.status === 401 || response.status === 403) {
                throw new Error('Yêu cầu xác thực. API này cần JWT token với quyền ADMIN.');
            }
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const apiResponse = await response.json();
        hideElement('loading');
        
        if (!apiResponse.success) {
            showError(apiResponse.message || 'Lỗi khi tải dữ liệu');
            return;
        }
        
        // Handle different response formats
        let users = [];
        if (apiResponse.data) {
            if (Array.isArray(apiResponse.data)) {
                users = apiResponse.data;
            } else if (Array.isArray(apiResponse.data.content)) {
                users = apiResponse.data.content;
            } else if (Array.isArray(apiResponse.data.users)) {
                users = apiResponse.data.users;
            }
        }
        
        // Filter only admin users
        const admins = users.filter(user => {
            return user.roles && user.roles.some(role => 
                role === 'ADMIN' || role.name === 'ADMIN' || role === 'ROLE_ADMIN'
            );
        });
        
        renderAdmins(admins);
        
    } catch (error) {
        hideElement('loading');
        showError('Lỗi kết nối: ' + error.message);
        console.error('Error loading admins:', error);
    }
}

// Render admins table
function renderAdmins(admins) {
    const tbody = document.getElementById('adminsBody');
    tbody.innerHTML = '';
    
    if (!admins || admins.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" style="text-align: center;">Không có admin nào</td></tr>';
        return;
    }
    
    admins.forEach(admin => {
        const row = tbody.insertRow();
        
        // Format roles
        let rolesHtml = '';
        if (admin.roles && Array.isArray(admin.roles)) {
            rolesHtml = admin.roles.map(role => {
                const roleName = typeof role === 'string' ? role : (role.name || role);
                const isAdmin = roleName === 'ADMIN' || roleName === 'ROLE_ADMIN';
                const badgeClass = isAdmin ? 'badge-admin' : 'badge-user';
                return `<span class="badge ${badgeClass}">${escapeHtml(roleName)}</span>`;
            }).join(' ');
        }
        
        const statusBadge = admin.enabled 
            ? '<span class="badge badge-success">Kích hoạt</span>' 
            : '<span class="badge badge-danger">Chưa kích hoạt</span>';
        
        const fullName = `${admin.firstName || ''} ${admin.lastName || ''}`.trim() || '-';
        
        row.innerHTML = `
            <td>${escapeHtml(String(admin.id))}</td>
            <td>${escapeHtml(fullName)}</td>
            <td>${escapeHtml(admin.email)}</td>
            <td>${escapeHtml(admin.phone || '-')}</td>
            <td>${rolesHtml}</td>
            <td>${statusBadge}</td>
        `;
    });
}

// Form submit handler
document.addEventListener('DOMContentLoaded', function() {
    // Load admins on page load
    loadAdmins();
});
