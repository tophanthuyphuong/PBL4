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
    <link rel="stylesheet" href="../assets/css/styles.css">
    <link rel="stylesheet" href="../assets/css/login.css">
    <link rel="stylesheet" href="../assets/css/signup.css">

    <title>Đăng ký</title>
</head>

<body>
	<%
		String errorMessage = request.getAttribute("errorMessage")+"";
		errorMessage = (errorMessage.equals("null")) ? "" : errorMessage;
	
		String lastname = request.getAttribute("lastname")+"";
		lastname = (lastname.equals("null")) ? "" : lastname;
		
		String firstname= request.getAttribute("firstname")+"";	
		firstname = (firstname.equals("null")) ? "" : firstname;
		
		String email= request.getAttribute("email")+"";	
		email = (email.equals("null")) ? "" : email;
		
		String birthday= request.getAttribute("birthday")+"";	
		birthday = (birthday.equals("null")) ? "" : birthday;
		
		String phonenumber= request.getAttribute("phonenumber")+"";	
		phonenumber = (phonenumber.equals("null")) ? "" : phonenumber;
		
		String gmail= request.getAttribute("gmail")+"";	
		gmail = (gmail.equals("null")) ? "" : gmail;
	%>
    <div class="login">
        <img src="../assets/img/login-bg.jpg" alt="image" class="login__bg">

        <form name="signup_form" action="authentication" method="post" class="login__form">
        	<input type="hidden" name="my_action" value="signup">
            <h1 class="login__title">ĐĂNG KÝ</h1>
            <div class="login__inputs">
                <span class="error" id="format_pwd_error"><%=errorMessage %></span>

                <div class="login__box-row">
                    <div class="login_input_item">
                        <div class="login__box">
                            <input type="text" name="lastname" value="<%=lastname %>" id="last_name" class="login__input" placeholder="Họ *" required>
                        </div>
                        <span class="error" id="last_name_error"></span>
                    </div>
                    
                    <div class="login_input_item">
                        <div class="login__box">
                            <input type="text" name="firstname" value="<%=firstname %>" id="first_name" class="login__input" placeholder="Tên *" required >
                        </div>
                        <span class="error" id="first_name_error"></span>
                    </div>
                </div>

                <div class="login_input_item">
                    <div class="login__box">
                        <input type="email" name="email" value="<%=email %>" id="email" class="login__input" placeholder="Tên người dùng: abc@meowmail.vn *" required>
                        <i class="ri-mail-fill"></i>
                    </div>
                    <span class="error" id="email_error"></span>
                </div>
                
                <div class="login__box-row">
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
                            <i class="ri-key-fill" onclick="toggleReenterPassword()"></i>
                        </div>
                        <span class="error" id="confirm_pwd_error"></span>
                    </div>
                </div>
                
                <div class="login_input_item">
                    <div class="login__box">
                        <input type="text" name="birthday" value="<%=birthday %>" id="birthday" class="login__input" placeholder="Ngày sinh *">
                    </div>
                    <span class="error" id="birthday_error"></span>
                </div>

                <div class="login_input_item">
                    <div class="login__box">
                        <input type="tel" name="phonenumber" value="<%=phonenumber %>" id="phone_number" class="login__input" placeholder="Số điện thoại *" required >
                        <i class="ri-phone-fill"></i>
                    </div>
                    <span class="error" id="phone_number_error"></span>
                </div>
                
                <div class="login_input_item">
                    <div class="login__box">
                        <input type="email" name="gmail" value="<%=gmail %>" id="gmail" class="login__input" placeholder="Gmail *" required >
                        <i class="ri-mail-fill"></i>
                    </div>
                    <span class="error" id="gmail_error"></span>
                </div>
            </div>

            <button type="submit" id="btn_submit" class="login__button">Đăng ký</button>

            <div class="login__register">
                <a href="login.jsp">Bạn đã có tài khoản?</a>
            </div>
        </form>
    </div>
</body>
<script src="../assets/js/aesEncryption.js"></script>
<script src="../assets/js/login.js"></script>
<script src="../assets/js/signup.js"></script>
</html>