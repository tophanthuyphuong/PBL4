<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.io.IOException"%>
<%@page import="java.io.DataInputStream"%>
<%@page import="clientsocket.ClientSocket"%>
<%@page import="java.net.Socket"%>
<%@page import="java.util.Base64"%>
<%@page import="utils.Constant"%>
<%@page import="enums.Status"%>
<%@page import="dto.MailBoxDTO"%>
<%@page import="java.util.ArrayList"%>
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
<!--=============== REMIXICONS ===============-->
      <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/remixicon/3.5.0/remixicon.css">
<!--=============== BOOTSTRAP & CSS ===============-->
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap@4.3.1/dist/css/bootstrap.min.css"
	integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T"
	crossorigin="anonymous">
<link rel="stylesheet" href="../assets/css/styles.css">
<link rel="stylesheet" href="../assets/css/homepage.css">
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>

<% 	
	String encodeAction = request.getParameter(Constant.MY_ACTION); 
	String action = ((encodeAction != null)) ? new String(Base64.getDecoder().decode(encodeAction)) : null;
%>
<% if (action == null || action.equals(Constant.INCOMING_MAILBOX)) { %>
	<title>Hộp thư đến</title>
<% } else if (action.equals(Constant.OUTBOX)) { %>
	<title>Thư đã gửi</title>
<% } else if (action.equals(Constant.ALL_MAILBOX)) { %>
	<title>Tất cả thư</title>
<% } else if (action.equals(Constant.DELETED_MAILBOX)) { %>
	<title>Thùng rác</title>
<% } else if (action.equals(Constant.SPAM_MAILBOX)) { %>
	<title>Spam</title>
<% } %>

