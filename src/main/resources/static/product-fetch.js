// product-fetch.js

document.addEventListener('DOMContentLoaded', () => {
    const tableBody = document.getElementById('product-data');
    const table = document.getElementById('product-table');
    const loadingMessage = document.getElementById('loading-message');
    const errorMessage = document.getElementById('error-message');

    const API_URL = '/api/v1/products?page=0&size=10&sort=id,asc';

    const formatCurrency = (value) => {
        return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
    };

    fetch(API_URL)
        .then(response => {
            if (!response.ok) {
                throw new Error(`Server status lỗi: ${response.status}`);
            }
            return response.json();
        })
        .then(apiResponse => {
            loadingMessage.style.display = 'none';

            if (apiResponse.success && apiResponse.data && apiResponse.data.content) {
                const products = apiResponse.data.content;

                if (products.length === 0) {
                    errorMessage.textContent = 'Hết hàng rồi, không có sản phẩm nào.';
                    errorMessage.style.display = 'block';
                    return;
                }

                products.forEach(product => {
                    const row = tableBody.insertRow();

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

            } else {
                errorMessage.textContent = `Lỗi logic từ Server: ${apiResponse.message || 'Không rõ lỗi'}`;
                errorMessage.style.display = 'block';
            }
        })
        .catch(error => {
            loadingMessage.style.display = 'none';
            console.error('Lỗi khi fetch Product:', error);
            errorMessage.textContent = `Lỗi mạng hoặc server: ${error.message}`;
            errorMessage.style.display = 'block';
        });
});