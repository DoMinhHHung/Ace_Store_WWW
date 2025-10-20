// users.js - Client-side JavaScript for User Management

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

// Load users from API
async function loadUsers() {
    showElement('loading');
    hideElement('error');
    hideElement('success');
    
    try {
        // Try to get all users - this might require admin token
        // For now, we'll use a simple GET endpoint
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
        
        renderUsers(users);
        
    } catch (error) {
        hideElement('loading');
        showError('Lỗi kết nối: ' + error.message);
        console.error('Error loading users:', error);
    }
}

// Render users table
function renderUsers(users) {
    const tbody = document.getElementById('usersBody');
    tbody.innerHTML = '';
    
    if (!users || users.length === 0) {
        tbody.innerHTML = '<tr><td colspan="7" style="text-align: center;">Không có người dùng nào</td></tr>';
        return;
    }
    
    users.forEach(user => {
        const row = tbody.insertRow();
        const statusBadge = user.enabled 
            ? '<span class="badge badge-success">Kích hoạt</span>' 
            : '<span class="badge badge-danger">Chưa kích hoạt</span>';
        
        row.innerHTML = `
            <td>${escapeHtml(String(user.id))}</td>
            <td>${escapeHtml(user.firstName || '-')}</td>
            <td>${escapeHtml(user.lastName || '-')}</td>
            <td>${escapeHtml(user.email)}</td>
            <td>${escapeHtml(user.phone || '-')}</td>
            <td>${statusBadge}</td>
            <td>
                <button class="btn btn-danger" onclick="deleteUser(${user.id})">🗑️ Xóa</button>
            </td>
        `;
    });
}

// Open create modal
function openCreateModal() {
    document.getElementById('modalTitle').textContent = 'Thêm Người Dùng Mới';
    document.getElementById('userForm').reset();
    showElement('userModal');
}

// Close modal
function closeModal() {
    hideElement('userModal');
}

// Create user (using signup endpoint)
async function createUser(formData) {
    try {
        const csrf = getCsrfToken();
        const headers = {
            'Content-Type': 'application/json'
        };
        
        if (csrf) {
            headers[csrf.header] = csrf.token;
        }
        
        // Use signup endpoint for creating users
        const response = await fetch('/api/auth/signup', {
            method: 'POST',
            headers: headers,
            body: JSON.stringify(formData)
        });
        
        const result = await response.json();
        
        if (!response.ok || !result.success) {
            throw new Error(result.message || 'Lỗi khi tạo người dùng');
        }
        
        showSuccess('Tạo người dùng thành công!');
        closeModal();
        loadUsers();
        
    } catch (error) {
        showError('Lỗi: ' + error.message);
        console.error('Error creating user:', error);
    }
}

// Delete user
async function deleteUser(id) {
    if (!confirm('Bạn có chắc chắn muốn xóa người dùng này?')) {
        return;
    }
    
    try {
        const csrf = getCsrfToken();
        const headers = {};
        
        if (csrf) {
            headers[csrf.header] = csrf.token;
        }
        
        // This endpoint might not exist - need to implement in backend
        const response = await fetch(`/api/users/${id}`, {
            method: 'DELETE',
            headers: headers
        });
        
        if (!response.ok && response.status !== 204) {
            if (response.status === 404) {
                throw new Error('API endpoint DELETE /api/users/{id} chưa được implement.');
            }
            const result = await response.json();
            throw new Error(result.message || 'Lỗi khi xóa người dùng');
        }
        
        showSuccess('Xóa người dùng thành công!');
        loadUsers();
        
    } catch (error) {
        showError('Lỗi: ' + error.message);
        console.error('Error deleting user:', error);
    }
}

// Form submit handler
document.addEventListener('DOMContentLoaded', function() {
    // Load users on page load
    loadUsers();
    
    // Form submit handler
    const form = document.getElementById('userForm');
    form.addEventListener('submit', function(e) {
        e.preventDefault();
        
        const formData = {
            firstName: document.getElementById('firstName').value,
            lastName: document.getElementById('lastName').value,
            email: document.getElementById('email').value,
            phone: document.getElementById('phone').value || null,
            password: document.getElementById('password').value,
            dob: document.getElementById('dob').value || null
        };
        
        createUser(formData);
    });
    
    // Close modal when clicking outside
    window.onclick = function(event) {
        const modal = document.getElementById('userModal');
        if (event.target === modal) {
            closeModal();
        }
    };
});
