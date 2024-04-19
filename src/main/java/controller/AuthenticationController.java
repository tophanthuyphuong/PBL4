package controller;

import java.io.IOException;
import java.util.Base64;
import java.util.Random;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import clientsocket.AccountHandler;
import dto.User;
import utils.AuthorizeMail;
import utils.Constant;
import utils.EncodeHash;

@WebServlet("/authentication/authentication")
public class AuthenticationController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String sessionId;
	private String url;

	public AuthenticationController() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		HttpSession session = request.getSession(true);
		sessionId = session.getId();
		String action = request.getParameter(Constant.MY_ACTION);
		if (action == null) {
			requestDispacherForward("/authentication/login.jsp", request, response);
		} else if (action.equals(Constant.SIGNUP)) {
			requestDispacherForward("/authentication/signup.jsp", request, response);
		} else if (action.equals(Constant.LOGOUT))
			doGetLogout(request, response);
		else if (action.equals(Constant.UPDATE_ACCOUNT))
			doGetDetailAccount(request, response);

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		HttpSession session = request.getSession(true);
		sessionId = session.getId();
		String action = request.getParameter(Constant.MY_ACTION);
		if (action == null) {
			response.getWriter().append("Served at: ").append(request.getContextPath());
		} else if (action.equals(Constant.LOGIN))
			doPostLogin(request, response);
		else if (action.equals(Constant.SIGNUP))
			doPostSignup(request, response);
		else if (action.equals(Constant.SUBMIT_UPDATE_ACCOUNT))
			doPostUpdateAccount(request, response);
		else if (action.equals(Constant.NEW_PASSWORD))
			doPostChangePassword(request, response);
		else if (action.equals(Constant.AUTHORIZE))
			doPostAuthorize(request, response);
		else if (action.equals(Constant.FORGET_PASSWORD))
			doPostForgetPassword(request, response);
	}

	private synchronized void doPostLogin(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String email = request.getParameter("email");
		String password = request.getParameter("password");

		Object[] object = new AccountHandler(sessionId, null).getUserbyEmailandPassword(email, password);
		String result = (String) object[0];
		if (result.equals(Constant.SUCCESS)) {
			HttpSession session = request.getSession();
			User user = (User) object[1];
			session.setAttribute(Constant.USERID, user.getUserId());
			session.setAttribute(Constant.USERNAME, user.getName());
			session.setAttribute(Constant.EMAIL, user.getEmail());
			session.setAttribute(Constant.KEY, user.getKey());
			session.setAttribute(Constant.IV, user.getIv());

			url = "/mail/mail?my_action=" + Base64.getEncoder().encodeToString(Constant.INCOMING_MAILBOX.getBytes());
			sendRedirect(url, request, response);
		} else {
			String errorMessage = "";
			if (result.equals(Constant.EMAIL_UNEXISTED))
				errorMessage = "Tài khoản không tồn tại!";
			else if (result.equals(Constant.WRONG_PASSWORD)) {
				errorMessage = "Mật khẩu không đúng!";
			}

			request.setAttribute("error", errorMessage);
			request.setAttribute("email", email);
			url = "/authentication/login.jsp";
			requestDispacherForward(url, request, response);
		}
	}

	private synchronized void doPostSignup(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		String lastname = request.getParameter("lastname");
		String firstname = request.getParameter("firstname");
		String birthday = !request.getParameter("birthday").equals("") ? request.getParameter("birthday") : null;
		String phonenumber = request.getParameter("phonenumber");
		String gmail = request.getParameter("gmail");
		System.out.println(gmail);
//		password = EncodeHash.toSHA1(password);
		System.out.println(password);
		String name = lastname + " " + firstname;

		User user = new User(email, password, name, phonenumber, birthday, gmail);
		String registResponse = "hi";// new AccountHandler(sessionId, null).registerAccount(user);

		if (registResponse.equals(Constant.EMAIL_EXISTED) || registResponse.equals(Constant.ERROR_VALIDATE)) {
			if (registResponse.equals(Constant.EMAIL_EXISTED)) {
				request.setAttribute("errorMessage", "Email đã tồn tại. Bạn vui lòng nhập email khác!");
			} else {
				request.setAttribute("errorMessage", "Vui lòng nhập mật khẩu đúng định dạng!");
			}
			request.setAttribute("email", email);
			request.setAttribute("lastname", lastname);
			request.setAttribute("firstname", firstname);
			request.setAttribute("birthday", birthday);
			request.setAttribute("phonenumber", phonenumber);
			request.setAttribute("gmail", gmail);

			url = "/authentication/signup.jsp";
			requestDispacherForward(url, request, response);
		} else if (registResponse.equals(Constant.ERROR)) {
			throw new RuntimeException("Đăng ký lỗi");
		} else {
			HttpSession session = request.getSession();
			request.setAttribute("gmail", gmail);
			session.setAttribute("user", user);
			Random random = new Random();
			int passcode = 100000 + random.nextInt(900000);
			new AuthorizeMail().authorize(gmail, passcode);
			session.setAttribute("passcode", passcode);
			session.getAttribute("user").toString();
			requestDispacherForward("/authentication/authorize.jsp", request, response);
		}
	}

	private void doPostAuthorize(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession();
		int passcode = (Integer) session.getAttribute("passcode");
		int inputCode = Integer.parseInt(request.getParameter("inputCode"));
		if (passcode != inputCode) {
//			response.getWriter().write("Mã xác thực không chính xác");
			String gmail = request.getParameter("gmail");
//			System.out.println(gmail);
			request.setAttribute("gmail", gmail);
			request.setAttribute("error", "Mã xác thực không chính xác");
			url = "/authentication/authorize.jsp";
			requestDispacherForward(url, request, response);
		} else {
			User user = (User) session.getAttribute("user");
			System.out.println(user.getEmail());
			session.invalidate();
			String registResponse = new AccountHandler(sessionId, null).registerAccount(user);
			session = request.getSession();
			String[] resArr = registResponse.split(";");
			int userID = Integer.parseInt(resArr[0]);
			session.setAttribute(Constant.USERID, userID);
			session.setAttribute(Constant.USERNAME, user.getName());
			session.setAttribute(Constant.EMAIL, user.getEmail());
			session.setAttribute(Constant.KEY, resArr[1]);
			session.setAttribute(Constant.IV, resArr[2]);
			System.out.println(resArr[1]);
			System.out.println(resArr[2]);
			url = "/mail/mail?my_action=" + Base64.getEncoder().encodeToString(Constant.INCOMING_MAILBOX.getBytes());
			sendRedirect(url, request, response);
		}
	}

	private void doPostForgetPassword(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		if (request.getParameter("email") == null) {
			sendRedirect("/authentication/forget-password.jsp", request, response);
		} else {

//		    request.getSession().setAttribute("passcode", passcode);
			String gmail = request.getParameter("gmail");
			String email = request.getParameter("email");
			request.setAttribute("email", email);
			System.out.println(request.getParameter("inputCode"));
			if (request.getParameter("inputCode") != null) {
				request.setAttribute("gmailInput", true);
				int passcode = (Integer) request.getSession().getAttribute("passcode");
				System.out.println(passcode);
				int inputCode = Integer.parseInt(request.getParameter("inputCode"));
				if (passcode != inputCode) {
					request.setAttribute("gmail", gmail);
//					request.setAttribute("gmailInput", true);
					request.setAttribute("errorMessage", "Mã xác thực không chính xác");
					url = "/authentication/forget-password.jsp";
					requestDispacherForward(url, request, response);
				} else {
					String newPassword = request.getParameter("password");
					System.out.println(email);
					String registResponse = new AccountHandler(sessionId, null).forgetPassword(email, newPassword);
					if (registResponse == Constant.ERROR_VALIDATE) {
						request.setAttribute("errorMessage", "Vui lòng nhập mật khẩu đúng định dạng!");
						requestDispacherForward("/authentication/forget-password.jsp", request, response);
					} else {
//			    		url = "/authentication/login.jsp";
//						requestDispacherForward(url, request, response);
						request.getSession().invalidate();
						sendRedirect("/authentication/login.jsp", request, response);
					}
				}
			} else {
				boolean authorize = new AccountHandler(sessionId, null).authorizeGmailAndEmail(gmail, email);
				if (authorize) {
					request.setAttribute("gmailInput", true);
					request.setAttribute("gmail", gmail);
					Random random = new Random();
					int passcode = 100000 + random.nextInt(900000);
					new AuthorizeMail().authorize(gmail, passcode);
					request.getSession().setAttribute("passcode", passcode);
					url = "/authentication/forget-password.jsp";
					requestDispacherForward(url, request, response);
				} else {
					request.setAttribute("errorMessage", "Tài khoản hoặc tài khoản xác thực không chính xác");
					request.setAttribute("gmail", gmail);
					request.setAttribute("email", email);
					url = "/authentication/forget-password.jsp";
					requestDispacherForward(url, request, response);
				}
			}

		}
	}

	private synchronized void doPostUpdateAccount(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		String email = (String) session.getAttribute(Constant.EMAIL);
		if (email != null) {
			String fullname = request.getParameter("fullname");
			String birthday = !request.getParameter("birthday").equals("") ? request.getParameter("birthday") : null;
			String phonenumber = request.getParameter("phonenumber");
			Boolean gender = request.getParameter("gender").equals("false") ? false : true;
			// System.out.println(request.getParameter("gender") + gender);
			User user = new User(email, null, fullname, phonenumber, birthday, gender);
			boolean result = new AccountHandler(sessionId, email).updateAccount(user);

			if (result) {
				session.setAttribute(Constant.USERNAME, user.getName());
				url = "/authentication/authentication?my_action=" + Constant.UPDATE_ACCOUNT;
				sendRedirect(url, request, response);
			} else {
				throw new RuntimeException("Cập nhật không thành công");
			}

		} else {
			redirectLoginPage(request, response);
		}
	}

	private synchronized void doGetLogout(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		String email = (String) session.getAttribute(Constant.EMAIL);
		session.invalidate();
		try {
			new AccountHandler(sessionId, email).logout();
		} catch (Exception ex) {
		}
		// Tạo mã JavaScript để lưu vào sessionStorage
		String path = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
				+ request.getContextPath() + "/authentication/authentication";
		String script = "<script>" + "    localStorage.removeItem('private_key');"
				+ "    localStorage.removeItem('private_iv');" + "    setTimeout(function() { window.location.href = '"
				+ path + "';}, 1500);" + "</script>";
		response.setContentType("text/html");
		response.getWriter().print(script);
	}

	private synchronized void doGetDetailAccount(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		String email = (String) session.getAttribute(Constant.EMAIL);

		if (email != null) {
			User user = new AccountHandler(sessionId, email).getDetailAccount(email);
			if (user != null) {
				request.setAttribute("fullname", user.getName());
				request.setAttribute("email", user.getEmail());
				request.setAttribute("birthday", user.getBirth());
				request.setAttribute("phonenumber", user.getPhone());
				request.setAttribute("gender", user.getGender());

				url = "/manage-account/account_update.jsp";
				requestDispacherForward(url, request, response);
			} else {
				doGetLogout(request, response);
			}
		} else {
			redirectLoginPage(request, response);
		}
	}

	private void requestDispacherForward(String url, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		RequestDispatcher rd = getServletContext().getRequestDispatcher(url);
		rd.forward(request, response);
	}

	private synchronized void doPostChangePassword(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		String email = (String) session.getAttribute(Constant.EMAIL);
		if (email != null) {
			String password = request.getParameter("old_password");
			String newPassword = request.getParameter("new_password");
			System.out.println(password);
			System.out.println(newPassword);
//			password = EncodeHash.toSHA1(password);
//			newPassword = EncodeHash.toSHA1(newPassword);

			String changeResult = new AccountHandler(sessionId, email).changePassword(email, password, newPassword);
			if (changeResult.equals(Constant.SUCCESS)) {
				url = "/authentication/authentication?my_action=update";
				response.sendRedirect(url);
			} else {
				if (changeResult.equals(Constant.WRONG_PASSWORD))
					request.setAttribute("error", "Mật khẩu cũ không đúng!");
				else if (changeResult.equals(Constant.ERROR_VALIDATE))
					request.setAttribute("error", "Mật khẩu mới không đúng định dạng!");

				url = "/manage-account/account_password.jsp";
				RequestDispatcher rd = getServletContext().getRequestDispatcher(url);
				rd.forward(request, response);
			}
//			String fullname = request.getParameter("fullname");
//			String birthday = !request.getParameter("birthday").equals("") ? request.getParameter("birthday") : null;
//			String phonenumber = request.getParameter("phonenumber");
//
//			User user = new User(email, null, fullname, phonenumber, birthday);
//			boolean result = new AccountHandler(email).updateAccount(user);
//			
//			if (result) {
//				session.setAttribute(Constant.USERNAME, user.getName());
//			}
//			
//			url = request.getScheme() + "://" + request.getServerName() + ":" 
//					+ request.getServerPort() + request.getContextPath();
//			url += result ? ("/authentication/authentication?my_action=" + Constant.UPDATE_ACCOUNT)
//						: "/error/error500.jsp";
//			response.sendRedirect(url);
		} else {
			redirectLoginPage(request, response);
		}

	}

	private void sendRedirect(String destination, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String path = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
				+ request.getContextPath();
		path += destination;
		response.sendRedirect(path);
	}

	private void redirectLoginPage(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		url = "/authentication/authentication";
		sendRedirect(url, request, response);
	}
}
