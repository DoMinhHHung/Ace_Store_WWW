// product-fetch.js

document.addEventListener('DOMContentLoaded', () => {
    const tableBody = document.getElementById('product-data');
    const table = document.getElementById('product-table');
    const loadingMessage = document.getElementById('loading-message');
    const errorMessage = document.getElementById('error-message');

    // SỬA ĐỊA CHỈ API: BỎ /v1 như mày muốn
    const API_URL = '/api/products?page=0&size=10&sort=id,asc';

    // Định dạng tiền tệ VND
    const formatCurrency = (value) => {
        return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
    };

    fetch(API_URL)
        .then(response => {
            if (!response.ok) {
                // Lỗi HTTP status code (4xx, 5xx)
                throw new Error(`Server status lỗi: ${response.status}`);
            }
            return response.json();
        })
        .then(apiResponse => {
            // Luôn ẩn loading và mặc định ẩn lỗi khi có response mới
            loadingMessage.style.display = 'none';
            errorMessage.style.display = 'none';

            // Nếu backend báo failure rõ ràng
            if (!apiResponse.success) {
                table.style.display = 'none';
                errorMessage.textContent = `LỖI BACKEND: ${apiResponse.message || 'Server trả về lỗi không xác định.'}`;
                errorMessage.style.display = 'block';
                return;
            }

            // NORMALIZE: backend của bạn trả về pageData như { products: [...], totalPages: ..., ... }
            // một số API khác trả về { content: [...] } (Spring Page), hoặc trả thẳng mảng.
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

            if (!products || products.length === 0) {
                errorMessage.textContent = 'Hết hàng rồi, không có sản phẩm nào.';
                errorMessage.style.display = 'block';
                table.style.display = 'none';
                return;
            }

            products.forEach(product => {
                const row = tableBody.insertRow();

                // Tính toán giá cuối cùng
                const discountPercent = product.promotion ? product.promotion.discountPercent : 0;
                const finalPrice = product.price * (1 - discountPercent);

                // CSS highlight nếu có KM
                if (discountPercent > 0) {
                    row.className = 'promo';
                }

                row.insertCell().textContent = product.id;
                row.insertCell().textContent = product.name;
                row.insertCell().textContent = product.productType;
                // Giá gốc
                row.insertCell().textContent = formatCurrency(product.price);
                row.insertCell().textContent = product.stock;
                // KM %
                row.insertCell().textContent = `${(discountPercent * 100).toFixed(0)}%`;
                // Giá cuối cùng
                row.insertCell().textContent = formatCurrency(finalPrice);
            });

            table.style.display = 'table';
        })
        .catch(error => {
            // Case 3: Lỗi mạng/status 4xx/5xx
            loadingMessage.style.display = 'none';
            table.style.display = 'none';
            console.error('Lỗi khi fetch Product:', error);
            errorMessage.textContent = `Lỗi kết nối/mạng: ${error.message}. Thử kiểm tra console F12 để biết chi tiết.`;
            errorMessage.style.display = 'block';
        });
});