// promotions.js - Client-side JavaScript for Promotion Management

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

// Format date
function formatDateTime(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleString('vi-VN');
}

// Load promotions from API
async function loadPromotions() {
    showElement('loading');
    hideElement('error');
    hideElement('success');
    
    try {
        const response = await fetch('/api/promotions?page=0&size=100&sort=id,asc');
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const apiResponse = await response.json();
        hideElement('loading');
        
        if (!apiResponse.success) {
            showError(apiResponse.message || 'Lỗi khi tải dữ liệu');
            return;
        }
        
        // Handle different response formats
        let promotions = [];
        if (apiResponse.data) {
            if (Array.isArray(apiResponse.data)) {
                promotions = apiResponse.data;
            } else if (Array.isArray(apiResponse.data.content)) {
                promotions = apiResponse.data.content;
            } else if (Array.isArray(apiResponse.data.promotions)) {
                promotions = apiResponse.data.promotions;
            }
        }
        
        renderPromotions(promotions);
        
    } catch (error) {
        hideElement('loading');
        showError('Lỗi kết nối: ' + error.message);
        console.error('Error loading promotions:', error);
    }
}

// Render promotions table
function renderPromotions(promotions) {
    const tbody = document.getElementById('promotionsBody');
    tbody.innerHTML = '';
    
    if (!promotions || promotions.length === 0) {
        tbody.innerHTML = '<tr><td colspan="7" style="text-align: center;">Không có khuyến mãi nào</td></tr>';
        return;
    }
    
    promotions.forEach(promotion => {
        const row = tbody.insertRow();
        const statusBadge = promotion.active 
            ? '<span class="badge badge-success">Đang hoạt động</span>' 
            : '<span class="badge badge-danger">Ngừng hoạt động</span>';
        
        const discountPercent = (promotion.discountPercent * 100).toFixed(0);
        
        row.innerHTML = `
            <td>${escapeHtml(String(promotion.id))}</td>
            <td>${escapeHtml(promotion.name)}</td>
            <td>${discountPercent}%</td>
            <td>${formatDateTime(promotion.startDate)}</td>
            <td>${formatDateTime(promotion.endDate)}</td>
            <td>${statusBadge}</td>
            <td>
                <button class="btn btn-danger" onclick="deletePromotion(${promotion.id})">🗑️ Xóa</button>
            </td>
        `;
    });
}

// Open create modal
function openCreateModal() {
    document.getElementById('modalTitle').textContent = 'Tạo Khuyến Mãi Mới';
    document.getElementById('promotionForm').reset();
    showElement('promotionModal');
}

// Close modal
function closeModal() {
    hideElement('promotionModal');
}

// Create promotion
async function createPromotion(formData) {
    try {
        const csrf = getCsrfToken();
        const headers = {
            'Content-Type': 'application/json'
        };
        
        if (csrf) {
            headers[csrf.header] = csrf.token;
        }
        
        const response = await fetch('/api/promotions', {
            method: 'POST',
            headers: headers,
            body: JSON.stringify(formData)
        });
        
        const result = await response.json();
        
        if (!response.ok || !result.success) {
            throw new Error(result.message || 'Lỗi khi tạo khuyến mãi');
        }
        
        showSuccess('Tạo khuyến mãi thành công!');
        closeModal();
        loadPromotions();
        
    } catch (error) {
        showError('Lỗi: ' + error.message);
        console.error('Error creating promotion:', error);
    }
}

// Delete promotion
async function deletePromotion(id) {
    if (!confirm('Bạn có chắc chắn muốn xóa khuyến mãi này?')) {
        return;
    }
    
    try {
        const csrf = getCsrfToken();
        const headers = {};
        
        if (csrf) {
            headers[csrf.header] = csrf.token;
        }
        
        const response = await fetch(`/api/promotions/${id}`, {
            method: 'DELETE',
            headers: headers
        });
        
        if (!response.ok && response.status !== 204) {
            const result = await response.json();
            throw new Error(result.message || 'Lỗi khi xóa khuyến mãi');
        }
        
        showSuccess('Xóa khuyến mãi thành công!');
        loadPromotions();
        
    } catch (error) {
        showError('Lỗi: ' + error.message);
        console.error('Error deleting promotion:', error);
    }
}

// Form submit handler
document.addEventListener('DOMContentLoaded', function() {
    // Load promotions on page load
    loadPromotions();
    
    // Form submit handler
    const form = document.getElementById('promotionForm');
    form.addEventListener('submit', function(e) {
        e.preventDefault();
        
        const formData = {
            name: document.getElementById('name').value,
            description: document.getElementById('description').value || null,
            discountPercent: parseFloat(document.getElementById('discountPercent').value),
            startDate: document.getElementById('startDate').value,
            endDate: document.getElementById('endDate').value
        };
        
        createPromotion(formData);
    });
    
    // Close modal when clicking outside
    window.onclick = function(event) {
        const modal = document.getElementById('promotionModal');
        if (event.target === modal) {
            closeModal();
        }
    };
});
