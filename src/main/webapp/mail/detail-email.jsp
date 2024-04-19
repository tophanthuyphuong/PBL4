<%@page import="org.apache.coyote.http11.upgrade.UpgradeServletInputStream"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Base64"%>
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
<link rel="stylesheet" href="../assets/css/detail_email.css">

<title>Chi tiết thư</title>
</head>
<body>
	<% Integer userID = (Integer) session.getAttribute("userId"); %>
	<%
		if(userID == null){
			response.sendRedirect("/authentication/authentication");
		}
	%>
	
	<%
		List<MailDetailDTO> list = request.getAttribute("listDetail") != null 
					? (List<MailDetailDTO>)request.getAttribute("listDetail") : new ArrayList<MailDetailDTO>();
		
		int currentPage = Integer.parseInt((String) ((request.getAttribute("currentPage") == null)? "1" : request.getAttribute("currentPage")));
		int maxPage = list.size()/5 + ((list.size()%5 == 0)? 0 : 1);
		String mailID = (request.getAttribute("mailID") == null)? "" : ((String) request.getAttribute("mailID"));
	%>
	
	<%!	private StringBuilder getListReceiver(List<UserDetailDTO> listReceiver, StringBuilder listReceiverName, int userID, boolean isVisible) {
			StringBuilder res = new StringBuilder();
			if (isVisible) {
				for (UserDetailDTO mail :listReceiver) {
					res.append(mail.getEmail()).append(", ");
					if (listReceiverName.indexOf(mail.getUsername() + ",") == -1) {
						listReceiverName.append(mail.getUsername()).append(", ");
					}
				}
			} else {
				for (UserDetailDTO mail :listReceiver) {
					if (mail.getUserID() == userID)
						res.append(mail.getEmail()).append(", ");
					if (listReceiverName.indexOf(mail.getUsername() + ",") == -1) {
						listReceiverName.append(mail.getUsername()).append(", ");
					}
				}
			}
			
			if (res.length() > 2)
				res.delete(res.length() - 2, res.length() - 1);
			else
				res.append("(không có)");
			return res;
		}%>
	<input type="hidden" id="mailID" value="<%= mailID%>">
	<div class="row home_page">
		<div class="clear_fixed"></div>
		<jsp:include page="../layout-website/sidebar.jsp"></jsp:include>

		<div class="container">
			<jsp:include page="../layout-website/header.jsp"></jsp:include>

			<div class="content">
				<div class="content_header">
					<div class="content_header-left">
						<% String url = "/mail/mail?my_action="; %>
						<a href="<%=(url + Base64.getEncoder().encodeToString(Constant.INCOMING_MAILBOX.getBytes())) %>"
						title="Quay lại"
							class="content_header-item" id=""> 
							<i class="fa-solid fa-arrow-left"></i>
						</a> 
						<a href="#" title="Đánh dấu là spam" name="check-status"  value='check-spam'
							class="content_header-item"> 
							<i class="fa-solid fa-circle-exclamation"></i>
						</a> 
						<a href="#" title="Xóa"  name="check-status"  value='delete-mail'
							class="content_header-item" id=""> 
							<i class="fa-regular fa-trash-can"></i>
						</a> 
						<a href="#" title="Đánh dấu là đã đọc" name="check-status" value = 'check-read'
							class="content_header-item" id=""> 
							<i class="fa-regular fa-envelope-open"></i>
						</a>
						<a href="#" title="Đánh dấu là chưa đọc" class="content_header-item" 
							value='check-unread' name="check-status"> 
							<i class="fa-solid fa-envelope-circle-check"></i>
						</a>
					</div>

					<div class="content_header-right">
						<span><%= (list.size() == 0)? 0 : currentPage*5-4 %>-<%= (list.size() > 5 && currentPage*5 < list.size())? currentPage*5 : list.size() %> trong số <%=list.size() %> tin nhắn</span> 
						<a href="/mail/mail?mailId=<%= mailID %>&my_action=<%=Base64.getEncoder().encodeToString(Constant.DETAIL.getBytes()) %>&currentPage=<%=(currentPage == 1)? 1 : currentPage-1  %>"
							title="Trang trước" class="content_header-item"> <i
							class="fa-solid fa-angle-left"></i>
						</a> <a href="/mail/mail?mailId=<%= mailID %>&my_action=<%=Base64.getEncoder().encodeToString(Constant.DETAIL.getBytes()) %>&currentPage=<%=(currentPage == maxPage)? maxPage : currentPage+1 %>" 
							title="Trang sau" class="content_header-item"> <i
							class="fa-solid fa-angle-right"></i>
						</a>
					</div>
				</div>

				<div class="mails_list">
					<div class="mail_title" >
						<span class="mr-2" >Tiêu đề:</span>
						<span id="mail-title" class="invisible font-italic"><%=list.get(0).getMailContent().getSubject()%></span>
					</div>

					<%
					//for (MailDetailDTO item : list) {
						for(int j = currentPage*5-5; j < currentPage*5; j++){
							if(j >= list.size()) break;
							MailDetailDTO item = list.get(j);
							
					%>
					<input type='hidden' name='mailID' value='<%= item.getMailID()%>'>
					<div class="mail_item">
						<div class="mail_info">
							<div class="content_header-left">
								<img class="mail_info_avt" src="../assets/img/avt.png">
								<div class="mail_info_tofrom">
									<div class="mail_from">
										<span class="mail_user_name"><%=item.getSender().getUsername()%></span>
										<span class="mail_user_account">(<%=item.getSender().getEmail()%>)
										</span>
									</div>
									<% 
											StringBuilder listNameReceiver = new StringBuilder();
											StringBuilder listTo = getListReceiver(item.getListTo(), listNameReceiver, userID, true);
											StringBuilder listCc = getListReceiver(item.getListCc(), listNameReceiver, userID, true);
											boolean isBccVisible = item.getSender().getUserID() == userID;
											StringBuilder listBcc = getListReceiver(item.getListBcc(), listNameReceiver, userID, isBccVisible);
											
											if (listNameReceiver.length() > 2) listNameReceiver.delete(listNameReceiver.length() - 2, listNameReceiver.length() - 1); 
											else listNameReceiver.append("(không có)");
											%>
									<div class="mail_to">
										đến 
										<% if (item.isInbox()) { %>
											tôi
										<% } else { %>
											<%=listNameReceiver %>
										<% } %>
										<button class="button_list_users">
											<i class="fa-solid fa-caret-down"></i>
										</button>
										<div class="list_users">
											<div class="row">
												<label class="col-3">từ:</label>
												<div class="col-8">
													<span class="mail_user_name"><%=item.getSender().getUsername() %></span> <span
														class="mail_user_account">(<%=item.getSender().getEmail() %>)</span>
												</div>
											</div>

											<div class="row">
												<label class="col-3">đến:</label>
												<div class="col-8">
													<span><%=listTo %></span>
												</div>
											</div>

											<div class="row">
												<label class="col-3">cc:</label>
												<div class="col-8">
													<span><%=listCc %></span>
												</div>
											</div>

											<div class="row">
												<label class="col-3">bcc:</label>
												<div class="col-8">
													<span><%=listBcc %></span>
												</div>
											</div>

											<div class="row">
												<label class="col-3">ngày:</label> <span class="col-8">
												<%=item.getMailContent().getTime() %></span>
											</div>

											<div class="row">
												<label class="col-3">tiêu đề:</label> 
												<span class="col-8 mail-title-div"><%=item.getMailContent().getSubject() %></span>
											</div>
										</div>
									</div>

								</div>
							</div>

							<div class="content_header-right">
								<span class="mail_info_time"><%=item.getMailContent().getTime() %>
							</div>
						</div>
						<% if (item.getMailContent().getSubject().equals(Constant.SUBJECT_DELIVERY_FAILURE)) { %>
						<div class="failure-error row">
							<img class="failure-error-img" src="../assets/img/question.png"
								alt="Lỗi" />
							<div class="failure-error-container">
								<h2 class="failure-mess">Không tìm thấy địa chỉ</h2>
								<p>
									Thư của bạn không được gửi đến <span class="error-name"><%=item.getMailContent().getMailContent() %></span>
									vì không thể tìm thấy địa chỉ hoặc địa chỉ không thể nhận thư
								</p>
							</div>
						</div>
						<% } else { 
							String mailContent = item.getMailContent().getMailContent();
							mailContent = (mailContent.equals("")) ? "(Không có nội dung)" : mailContent;
						%>
						<div class="mail_content invisible"><%=mailContent%></div>
						
							<%if(item.getAttachmentName() != null){ %>
							<div class="mail_attachment" title="Tải về">
							<i class="fa-regular fa-file fa-xl"></i>
							<a href="<%= item.getMailContent().getAttachment()%>">
								
								<div class='mail_fileName'><%= item.getAttachmentName() %></div>
							</a>
							</div>
							<%} %>
						
						<div class="mail_reply">
							<a class="mail_reply_item reply_one" 
							   href='/mail/mail?my_action=<%=Base64.getEncoder().encodeToString(Constant.REPLY.getBytes()) %>&mailID=<%= item.getMailID()%>'>
								<i class="fa-solid fa-reply"></i> Trả lời
							</a>

							<a class="mail_reply_item reply_all"
							   href='/mail/mail?my_action=<%=Base64.getEncoder().encodeToString(Constant.REPLY_ALL.getBytes()) %>&mailID=<%= item.getMailID()%>'>
								<i class="fa-solid fa-reply-all"></i> <span>Trả lời tất cả</span>
							</a>
						</div>
						<%
						}
						%>
					</div>
					<%
					}
					%>
				</div>
			</div>
			<jsp:include page="../layout-website/footer.jsp"></jsp:include>
		</div>
	</div>
