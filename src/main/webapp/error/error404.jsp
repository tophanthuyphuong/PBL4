<%@page import="utils.Constant"%>
<%@page import="java.util.Base64"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Error 404</title>
<% String path = request.getScheme() + "://" + request.getServerName() + ":" 
	+ request.getServerPort() + request.getContextPath(); %>
<link rel="stylesheet" href="<%=path %>/assets/css/styles.css" />
<link rel="stylesheet" href="<%=path %>/assets/css/error404.css" />
</head>
<body>
<div class="error404">
	<div>
		<h1>404</h1>
        <strong class="error-message">Trang bạn truy cập không tồn tại!</strong>
    </div>
    <img src="<%=path %>/assets/img/cat.png">
    <% String homePath = request.getScheme() + "://" + request.getServerName() + ":" 
    	+ request.getServerPort() + "/" + "mail/mail?my_action=" + Base64.getEncoder().encodeToString(Constant.INCOMING_MAILBOX.getBytes()); %>
        <button class="home-link home-link__error" onclick="window.location.href = '<%=homePath %>';">
            <i class="fa-solid fa-chevron-left"></i>
            Trở về trang chủ   
        </button>
</div>
</body>
</html>