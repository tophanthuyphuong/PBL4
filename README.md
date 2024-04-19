# PBL4: DỰ ÁN HỆ ĐIỀU HÀNH & MẠNG MÁY TÍNH
## ĐỀ TÀI: Xây dựng website thư điện tử (E-mail) với lập trình Socket
### Giới thiệu
Đây là dự án ứng dụng lập trình Socket bằng ngôn ngữ Java để kết nối 2 hay nhiều máy tính trong 1 mạng LAN, từ đó trao đổi thư điện tử với nhau.

Chương trình được viết theo mô hình Client-Server:
- Server đóng vai trò là 1 MailServer trong mô hình Hộp thư lưu, có chức năng lưu trữ thông tin Email và thông tin User.
- Client là MailClient có thể kết nối đến MailServer để nhận thông tin Email
### Cấu trúc các nhánh
#### 1. mailserver
- Là nhánh chứa mã nguồn của phía Server
#### 2. mailclient
- Là nhánh chứa mã nguồn phía Client
### Chức năng
- Đăng nhập, đăng ký, quản lý tài khoản
- Gửi Email có đính kèm tệp
- Hiển thị hộp thư theo các trạng thái (Hộp thư đến, Đã gửi, Thư rác, Spam)
- Đánh dấu trạng thái thư (Chưa đọc, Đã đọc, Đã xóa, Spam)
- Xem chi tiết Email
- Hiện thông báo khi có Email đến
- Log việc gửi thư ở phía MailServer

