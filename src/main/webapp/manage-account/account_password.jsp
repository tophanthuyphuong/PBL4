<%@page import="utils.Constant"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<!--=============== THƯ VIỆN CRYPTO JS ===============-->
<script src="https://cdnjs.cloudflare.com/ajax/libs/crypto-js/4.1.1/crypto-js.min.js"></script>
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
<title>Thay đổi mật khẩu</title>
</head>
<body>
	<%
		//if(session.getAttribute("userId") == null){
			//response.sendRedirect("/authentication/authentication");
		//}
	%>
	<div class="row account">
		<div class="clear_fixed"></div>
		<jsp:include page="../layout-website/sidebar_account.jsp"></jsp:include>

		<div class="container">
			<jsp:include page="../layout-website/header.jsp"></jsp:include>

			<div class="content_container change_password">
                <h1 class="title">Thay đổi mật khẩu</h1>
                <div class="content">
                    <form name="update_pass" method="post" action="/authentication/authentication?my_action=new-password">
                        <div class="card">
                            <div class="card-body">
                                <div class="row mb-2 d-flex align-items-center">
                                    <div class="col-sm-4">
                                        <h4 class="label" class="mb-0">Mật khẩu cũ</h4>
                                    </div>
                                    <div class="col-sm-8 text-secondary position-relative">
                                        <input type="password" id="old_password" class="form-control input_txt">
                                        <input type="hidden" name="old_password" value="">
                                		<i class="fa fa-eye position-absolute" onclick="togglePassword()"></i>
                                    </div>
                                </div>
                                <div class="row mb-4 d-flex align-items-center">
                                    <div class="col-sm-4"></div>
                                    <div class="col-sm-8 text-center">
                                        <h3 class="text-danger font-italic" id="old_error_message"><%= (request.getAttribute("error") == null)? "" : (String) request.getAttribute("error") %></h3>
                                    </div>
                                </div>
                                <div class="row mb-2 d-flex align-items-center">
                                    <div class="col-sm-4">
                                        <h4 class="label" class="mb-0">Mật khẩu mới</h4>
                                    </div>
                                    <div class="col-sm-8 text-secondary">
                                        <input type="password" id="new_password" class="form-control input_txt">
                                        <input type="hidden" name="new_password" value="">
                                        <i class="fa fa-eye position-absolute" onclick="togglePassword()"></i>
                                    </div>
                                </div>
                                <div class="row mb-4 d-flex align-items-center">
                                    <div class="col-sm-4"></div>
                                    <div class="col-sm-8 text-center">
                                        <h3 class="text-danger font-italic" id="new_error_message"></h3>
                                    </div>
                                </div>
                                <div class="row mb-2 d-flex align-items-center">
                                    <div class="col-sm-4">
                                        <h4 class="label" class="mb-0">Nhập lại mật khẩu</h4>
                                    </div>
                                    <div class="col-sm-8 text-secondary">
                                        <input type="password" name="reenter_password" id="reenter_password" class="form-control input_txt">
                                        <i class="fa fa-eye position-absolute" onclick="togglePassword()"></i>
                                    </div>
                                </div>
                                <div class="row mb-4 d-flex align-items-center">
                                    <div class="col-sm-4"></div>
                                    <div class="col-sm-8 text-center">
                                        <h3 class="text-danger font-italic" id="reenter_error_message"></h3>
                                    </div>
                                </div>
                                <div class="row mb-4 pr-4 justify-content-end">
                                        <a href="../authentication/forget-password.jsp" class="forget_password">Quên mật khẩu</a>
                                </div>
                                <div class="row">
                                        <input type="submit" id="btn_submit" class="btn mt-0 px-4 btn_save" value="Cập nhật" 
                                        onclick="handleButtonSubmit(event)">
                                </div>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
		</div>
	</div>
</body>
<script src="../assets/js/aesEncryption.js"></script>
<script src="../assets/js/password_setting.js"></script>
</html>