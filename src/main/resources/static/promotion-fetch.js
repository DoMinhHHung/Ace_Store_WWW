// promotion-fetch.js

document.addEventListener('DOMContentLoaded', () => {
    const tableBody = document.getElementById('promotion-data');
    const table = document.getElementById('promotion-table');
    const loadingMessage = document.getElementById('loading-message');
    const errorMessage = document.getElementById('error-message');

    const API_URL = '/api/promotions?page=0&size=20&sort=startDate,desc';

    fetch(API_URL)
        .then(response => {
            if (!response.ok) {
                throw new Error(`Server status lỗi: ${response.status}`);
            }
            return response.json();
        })
        .then(apiResponse => {
            loadingMessage.style.display = 'none';

            // Nếu backend báo failure rõ ràng
            if (!apiResponse.success) {
                errorMessage.textContent = `Lỗi Backend: ${apiResponse.message || 'Error không rõ'}`;
                errorMessage.style.display = 'block';
                table.style.display = 'none';
                return;
            }

            // NORMALIZE giống product: data.promotions, data.content (Spring Page) hoặc data là mảng
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

            if (!promotions || promotions.length === 0) {
                errorMessage.textContent = 'Chưa có khuyến mãi nào. Thiếu deal rồi!';
                errorMessage.style.display = 'block';
                table.style.display = 'none';
                return;
            }

            promotions.forEach(promo => {
                const row = tableBody.insertRow();

                row.insertCell().textContent = promo.id;
                row.insertCell().textContent = promo.name;
                // Format % cho đẹp
                row.insertCell().textContent = `${(promo.discountPercent * 100).toFixed(0)}%`;

                // Format ngày giờ (thư viện như MomentJS sẽ tốt hơn, nhưng dùng tạm Date cơ bản)
                row.insertCell().textContent = new Date(promo.startDate).toLocaleString('vi-VN');
                row.insertCell().textContent = new Date(promo.endDate).toLocaleString('vi-VN');

                // Trạng Thái Active/Inactive
                const statusCell = row.insertCell();
                if (promo.active) {
                    statusCell.textContent = 'Active (Đang chạy)';
                    statusCell.className = 'active';
                } else {
                    statusCell.textContent = 'Inactive (Tạm dừng)';
                    statusCell.className = 'inactive';
                }
            });

            table.style.display = 'table';
        })
        .catch(error => {
            loadingMessage.style.display = 'none';
            console.error('Lỗi khi fetch Promotion:', error);
            errorMessage.textContent = `Lỗi kết nối/mạng: ${error.message}`;
            errorMessage.style.display = 'block';
        });
});