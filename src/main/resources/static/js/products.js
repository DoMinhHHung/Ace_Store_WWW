// products.js - Client-side JavaScript for Product Management

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

// Format currency
function formatCurrency(value) {
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
}

// Load products from API
async function loadProducts() {
    showElement('loading');
    hideElement('error');
    hideElement('success');
    
    try {
        const response = await fetch('/api/products?page=0&size=100&sort=id,asc');
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
        let products = [];
        if (apiResponse.data) {
            if (Array.isArray(apiResponse.data)) {
                products = apiResponse.data;
            } else if (Array.isArray(apiResponse.data.content)) {
                products = apiResponse.data.content;
            } else if (Array.isArray(apiResponse.data.products)) {
                products = apiResponse.data.products;
            }
        }
        
        renderProducts(products);
        
    } catch (error) {
        hideElement('loading');
        showError('Lỗi kết nối: ' + error.message);
        console.error('Error loading products:', error);
    }
}

// Render products table
function renderProducts(products) {
    const tbody = document.getElementById('productsBody');
    tbody.innerHTML = '';
    
    if (!products || products.length === 0) {
        tbody.innerHTML = '<tr><td colspan="7" style="text-align: center;">Không có sản phẩm nào</td></tr>';
        return;
    }
    
    products.forEach(product => {
        const row = tbody.insertRow();
        row.innerHTML = `
            <td>${escapeHtml(String(product.id))}</td>
            <td>${escapeHtml(product.name)}</td>
            <td>${escapeHtml(product.brand || '-')}</td>
            <td>${escapeHtml(product.productType || '-')}</td>
            <td>${formatCurrency(product.price)}</td>
            <td>${escapeHtml(String(product.stock))}</td>
            <td>
                <button class="btn btn-danger" onclick="deleteProduct(${product.id})">🗑️ Xóa</button>
            </td>
        `;
    });
}

// Open create modal
function openCreateModal() {
    document.getElementById('modalTitle').textContent = 'Thêm Sản Phẩm Mới';
    document.getElementById('productForm').reset();
    showElement('productModal');
}

// Close modal
function closeModal() {
    hideElement('productModal');
}

// Create product
async function createProduct(formData) {
    try {
        const csrf = getCsrfToken();
        const headers = {
            'Content-Type': 'application/json'
        };
        
        if (csrf) {
            headers[csrf.header] = csrf.token;
        }
        
        const response = await fetch('/api/products', {
            method: 'POST',
            headers: headers,
            body: JSON.stringify(formData)
        });
        
        const result = await response.json();
        
        if (!response.ok || !result.success) {
            throw new Error(result.message || 'Lỗi khi tạo sản phẩm');
        }
        
        showSuccess('Tạo sản phẩm thành công!');
        closeModal();
        loadProducts();
        
    } catch (error) {
        showError('Lỗi: ' + error.message);
        console.error('Error creating product:', error);
    }
}

// Delete product
async function deleteProduct(id) {
    if (!confirm('Bạn có chắc chắn muốn xóa sản phẩm này?')) {
        return;
    }
    
    try {
        const csrf = getCsrfToken();
        const headers = {};
        
        if (csrf) {
            headers[csrf.header] = csrf.token;
        }
        
        const response = await fetch(`/api/products/${id}`, {
            method: 'DELETE',
            headers: headers
        });
        
        if (!response.ok && response.status !== 204) {
            const result = await response.json();
            throw new Error(result.message || 'Lỗi khi xóa sản phẩm');
        }
        
        showSuccess('Xóa sản phẩm thành công!');
        loadProducts();
        
    } catch (error) {
        showError('Lỗi: ' + error.message);
        console.error('Error deleting product:', error);
    }
}

// Form submit handler
document.addEventListener('DOMContentLoaded', function() {
    // Load products on page load
    loadProducts();
    
    // Form submit handler
    const form = document.getElementById('productForm');
    form.addEventListener('submit', function(e) {
        e.preventDefault();
        
        const formData = {
            name: document.getElementById('name').value,
            brand: document.getElementById('brand').value || null,
            description: document.getElementById('description').value || null,
            price: parseFloat(document.getElementById('price').value),
            stock: parseInt(document.getElementById('stock').value),
            productType: document.getElementById('productType').value,
            mainImage: document.getElementById('mainImage').value || null
        };
        
        createProduct(formData);
    });
    
    // Close modal when clicking outside
    window.onclick = function(event) {
        const modal = document.getElementById('productModal');
        if (event.target === modal) {
            closeModal();
        }
    };
});
