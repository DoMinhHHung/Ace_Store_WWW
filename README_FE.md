# Frontend Interface - Ace Store WWW

## Giới Thiệu

Frontend interface sử dụng Thymeleaf + JavaScript để quản lý các chức năng chính của Ace Store.

## Các Trang Frontend

Sau khi khởi động ứng dụng, bạn có thể truy cập các trang sau:

- **Quản lý Sản phẩm**: http://localhost:8080/fe/products
- **Quản lý Người dùng**: http://localhost:8080/fe/users  
- **Quản lý Khuyến mãi**: http://localhost:8080/fe/promotions
- **Quản lý Admin**: http://localhost:8080/fe/admins

## Cách Chạy

### 1. Yêu cầu hệ thống
- Java 17 hoặc cao hơn
- Maven 3.6+ 

### 2. Cấu hình application.properties

Trước tiên, copy file mẫu và điều chỉnh nếu cần:
```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

File này đã được cấu hình với các giá trị mặc định cho development.

### 3. Build và chạy ứng dụng

```bash
# Build project
./mvnw clean install -DskipTests

# Chạy ứng dụng
./mvnw spring-boot:run
```

Hoặc:

```bash
# Chạy trực tiếp từ JAR
java -jar target/Ace_Store_WWW-0.0.1-SNAPSHOT.jar
```

### 4. Truy cập ứng dụng

Sau khi ứng dụng khởi động thành công, mở trình duyệt và truy cập:
- **Base URL**: http://localhost:8080
- **H2 Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:ace_store_db`
  - Username: `sa`
  - Password: (để trống)

## Tính Năng

### Quản lý Sản phẩm (/fe/products)
- ✅ Xem danh sách sản phẩm
- ✅ Thêm sản phẩm mới
- ✅ Xóa sản phẩm
- 📊 Hiển thị: ID, Tên, Thương hiệu, Loại, Giá, Tồn kho

### Quản lý Người dùng (/fe/users)
- ✅ Xem danh sách người dùng
- ✅ Thêm người dùng mới (qua signup API)
- ✅ Xóa người dùng
- 📊 Hiển thị: ID, Họ, Tên, Email, SĐT, Trạng thái

### Quản lý Khuyến mãi (/fe/promotions)
- ✅ Xem danh sách khuyến mãi
- ✅ Tạo chương trình khuyến mãi mới
- ✅ Xóa khuyến mãi
- 📊 Hiển thị: ID, Tên chương trình, % Giảm giá, Ngày bắt đầu/kết thúc, Trạng thái

### Quản lý Admin (/fe/admins)
- ✅ Xem danh sách admin
- 📊 Hiển thị: ID, Họ tên, Email, SĐT, Roles, Trạng thái
- ℹ️ Để cấp quyền admin, sử dụng API `/api/admin/grant-admin/{userId}` (yêu cầu JWT token với role ADMIN)

## API Endpoints Backend

### Products API
- `GET /api/products` - Lấy danh sách sản phẩm
- `GET /api/products/{id}` - Lấy chi tiết sản phẩm
- `POST /api/products` - Tạo sản phẩm mới
- `PUT /api/products/{id}` - Cập nhật sản phẩm
- `DELETE /api/products/{id}` - Xóa sản phẩm

### Users API
- `GET /api/users` - Lấy danh sách người dùng
- `GET /api/users/me` - Lấy thông tin user hiện tại (yêu cầu JWT)
- `POST /api/auth/signup` - Đăng ký người dùng mới
- `PUT /api/users/{id}` - Cập nhật thông tin
- `DELETE /api/users/{id}` - Xóa người dùng

### Promotions API
- `GET /api/promotions` - Lấy danh sách khuyến mãi
- `GET /api/promotions/{id}` - Lấy chi tiết khuyến mãi
- `POST /api/promotions` - Tạo khuyến mãi mới
- `PUT /api/promotions/{id}` - Cập nhật khuyến mãi
- `DELETE /api/promotions/{id}` - Xóa khuyến mãi

### Admin API
- `POST /api/admin/grant-admin/{userId}` - Cấp quyền admin cho user (yêu cầu role ADMIN)

## Cấu Hình

### Database (H2 In-Memory)
File `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:h2:mem:ace_store_db
spring.jpa.hibernate.ddl-auto=update
spring.h2.console.enabled=true
```

### Security
- CSRF đã được disabled cho API endpoints
- Các đường dẫn `/fe/**`, `/api/**`, `/js/**`, `/css/**` đều được permitAll()
- Để sử dụng các API yêu cầu authentication, cần JWT token

### Thymeleaf
```properties
spring.thymeleaf.cache=false
```

## Cấu Trúc File

```
src/main/
├── java/
│   └── iuh/fit/se/ace_store_www/
│       ├── controller/
│       │   ├── views/
│       │   │   └── FEViewController.java       # View controller cho frontend
│       │   ├── ProductController.java
│       │   ├── UserController.java
│       │   ├── PromotionController.java
│       │   └── AdminController.java
│       ├── service/
│       ├── repository/
│       └── entity/
└── resources/
    ├── templates/
    │   ├── products.html                        # Trang quản lý sản phẩm
    │   ├── users.html                          # Trang quản lý người dùng
    │   ├── promotions.html                     # Trang quản lý khuyến mãi
    │   └── admins.html                         # Trang quản lý admin
    ├── static/
    │   └── js/
    │       ├── products.js                     # Client JS cho products
    │       ├── users.js                        # Client JS cho users
    │       ├── promotions.js                   # Client JS cho promotions
    │       └── admins.js                       # Client JS cho admins
    └── application.properties
```

## Security & Best Practices

### JavaScript Files
- ✅ CSRF token được đọc từ meta tags
- ✅ HTML escaping để phòng XSS attacks
- ✅ Error handling cho tất cả API calls
- ✅ Confirmation dialogs trước khi xóa

### API Responses
Tất cả API trả về format:
```json
{
  "success": true/false,
  "message": "...",
  "data": {...}
}
```

## Lưu Ý

1. **Database**: Sử dụng H2 in-memory, dữ liệu sẽ mất khi restart ứng dụng
2. **Authentication**: Một số API yêu cầu JWT token. Để test đầy đủ, cần login và lấy token trước
3. **CORS**: Đã được cấu hình cho phép truy cập từ frontend
4. **Production**: Trước khi deploy production, cần:
   - Chuyển sang database persistent (PostgreSQL, MySQL)
   - Bật CSRF protection
   - Cấu hình HTTPS
   - Thêm rate limiting

## Troubleshooting

### Port đã được sử dụng
Nếu port 8080 đã được sử dụng, thêm vào application.properties:
```properties
server.port=8081
```

### Lỗi kết nối database
Kiểm tra H2 Console và đảm bảo URL đúng: `jdbc:h2:mem:ace_store_db`

### API trả về 401/403
- Kiểm tra Security configuration
- Đảm bảo endpoint đã được permitAll() hoặc cung cấp JWT token hợp lệ

## Tác Giả

Ace Store Development Team

## License

Private Project