</body>
<script src="../assets/js/detail-email.js"></script>
<script src="../assets/js/aesEncryption.js"></script>
<script type="text/javascript">
var webSocket;
/* window.addEventListener("load", function(event) { */
	if (webSocket !== undefined && webSocket.readyState !== WebSocket.CLOSED) {
		writeResponse("WebSocket is already opened.");
	} else {
		// Create a new instance of the websocket
		const url = window.location.href;
		const urlObject = new URL(url);
		const hostname = urlObject.hostname;
		webSocket = new WebSocket("ws://" + hostname + "/websocketendpoint");
		webSocket.onopen = function(event) {
			console.log("Open websocket");
			var sessionId = '<%=session.getId()%>'; // Lấy session ID từ cookie hoặc từ trang web
			webSocket.send(sessionId);
		};

		webSocket.onmessage = function(event) {
			console.log(event.data);
			const newMail = JSON.parse(event.data);
			// Gọi hàm để tạo khung email mới khi cần
			console.log(newMail);
			
			addCountUnread();
			createNewEmail(newMail.rootID, <%=list.get(0).getReplyTo()%>);
		};

		webSocket.onclose = function(event) {
			console.log("Connection closed");
		};			
	}
/* }); */

window.addEventListener("beforeunload", function(event) {
	if (webSocket) {
		webSocket.close();
	}
});
</script>
</html>