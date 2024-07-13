### Hướng Dẫn Sử Dụng Ứng Dụng CameraXApp

Ứng dụng CameraXApp cho phép người dùng chụp ảnh và quay video sử dụng thư viện CameraX của Android. Dưới đây là hướng dẫn chi tiết cách sử dụng ứng dụng.


#### Demo
Link: [https://share.cleanshot.com/XVvgwLcF](https://share.cleanshot.com/XVvgwLcF)

#### Cài Đặt

1. Clone dự án từ GitHub hoặc tải xuống mã nguồn.
2. Mở dự án bằng Android Studio.
3. Đảm bảo rằng Android SDK và các công cụ liên quan đã được cài đặt và cấu hình đúng cách.
4. Chạy ứng dụng trên thiết bị hoặc máy ảo Android.

#### Chức Năng Chính

- **Chụp Ảnh**: Người dùng có thể chụp ảnh sử dụng camera trước hoặc sau của thiết bị.
- **Quay Video**: Người dùng có thể quay video với độ phân giải cao.
- **Xem Trước Camera**: Trước khi chụp ảnh hoặc quay video, người dùng có thể xem trước hình ảnh từ camera.
- **Lưu và Chia Sẻ**: Ảnh và video được lưu vào bộ nhớ thiết bị và có thể được chia sẻ lên các dịch vụ lưu trữ đám mây hoặc mạng xã hội.

#### Cấu Hình

Ứng dụng sử dụng Supabase cho việc lưu trữ và quản lý dữ liệu. Để cấu hình, bạn cần cung cấp các thông tin sau trong tệp `local.properties`:

- `SUPABASE_ANON_KEY`: Khóa truy cập ẩn danh cho Supabase.
- `SUPABASE_URL`: URL của dự án Supabase.
- `SUPABASE_ROLE`: Khóa truy cập với quyền hạn cao hơn.
- `SECRET`: Một khóa bí mật dùng cho mục đích mã hóa hoặc xác thực.

#### Phát Triển và Mở Rộng

Dự án được cấu trúc theo mô hình MVVM, giúp tách biệt logic xử lý dữ liệu và giao diện người dùng, làm cho việc bảo trì và phát triển dự án trở nên dễ dàng hơn. Bạn có thể mở rộng dự án bằng cách thêm các chức năng mới như chỉnh sửa ảnh/video, thêm bộ lọc, hoặc tích hợp AI để nhận diện khuôn mặt.

#### Thư Viện và Công Nghệ Sử Dụng

- **CameraX**: Thư viện hỗ trợ chụp ảnh và quay video.
- Chi tiết về các thư viện khác có thể được tìm thấy trong tệp `build.gradle`.