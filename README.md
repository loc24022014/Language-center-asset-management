# Hệ thống Quản lý Tài sản Trung tâm Ngoại ngữ
*(Language Center Asset Management System)*

Dự án phát triển phần mềm theo mô hình Agile Scrum, xây dựng hệ thống quản lý tài sản, trang thiết bị dành cho Trung tâm Ngoại ngữ.

## 🌟 Chức năng Nghiệp vụ Cốt lõi (Core Features)
Hệ thống đáp ứng tối thiểu 04 chức năng nghiệp vụ chính theo yêu cầu:
1. **Quản lý Người dùng (User Management):** Đăng nhập JWT, Phân quyền (Admin, Manager, Staff, Teacher), Khóa/Mở khóa tài khoản.
2. **Quản lý Tài sản (Asset Management):** Thêm mới, Sửa, Xóa, Tìm kiếm, Lọc tài sản theo danh mục. Theo dõi số lượng tổng và số lượng khả dụng.
3. **Quản lý Mượn/Trả (Transactions):** Tạo phiếu mượn tài sản, Duyệt phiếu mượn, Báo trả tài sản, Xác nhận trả. Tự động cộng/trừ số lượng tài sản trong kho.
4. **Thống kê Báo cáo (Reports):** Báo cáo tình trạng tài sản, xuất dữ liệu ra file PDF và Excel.

## 🛠 Công nghệ Sử dụng
- **Backend:** Java 17, Spring Boot 3.x, Spring Security (JWT), Spring Data JPA.
- **Frontend:** HTML5, CSS3 (Vanilla), JavaScript, Chart.js, Lucide Icons.
- **Database:** Microsoft SQL Server.
- **Build Tool:** Apache Maven.

---

## ⚙️ Hướng dẫn Cài đặt & Chạy Dự án (Installation Guide)

### 1. Yêu cầu Hệ thống (Prerequisites)
Để chạy được dự án, máy tính của bạn bắt buộc phải cài đặt:
- **Java Development Kit (JDK) 17** trở lên.
- **Apache Maven** (Cài đặt và cấu hình biến môi trường `MAVEN_HOME`).
- **Microsoft SQL Server** (Bản Developer hoặc Express đều được) & SQL Server Management Studio (SSMS).

### 2. Cấu hình Cơ sở dữ liệu (Database Setup)
**BƯỚC QUAN TRỌNG:** Bạn phải chạy script tạo database trước khi khởi động ứng dụng, nếu không Spring Boot sẽ báo lỗi.

1. Mở SQL Server Management Studio (SSMS).
2. Mở file `database.sql` có sẵn trong thư mục gốc của dự án.
3. Chạy toàn bộ file (Nhấn `F5` hoặc nút `Execute`).
   - Script này sẽ tự động tạo Database tên là `LangCenterAssetDB`.
   - Tạo các bảng: Roles, Users, Assets, Transactions.
   - Thêm các dữ liệu mẫu (Seeding) và các Trigger tự động cập nhật số lượng kho.

### 3. Cấu hình Kết nối SQL Server
Mở file cấu hình tại đường dẫn `src/main/resources/application.properties` và đảm bảo thông tin kết nối khớp với máy của bạn:

```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=LangCenterAssetDB;encrypt=true;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=MatKhauCuaBan123!
```
*(Hãy thay `sa` và `MatKhauCuaBan123!` bằng username và password của SQL Server trên máy bạn).*

### 4. Khởi động Ứng dụng
Mở Terminal / Command Prompt tại thư mục gốc của dự án và chạy lệnh sau:
```bash
mvn clean install
mvn spring-boot:run
```

Sau khi Terminal báo `Started AssetManagementApplication`, mở trình duyệt và truy cập:
👉 **http://localhost:8080**

---

## 🔐 Tài khoản Kiểm thử Mặc định
Hệ thống đã được tạo sẵn 3 cấp độ tài khoản (Mật khẩu chung là: `[tên_tài_khoản]123`):

| Vai trò (Role) | Username | Password | Quyền hạn |
| :--- | :--- | :--- | :--- |
| **Quản trị viên** | `admin` | `admin123` | Toàn quyền hệ thống. Được xóa tài sản, thêm User. |
| **Quản lý** | `manager` | `manager123` | Được thêm/sửa tài sản, duyệt Phiếu mượn/trả. |
| **Giáo viên** | `teacher1` | `teacher123` | Chỉ được xem tài sản và tạo yêu cầu Mượn/Trả. |

---

## 🌳 Quy trình Git & Phân nhánh (Branching Strategy)
Để tuân thủ quy trình **Agile Development**, nhóm áp dụng quy tắc quản lý mã nguồn sau:
- Nhánh `main`: Chứa code đã được Release, ổn định 100%.
- Nhánh `develop`: Chứa code đang tích hợp ở hiện tại.
- Nhánh `feature/*`: Tạo từ `develop` để làm các task cụ thể. (VD: `feature/login`, `feature/asset-crud`).

**Luồng làm việc (Workflow):**
`Developer` -> Code trên `feature/xxx` -> Tạo `Pull Request` -> `Code Review` -> Merge vào `develop` -> Hoàn thành Sprint -> Merge vào `main`.

Mọi commit phải có tiền tố là mã Task trên Jira (Ví dụ: `git commit -m "EDU-15: Fix layout for reports page"`).
