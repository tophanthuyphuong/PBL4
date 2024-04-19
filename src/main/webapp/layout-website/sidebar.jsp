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
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css"
        integrity="sha512-z3gLpd7yknf1YoNbCzqRKc4qyor8gaKU1qmn+CShxbuBusANI9QpRohGBreCFkKxLhei6S9CQXFEbbKuqLg0DA=="
        crossorigin="anonymous" referrerpolicy="no-referrer" />

    <!--=============== BOOTSTRAP & CSS ===============-->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.3.1/dist/css/bootstrap.min.css"
        integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
    <link rel="stylesheet" href="../assets/css/styles.css">
	<link rel="stylesheet" href="../assets/css/layout_home.css">
</head>
<body>
<aside class="side_bar">
			<% String url = "/mail/mail?my_action="; %>
            <a href="<%=(url + Base64.getEncoder().encodeToString(Constant.INCOMING_MAILBOX.getBytes())) %>">
            	<img class="logo_img" src="../assets/img/logo.png">
            </a>
            <ul class="side_bar__list">
                <li class="side_bar__item text-center add_email ">
                    <i class="fa-solid fa-plus"></i>
                    <a class="side_bar__link" href="../mail/inbox.jsp">Tạo thư mới</a>
                </li>
                
                <li class="side_bar__item">
                    <i class="fa-solid fa-envelope"></i>
                    <a class="side_bar__link d-flex align-items-center" 
                    	href="<%=(url + Base64.getEncoder().encodeToString(Constant.INCOMING_MAILBOX.getBytes())) %>">
                    	Hộp thư đến
                    	<h4 class="ml-2 mb-0 font" id="new-message">
                    		(<%=session.getAttribute(Constant.COUNT_UNREAD) != null ? (Integer)session.getAttribute(Constant.COUNT_UNREAD) : 0 %>)
                    	</h4>
                    </a>
                </li>
                <li class="side_bar__item">
                    <i class="fa-solid fa-paper-plane"></i>
                    <a class="side_bar__link" href="<%=(url + Base64.getEncoder().encodeToString(Constant.OUTBOX.getBytes()))%>">Đã gửi</a>
                </li>
                <li class="side_bar__item">
                    <i class="fa-solid fa-list-ul"></i>
                    <a class="side_bar__link" href="<%=(url + Base64.getEncoder().encodeToString(Constant.ALL_MAILBOX.getBytes()))%>">Tất cả thư</a>
                </li>
                <li class="side_bar__item">
                    <i class="fa-solid fa-trash"></i>
                    <a class="side_bar__link" href="<%=(url + Base64.getEncoder().encodeToString(Constant.DELETED_MAILBOX.getBytes()))%>">Thùng rác</a>
                </li>
                <li class="side_bar__item">
                    <i class="fa-solid fa-circle-exclamation"></i>
                    <a class="side_bar__link" href="<%=(url + Base64.getEncoder().encodeToString(Constant.SPAM_MAILBOX.getBytes()))%>">Spam</a>
                </li>
                <li class="side_bar__item">
                	<i class="fa-solid fa-arrow-right-from-bracket"></i>
		            <a class="side_bar__link" href="../authentication/authentication?my_action=logout">Đăng xuất</a>
                </li>
            </ul>
        </aside>
</body>

<%--<script src="../assets/js/sidebar.js"></script> --%>
</script>
<script>
	var currentUrl = window.location.pathname + window.location.search;
	if (currentUrl === "/mail/inbox.jsp") {
		sessionStorage.setItem('currentURL', "../mail/inbox.jsp");
	}
	var sidebarLinks = document.querySelectorAll('.side_bar__link');
	sidebarLinks.forEach( item => {
        var url = item.getAttribute('href');
        // Lưu URL vào session
        if (currentUrl === url) {
        	sessionStorage.setItem('currentURL', url);
        }
	});

	var currentURL = sessionStorage.getItem('currentURL');
	if (currentURL == null) {
		currentURL = "<%=(url + Base64.getEncoder().encodeToString(Constant.INCOMING_MAILBOX.getBytes())) %>";
	}
	
  	const sidebarItems = document.querySelectorAll('.side_bar__item');
  	sidebarItems.forEach(item => {
    	const link = item.querySelector('.side_bar__link');
    	const href = link.getAttribute('href');
    	if (currentURL === href) {
      	sidebarItems.forEach(item => {
        	item.classList.remove('active');
      	});

      	item.classList.add('active');
    }
  });
</script>
</html>