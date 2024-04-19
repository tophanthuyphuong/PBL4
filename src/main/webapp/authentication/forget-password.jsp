<%@page import="dto.User"%>
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
      <title>Quên mật khẩu</title>
   </head>
   <body>
   
   	<% 
		/*String userId = (session.getAttribute("userId") != null) ? session.getAttribute("userId")+"" : null;
   		if(userId!=null){
   			String url = "/mail/mail?my_action="
   					+ Base64.getEncoder().encodeToString(Constant.INCOMING_MAILBOX.getBytes());
   			RequestDispatcher rd = getServletContext().getRequestDispatcher(url);
			rd.forward(request, response);
		}*/
   		String gmail = (request.getAttribute("gmail")!=null) ? request.getAttribute("gmail") + "" : "";
		String email = (request.getAttribute("email")!=null) ? request.getAttribute("email") + "" : "";
   		String messageError = (request.getAttribute("errorMessage")!=null) ? request.getAttribute("errorMessage") + "" : "";
   		
	%>

   	
      <div class="login">
         <img src="../assets/img/login-bg.jpg" alt="image" class="login__bg">

         <form class="login__form" style="width: 650px; text-align: center" name="login_form" action="authentication" method="POST">
            <h1 class="login__title">QUÊN MẬT KHẨU</h1>
			<input type="hidden" name="my_action" value="forget-password">
			<%if(request.getAttribute("gmailInput") != null) {%>
			<div style="padding-bottom: 10px">Đã gửi mã xác nhận đến tài khoản <%= gmail %></div>
            <%} %>
            <div class="center-important" id="errorText"><%=messageError%></div>
            <div class="login__inputs">
                  <%if(request.getAttribute("gmailInput") == null){ %>
                  <div class="login_input_item">
                     <div class="login__box">
                        <input type="text" id="email" name="email" value="<%= email %>" placeholder="Email *"  pattern="^\w+@meowmail.vn$" required class="login__input">
                        <i class="ri-mail-fill"></i>
                     </div>
                  </div>
                  <div class="login_input_item">
                     <div class="login__box">
                        <input type="text" id="gmail" name="gmail" value="<%= gmail%>" placeholder="Gmail khôi phục *"  pattern="^[\w.+\-]+@gmail\.com$" required class="login__input">
                        <i class="ri-mail-fill"></i>
                     </div>
                  </div>
                  </div>
			
            <button type="submit" id="" class="login__button">Xác thực</button>
                  <%} else {%>
                  <input type="hidden" name="email" value="<%= email%>">
                  <input type="hidden" name="gmail" value="<%= gmail%>">
                  <div class="login_input_item">
                        <div class="login__box">
                            <input type="password" id="pwd" class="login__input" placeholder="Mật khẩu *" required>
                            <input type="hidden" name="password" value="">
                            <i class="ri-lock-2-fill" onclick="togglePassword()"></i>
                        </div>
                        <span class="error" id="password_error"></span>
                    </div>
                    
                    <div class="login_input_item">
                        <div class="login__box">
                            <input type="password" id="confirm_pwd" class="login__input" placeholder="Xác thực mật khẩu *" required>
                            <i class="ri-key-fill" onclick="togglePassword()"></i>
                        </div>
                        <span class="error" id="confirm_pwd_error"></span>
                    </div>
                    
                    <div class="login_input_item">
                        <div class="login__box">
                            <input type="text" id="confirm_passcode" class="login__input" name="inputCode" placeholder="Mã xác nhận *" required>
                            <i class="ri-key-fill"></i>
                        </div>
                        <span class="error" id=""></span>
                    </div>
                    </div>
			
            <button type="submit" id="btn_submit" class="login__button">Xác thực</button>
                  <%} %>
            

            <div class="login__register">
               <a href="signup.jsp">Bạn chưa có tài khoản?</a>
            </div>
         </form>
      </div>
   </body>
   <script src="../assets/js/aesEncryption.js"></script>
   <script src="../assets/js/forget-password.js"></script>
</html>