<%@page import="utils.Constant"%>
<%@page import="java.util.Base64"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
   <html lang="en">
   <head>
      <meta charset="UTF-8">
      <meta name="viewport" content="width=device-width, initial-scale=1.0">
	<!--=============== THƯ VIỆN CRYPTO JS ===============-->
	<script src="https://cdnjs.cloudflare.com/ajax/libs/crypto-js/4.1.1/crypto-js.min.js"></script>
      <!--=============== REMIXICONS ===============-->
      <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/remixicon/3.5.0/remixicon.css">

      <!--=============== CSS ===============-->
      <link rel="stylesheet" type="text/css" href="../assets/css/styles.css">
      <link rel="stylesheet" type="text/css" href="../assets/css/login.css">
      <title>Đăng nhập</title>
   </head>
   <body>
   
   	<% 
		String userId = (session.getAttribute("userId") != null) ? session.getAttribute("userId")+"" : null;
   		if(userId!=null){
   			String url = "/mail/mail?my_action="
   					+ Base64.getEncoder().encodeToString(Constant.INCOMING_MAILBOX.getBytes());
   			RequestDispatcher rd = getServletContext().getRequestDispatcher(url);
			rd.forward(request, response);
		}
   		String email = (request.getAttribute("email")!=null) ? request.getAttribute("email") + "" : "";
   		String messageError = (request.getAttribute("error")!=null) ? request.getAttribute("error") + "" : "";
	%>

   	
      <div class="login">
         <img src="../assets/img/login-bg.jpg" alt="image" class="login__bg">

         <form class="login__form" name="login_form" action="authentication" method="POST">
            <h1 class="login__title">ĐĂNG NHẬP</h1>
			<div class="center-important"><%=messageError%></div>
			<input type="hidden" name="my_action" value="login">
            <div class="login__inputs">
                  <div class="login_input_item">
                     <div class="login__box">
                        <input type="email" id="email" name="email" value="<%=email%>" placeholder="Địa chỉ email" required pattern="^[a-z0-9](\.?[a-z0-9]){5,}@g(oogle)?mail\.com$" class="login__input">
                        <i class="ri-mail-fill"></i>
                     </div>
                     <span class="error" id="email_error"></span>
                  </div>
              
                  <div class="login_input_item">
                     <div class="login__box">
                        <input type="password" id="pwd" placeholder="Mật khẩu" required class="login__input">
                        <input type="hidden" name="password" value="">
                        <i class="ri-lock-2-fill" onclick="togglePassword()"></i>
                     </div>
                     <span class="error" id="password_error"></span>
                  </div>
            </div>

            <button type="submit" id="btn_submit" class="login__button">Đăng nhập</button>

            <div class="login__register">
               <a href="signup.jsp">Bạn chưa có tài khoản?</a>
               <br>
               <a href="/authentication/forget-password.jsp" class="login__forgot">Quên mật khẩu?</a>
            </div>
         </form>
      </div>
   </body>
   <script src="../assets/js/aesEncryption.js"></script>
   <script src="../assets/js/login.js"></script>
</html>