</head>
<body>
	<% if(session.getAttribute("userId") == null){ 
		response.sendRedirect("/authentication/authentication");
	} %>
	
	<%--<% if (request.getAttribute("isProcessed") == null) { 
		   		String url = "/mail/mail?my_action=" + Base64.getEncoder().encodeToString(Constant.INCOMING_MAILBOX.getBytes());
		--%>
    <%--  		<jsp:forward page="<%=url %>"></jsp:forward>
	
	<% } %>	--%>
		
	<%
		String _email = (String)session.getAttribute("email");
		ArrayList<MailBoxDTO> list = null;
		Object object = request.getAttribute("listmail");
		if (object != null) {
			list = (ArrayList<MailBoxDTO>)object;
		} else { 
			list = new ArrayList<MailBoxDTO>();
		} 
		int currentPage = Integer.parseInt((String) ((request.getAttribute("currentPage") == null)? "1" : request.getAttribute("currentPage")));
	%>
	
	<%!
	public String formatDateTime(String date) {
	    String formatedDate = null;
	    try {
	        // Format 
	        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
	        Date currentDate = new Date();
	        Date sendedDate = dateTimeFormat.parse(date);
	        
	        // Sử dụng equals() để so sánh giá trị ngày tháng
	        if (currentDate.getDate() == sendedDate.getDate() && currentDate.getMonth() == sendedDate.getMonth() 
	        	&& currentDate.getYear() == sendedDate.getYear()) {
	            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
	            formatedDate = timeFormat.format(sendedDate);
	        } else {
	        	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	            formatedDate = dateFormat.format(sendedDate);
	        }
	    } catch (Exception e) {  
	        e.printStackTrace();
	    }
	    return formatedDate;
	}

	%>
	<div class="row home_page">
		<div class="clear_fixed"></div>

		<jsp:include page="../layout-website/sidebar.jsp"></jsp:include>

		<div class="container">
			<jsp:include page="../layout-website/header.jsp"></jsp:include>

		<div class="content">
			<form id="check-status-form" action="/mail/mail" method="POST">
				<div class="content_header">
					<div class="content_header-left">
						<input type="checkbox" id="checked_all"
							class="content_header-item"> <a href="" title="Làm mới"
							class="content_header-item" id="reload"> <i
							class="fa-solid fa-arrow-rotate-right "></i>
						</a> 
						<% if (!action.equals(Constant.DELETED_MAILBOX)) { %>
						<a href="#"
							title="Xóa" class="content_header-item" id="delete-icon" value='delete-mail'
							name="check-status" style="display: none;"> 
							<i class="ri-delete-bin-6-line"></i>
						</a> 
						<% } else { %>
						<a href="#"
							title="Bỏ xóa" class="content_header-item" id="delete-icon" value='undelete-mail'
							name="check-status" style="display: none;"> 
							<i class="ri-delete-bin-2-line"></i>
						</a> 	
						<% } %>
						<% if (!action.equals(Constant.SPAM_MAILBOX)) { %>
						<a href="#"
							title="Đánh dấu spam" class="content_header-item" id="spam-icon" value='check-spam'
							name="check-status" style="display: none;"> 
							<i class="ri-spam-2-line"></i>
						</a> 
						<% } else { %>
						<a href="#"
							title="Bỏ spam" class="content_header-item" id="spam-icon" value='check-unspam'
							name="check-status" style="display: none;"> 
							<i>Không phải là spam</i>
						</a> 
						<% } %>
						<a href="#" title="Đánh dấu là đã đọc" class="content_header-item" value = 'check-read'
							id="read-icon" name="check-status" style="display: none;"> 
							<i class="ri-mail-open-line"></i>
						</a> 
						<a href="#"
							title="Đánh dấu là chưa đọc" class="content_header-item" value = 'check-unread'
							id="unread-icon" name="check-status" style="display: none;">
							<i class="ri-mail-unread-line"></i>
						</a>
						<% 
							String my_action = (String) request.getAttribute(Constant.MY_ACTION);
							int maxPage = list.size()/5 + ((list.size()%5 == 0)? 0 : 1);
							if(maxPage == 0){
								currentPage = 1;
								maxPage = 1;
							}
						%>
						<input type="hidden" name="my_new_action" id="my_new_action">
						<input type="hidden" name="my_action" id="my_action"
							value="<%=Base64.getEncoder().encodeToString(my_action.getBytes()) %>">
					</div>
					
					<div class="content_header-right">
					<input type='hidden' id='maxPage' name='maxPage' value='<%=list.size()/5 + ((list.size()%5 == 0)? 0 : 1)%>'>
						<span><%= (list.size() == 0)? 0 : currentPage*5-4 %>-<span id="totalInbox"><%= (list.size() > 5 && currentPage*5 < list.size())? currentPage*5 : list.size() %></span> 
							trong số <span id="totalBox"><%=list.size() %></span> tin nhắn</span> 
						<a
							title="Trang trước" class="content_header-item" 
							onclick="changePageLink(<%=(currentPage == 1)? 1 : currentPage-1%>, event)"> 
							<i class="fa-solid fa-angle-left"></i>
						</a> 
						<a title="Trang sau" class="content_header-item"
							onclick="changePageLink(<%=(currentPage == maxPage)? maxPage : currentPage+1%>, event)"> 
							<i class="fa-solid fa-angle-right"></i>
						</a>
					</div>
				</div>
				
				<div class="main_content">
					<div class="table_head">
						<div></div>

						<div>
							<i class="fa-regular fa-user"></i> Người gửi
						</div>
						<div></div>
						<div>
							<i class="fa-solid fa-list"></i> Tiêu đề
						</div>
						<div>
							<i class="fa-regular fa-comments"></i> Nội dung
						</div>
						<div>
							<i class="fa-regular fa-clock"></i> Thời gian
						</div>
					</div>
					<% if (list.size() == 0 ) {%>
						<h2 class="no-mail">Bạn không có thư nào</h2>
					<% } %>
					
					<div id="email-container">
					<%
					//for (MailBoxDTO i : list) {
					for(int j = currentPage*5-5; j < currentPage*5; j++){
						if(j >= list.size()) break;
						MailBoxDTO i = list.get(j);
						String urlDetail = "" + i.getMailID();
						String encodedEmail = Base64.getEncoder().encodeToString(urlDetail.getBytes());
						String encodedAction = Base64.getEncoder().encodeToString(Constant.DETAIL.getBytes());
						String url = "/mail/mail?my_action=" + encodedAction + "&mailId=" + encodedEmail;
						String statusStr = Status.getStatus(i.getStatus());
						if (statusStr.equals("UNREAD")) { %>
						<a href="<%=url%>" class="email_item active" id="mail-<%=i.getRootID()%>"> 
						<% } else { %> 
						<a href="<%=url%>" class="email_item" id="mail-<%=i.getRootID()%>"> 
						<% } %> 
							<input type="checkbox" class="content_header-item" 
								name="checkBox" value="<%=i.getMailID()%>"> 
							<div class="email_item_name"><%=i.getSendName()%>
								<% if (i.getCount() != 0) { %>
								<span class="email_item_count">(<%=i.getCount()%>)</span>
								<% } else { %>
								 <span class="email_item_count"></span>
								<% } %>
							</div>  
 					
 							<% if (!i.getSubject().equals("")) { %> 
 								<span class="email_item_title invisible"><%=i.getSubject()%></span> 
 							<% } %>
 							<% if (!i.getContent().equals("")) { %> 
 								<span class="email_item_content invisible"><%=i.getContent()%></span> 
 							<% } %> 
 							<span class="email_item_time"><%=formatDateTime(i.getTime())%></span>
						</a> 
					<% } %>
					</div>
				</div>
			</form>
		</div>
		<jsp:include page="../layout-website/footer.jsp"></jsp:include>
	</div>
	</div>
</body>

<script src="../assets/js/mail-box.js"></script>
<script src="../assets/js/aesEncryption.js"></script>
<script type="text/javascript">
	// Luu key va iv vao session
	window.onload = function() {
		if (!localStorage.getItem('private_key')) {
			// Nếu chưa, đây là lần truy cập đầu tiên
			let key = '<%=session.getAttribute(Constant.KEY)%>';
			let iv = '<%=session.getAttribute(Constant.IV)%>';
			if (key && iv) 
				initSessionStorage(key, iv);
		}
		handleMailBox();
	}
	
	
	
</script>
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
				
				<% if (action == null || action.equals(Constant.INCOMING_MAILBOX)) { %>
					createNewEmail(newMail);
				<% } else { %>
					addCountUnread();
				<% } %>
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