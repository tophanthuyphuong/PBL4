<%@page import="utils.Constant"%>
<%@page import="java.util.Base64"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<!--=============== ICONS ===============-->
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css"
	integrity="sha512-z3gLpd7yknf1YoNbCzqRKc4qyor8gaKU1qmn+CShxbuBusANI9QpRohGBreCFkKxLhei6S9CQXFEbbKuqLg0DA=="
	crossorigin="anonymous" referrerpolicy="no-referrer" />

<!--=============== BOOTSTRAP & CSS ===============-->
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap@4.3.1/dist/css/bootstrap.min.css"
	integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T"
	crossorigin="anonymous">
<link rel="stylesheet" href="../assets/css/styles.css">
<link rel="stylesheet" href="../assets/css/layout_home.css">
<link rel="stylesheet" href="../assets/css/sidebar_account.css">
</head>
<body>
	<%
		String fullname = (String) session.getAttribute(Constant.USERNAME);//(String) request.getAttribute("fullname");
		String email = (String) session.getAttribute(Constant.EMAIL);//request.getAttribute("email");
		String birthday = request.getAttribute("birthday") != null ? (String) request.getAttribute("birthday") : "";
		String phonenumber = (String) request.getAttribute("phonenumber");
		
	%>
	<aside class="side_bar">
		<div class="d-flex flex-column align-items-center text-center">
			<img src="https://bootdey.com/img/Content/avatar/avatar6.png"
				alt="User" class="rounded-circle p-1 bg-primary" width="110">
			<div class="mt-3">
				<h2><%=fullname %></h2>
				<p class="text-secondary mb-1 font-italic"><%=email %></p>
			</div>
		</div>


		<ul class="side_bar__list">
			<li class="side_bar__item active"><i
				class="fa-solid fa-pen-to-square"></i> <a class="side_bar__link"
				href="../authentication/authentication?my_action=update">Chỉnh sửa thông tin</a></li>
			<li class="side_bar__item"><i class="fa-solid fa-lock"></i> <a
				class="side_bar__link" href="../manage-account/account_password.jsp">Thay đổi mật khẩu</a></li>
			<li class="side_bar__item"><i class="fa-solid fa-house"></i> 
			<a href="<%=("/mail/mail?my_action=" + Base64.getEncoder().encodeToString(Constant.INCOMING_MAILBOX.getBytes())) %>"
				class="side_bar__link">Quay lại Trang chủ</a></li>
		</ul>
	</aside>
</body>
<script>
const currentURL = window.location.pathname;
const sidebarItems = document.querySelectorAll('.side_bar__item');

sidebarItems.forEach(item => {
  const link = item.querySelector('.side_bar__link');
  const href = link.getAttribute('href').replace("..", "");

  if (currentURL === href) {
    sidebarItems.forEach(item => {
      item.classList.remove('active');
    });

    item.classList.add('active');
  }
});
</script>
</html>