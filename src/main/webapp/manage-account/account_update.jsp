<%@page import="dto.UserDetailDTO"%>
<%@page import="utils.Constant"%>
<%@page import="dto.MailDetailDTO"%>
<%@page import="java.util.List"%>
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
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap@4.3.1/dist/css/bootstrap.min.css"
	integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T"
	crossorigin="anonymous">
<link rel="stylesheet" href="../assets/css/styles.css">
<link rel="stylesheet" href="../assets/css/sidebar_account.css">
<link rel="stylesheet" href="../assets/css/account_setting.css">
<title>Cập nhật tài khoản</title>
</head>
<body>
	<%
		String fullname = (String) request.getAttribute("fullname");
		String email = (String) request.getAttribute("email");
		String birthday = request.getAttribute("birthday") != null ? (String) request.getAttribute("birthday") : "";
		String phonenumber = (String) request.getAttribute("phonenumber");
		Boolean gender = (Boolean) request.getAttribute("gender");
	%>
	<div class="row account">
		<div class="clear_fixed"></div>
		<jsp:include page="../layout-website/sidebar_account.jsp"></jsp:include>

		<div class="container">
			<jsp:include page="../layout-website/header.jsp"></jsp:include>
			<div class="content_container">
                <h1 class="title">Thông tin tài khoản</h1>
                <div class="content">
                    <form name="update_account" method="post" 
                    	action="/authentication/authentication?my_action=submit-update">
                        <div class="card">
                            <div class="card-body">
                            	<h3 class="text-danger text-center font-italic mb-4" id="error_message"></h3>
                                <div class="row mb-4 d-flex align-items-center">
                                    <div class="col-sm-3">
                                        <h4 class="label" class="mb-0">Họ tên *</h4>
                                    </div>
                                    <div class="col-sm-9 text-secondary">
                                        <input type="text"  name="fullname" class="form-control input_txt" id="fullname" value="<%=fullname%>">
                                    </div>
                                </div>
                                <div class="row mb-4 d-flex align-items-center">
                                    <div class="col-sm-3">
                                        <h4 class="label" class="mb-0">Tài khoản *</h4>
                                    </div>
                                    <div class="col-sm-9 text-secondary">
                                        <input type="text" class="form-control input_txt" id="email" value="<%=email%>" readonly>
                                    </div>
                                </div>
                                <div class="row mb-4 d-flex align-items-center">
                                    <div class="col-sm-3">
                                        <h4 class="label" class="mb-0">Điện thoại *</h4>
                                    </div>
                                    <div class="col-sm-9 text-secondary">
                                        <input type="text" name="phonenumber" class="form-control input_txt" id="phone_number" 
                                        value="<%=phonenumber%>" placeholder="(84) 93-222-2222">
                                    </div>
                                </div>
                                <div class="row mb-4 d-flex align-items-center">
                                    <div class="col-sm-3">
                                        <h4 class="label" class="mb-0">Ngày sinh *</h4>
                                    </div>
                                    <div class="col-sm-9 text-secondary">
                                        <input type="date" name="birthday" class="form-control input_txt" 
                                        	id="birthday" value="<%=birthday%>">
                                    </div>
                                </div>
                                <div class="row mb-4 d-flex align-items-center">
                                    <div class="col-sm-3">
                                        <h4 class="label" class="mb-0">Giới tính</h4>
                                    </div>
                                    <div class="col-sm-9 text-secondary">
                                        <select name="gender" class="form-select form-control input_txt" aria-label="">
                                            <% if (gender) { %>
                                            	<option value="false">Nam</option>
                                            	<option value="true" selected>Nữ</option>
                                            <% } else { %>
                                            	<option value="fasle" selected>Nam</option>
                                            	<option value="true">Nữ</option>
                                            <% } %>
                                         </select>
                                    </div>
                                </div>
                                <div class="row">
                                        <input type="button" id="btn_submit" class="btn px-4 btn_save" value="Cập nhật" onclick="handleButtonSubmit(event)">
                                </div>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
		</div>
	</div>
</body>
<script src="../assets/js/account_update.js"></script>
</html>