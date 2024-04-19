<%@page import="java.util.Base64"%>
<%@page import="utils.Constant"%>
<%@page import="dto.UserDetailDTO"%>
<%@page import="java.util.List"%>
<%@page import="dto.MailDetailDTO"%>
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
<link rel="stylesheet" href="../assets/css/homepage.css">
<link rel="stylesheet" href="../assets/css/inbox.css">
<title>Tạo thư mới</title>
</head>
<body>
	<% if(session.getAttribute("userId") == null){ 
			response.sendRedirect("/authentication/authentication");
		} 
	%>

	<div class="row home_page">
		<div class="clear_fixed"></div>

		<jsp:include page="../layout-website/sidebar.jsp"></jsp:include>

		<div class="container">
			<jsp:include page="../layout-website/header.jsp"></jsp:include>
			<% MailDetailDTO mailDetail = (MailDetailDTO) request.getAttribute("mailDetail"); %>
			<div class="content">
				<div class="new_email_container">
					<h2 class="content_header new_email_header"><%= (mailDetail == null)? "Thư mới" : "Trả lời thư" %></h2>

					<form name="new_email_form" class="new_email_content" action="mail" method="post" accept-charset="UTF-8"  enctype="multipart/form-data">
						<% if(mailDetail == null){ %>
							<input type="hidden" name="my_action" value="<%=Base64.getEncoder().encodeToString(Constant.INBOX.getBytes()) %>"/>
						<% } else { %>
							<input type="hidden" name="my_action" value="<%=Base64.getEncoder().encodeToString(Constant.INBOX.getBytes()) %>"/>
							<input type="hidden" name="rootMailID" value="<%= mailDetail.getMailID() %>"/>
						<% } %>
						<div class="new_email_to new_email_row">
							<div class="col-1 label">Đến</div>
							<% if(mailDetail != null){
								String email = "";
								String senderEmail = "";
								if(mailDetail.getSender() != null){
									senderEmail = mailDetail.getSender().getEmail();
									email += senderEmail + " ";
								} 
								
								if (mailDetail.getListTo() != null){
									List<UserDetailDTO> listTo = mailDetail.getListTo();
									for(UserDetailDTO user : listTo) {
										if (!senderEmail.equals(user.getEmail())) 
											email += user.getEmail() + " ";
									} 
								}
							%>
								<input class="col-11" name="toMail" id="to" value='<%=email %>'>
							<%} else {%>
								<input class="col-11" name="toMail" id="to" placeholder="Người nhận: email1@meowmail.vn email2@meowmail.vn">
							<% } %>
						</div>

						<div class="new_email_to new_email_row">
							<div class="col-1 label">CC</div>
							<input class="col-11" name="ccMail" id="cc" placeholder="">
						</div>

						<div class="new_email_to new_email_row">
							<div class="col-1 label">BCC</div>
							<input class="col-11" name="bccMail" id="bcc" placeholder="">
						</div>
						<div class="new_email_title new_email_row">
							<div class="col-1 label">Tiêu đề</div>
							<%String subject = ""; %>
							<% if(mailDetail != null){
								subject = mailDetail.getMailContent().getSubject();
							%>
							<input class="col-11" placeholder="" id="subject" value='<%=subject %>'>
							<% } else {%>
							<input class="col-11" placeholder="" id="subject">
							<% } %>
						</div>

						<textarea id="content" class="new_email_row" placeholder="Nội dung thư"></textarea>
							
						<input type="hidden" id="encrypted_subject" name="subjectMail" value="">
						<input type="hidden" id="encrypted_content" name="contentMail" value="">
						
						<div class="new_email_button new_email_row">
							<input type="submit" value="Gửi thư" name="btnSubmit"
								id="submit_btn" class="input_btn"> 
							
							<input type="file"
								name="fileToUpload" id="fileToUpload" class="input_btn">
						</div>
					</form>
				</div>
			</div>

			<jsp:include page="../layout-website/footer.jsp"></jsp:include>
		</div>
	</div>
</body>
<script src="../assets/js/aesEncryption.js"></script>
<script src="../assets/js/inbox.js"></script>
</html>