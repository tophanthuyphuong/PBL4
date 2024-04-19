<%@page import="java.util.Base64"%>
<%@page import="utils.Constant"%>
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
<link rel="stylesheet" href="../assets/css/layout_home.css">
</head>
<body>
	<% 
		String input = (request.getAttribute("search_input") != null)? (String)request.getAttribute("search_input") : "";
		String startDate = (request.getAttribute("startDate") != null)? (String)request.getAttribute("startDate") : "";
		String endDate = (request.getAttribute("endDate") != null)? (String)request.getAttribute("endDate") : "";
	%>
	<header class="header">
		<form class="search_filter_form" action="../mail/mail" method="get">
			<div class="input-group search_form">
				<button class="btn filter-button" type="button">
					<i class="fa-solid fa-filter"></i>
				</button>
				<%--
					String my_action = (String) request.getAttribute(Constant.MY_ACTION);
				--%>
				<input type="hidden" name="my_action" id="my_action"
					   value="<%= Base64.getEncoder().encodeToString(Constant.SEARCH_AND_FILTER.getBytes())%><%--=Base64.getEncoder().encodeToString(my_action.getBytes()) --%>">
				<input type="search" class="form-control search_input"
					placeholder="Nhập tin nhắn cần tìm" aria-label="Search" 
					aria-describedby="search-addon" id='search_input' name='search_input' value='<%= input%>'/>
				<button type="submit" class="btn btn-outline-primary search_btn" <%-- %>onclick='search()'--%>>
					<i class="fa-solid fa-magnifying-glass"></i>
				</button>
			</div>

			<div class="filter-container">
				<div class="row">
					<label class="filter-label col-3">Thể loại thư:</label>
					<div class="col-9">
						<select name="search_type" id="category_filter">
							<option value="<%= Constant.MAILBOX_REQUEST%>">Hộp thư đến</option>
							<option value="<%= Constant.SEND_REQUEST%>">Hộp thư đã gửi</option>
							<option value="<%= Constant.ALL_REQUEST%>">Tất cả thư</option>
						</select>
					</div>
				</div>

				<div class="row">
					<label class="filter-label col-3">Khoảng thời gian:</label>
					<div class="col-9">
						<input name="startDate" type="date" id="startDate" value='<%= startDate %>'>
					</div>
				</div>

				<div class="row">
					<label class="filter-label col-3">Đến:</label> 
					<div class="col-9">
						<input name="endDate" type="date" id="endDate" value='<%= endDate %>'>
					</div>
				</div>
			</div>
		</form>

		<div class="tool_form">
                    <a class="tool_item tool_account" onclick="toggleMenu()">
                        <img class="tool_account_avt" src="../assets/img/avt.png">
                        <% if(session.getAttribute(Constant.USERID) != null) { %>
                        	<span><%=session.getAttribute(Constant.USERNAME) %></span>
                        <% } else { %>
                        	<span>Tài khoản</span>
                        <% } %>
                    </a>

                    <div class="container_menu" id="accountMenu">
                        <ul>
                            <li><a href="../authentication/authentication?my_action=update"><i class="fa-regular fa-user"></i>Thông tin tài khoản</a></li>
                            <li><a href="../authentication/authentication?my_action=logout"><i class="fa-solid fa-arrow-right-from-bracket"></i>Đăng xuất</a></li>
                        </ul>
                    </div>
                </div>
	</header>
</body>
<script src="../assets/js/header.js"></script>
</html>