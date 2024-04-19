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
      <title>Xác nhận tài khoản</title>
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
   		String messageError = (request.getAttribute("error")!=null) ? request.getAttribute("error") + "" : "";
   		User user = (User) session.getAttribute("user");
   		session.setAttribute("user", user);
	%>

   	
      <div class="login">
         <img src="../assets/img/login-bg.jpg" alt="image" class="login__bg">

         <form class="login__form" style="width: 650px; text-align: center" name="login_form" action="authentication" method="POST">
            <h1 class="login__title">XÁC THỰC TÀI KHOẢN</h1>
            <div>Đã gửi mã xác nhận đến tài khoản <%= gmail %></div> <br>
            <div>Hãy nhập mã xác nhận để hoản tất đăng ký</div>
			<div class="center-important" id="errorText"></div>
			<input type="hidden" name="my_action" value="authorize">
			<input type="hidden" name="gmail" value="<%= gmail %>">
            <div class="login__inputs">
                  <div class="login_input_item">
                     <div class="login__box">
                        <input type="text" id="inputCode" name="inputCode" value="" placeholder="Mã xác nhận" required class="login__input">
                        <i class="ri-mail-fill"></i>
                     </div>
                     <span class="error" id="email_error"><%=messageError%></span>
                  </div>
              	<%-- 
                  <div class="login_input_item">
                     <div class="login__box">
                        <input type="password" id="pwd" placeholder="Mật khẩu" required class="login__input">
                        <input type="hidden" name="password" value="">
                        <i class="ri-lock-2-fill" onclick="togglePassword()"></i>
                     </div>
                     <span class="error" id="password_error"></span>
                  </div>--%>
            </div>
			
            <button type="submit" id="" class="login__button">Đăng ký</button>

            <div class="login__register">
               <a href="signup.jsp">Bạn chưa có tài khoản?</a>
               <br>
               <a href="#" class="login__forgot">Quên mật khẩu?</a>
            </div>
         </form>
      </div>
   </body>
   <script type="text/javascript">
   		function authorize(){
   			let xhr = new XMLHttpRequest();
		    xhr.open('POST', 'http://localhost:8080/authentication/authentication', true);
		    xhr.setRequestHeader('Content-Type', 'plain');
		    
		    xhr.onreadystatechange = function() {
		        if (xhr.readyState === XMLHttpRequest.DONE) {
		            if (xhr.status === 200) {
		                // Xử lý phản hồi từ server nếu cần
		                var error = document.getElementById('email_error');
		                error.innerHTML = xhr.responseText;
		            } else {
		                console.error('Lỗi khi cập nhật sidebar');
		            }
		        }
		    };
		    
		    xhr.send();
   		}
   
   </script>
   <script src="../assets/js/aesEncryption.js"></script>
   <script src="../assets/js/login.js"></script>
</html>