package controller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import com.google.gson.Gson;

import clientsocket.MailHandler;
import dto.MailBoxDTO;
import dto.MailDetail;
import dto.MailDetailDTO;
import dto.SearchDTO;
import utils.AESEncryption;
import utils.Constant;
import utils.CreateGoogleFile;
import utils.HTMLSpecialChars;

@WebServlet(urlPatterns = { "/mail/mail", "/updateSessionData" })
@MultipartConfig() // 50MB
public class MailController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String sessionId;
	private String action = null;
	private String url;
	private int userId;
	private String userEmail = null;

	public MailController() {
		super();
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		sessionId = session.getId();
		if (session.getAttribute(Constant.USERID) != null) {
			String requestURI = request.getRequestURI();
			if ("/updateSessionData".equals(requestURI)) {
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				// Giả sử `countUnread` chứa số lượng thông báo chưa đọc
				
				int currentUnread = (Integer) session.getAttribute(Constant.COUNT_UNREAD) + 1;
				session.removeAttribute(Constant.COUNT_UNREAD);
				session.setAttribute(Constant.COUNT_UNREAD, currentUnread);
				response.getWriter().write("{\"countUnread\":" + currentUnread + "}");
			} else {
				userId = (Integer) session.getAttribute("userId");
				userEmail = (String) session.getAttribute("email");

				action = request.getParameter("my_action");
				action = ((action != null)) ? new String(Base64.getDecoder().decode(action)) : null;
				if (action == null) {
					response.getWriter().append("Served at: ").append(request.getContextPath());
				} else if (action.equals(Constant.DETAIL)) {
					doGetDetails(request, response);
				} else if (action.equals(Constant.REPLY) || action.equals(Constant.REPLY_ALL)) {
					doGetReply(action, request, response);
				} else if (action.equals(Constant.SEARCH_AND_FILTER)) {
					doPostSearch(request, response);
				} else {
					System.out.println(userId);
					System.out.println(userEmail);
					doGetMailBox(request, response);
				}
			}
		} else {
			url = "/authentication/authentication";
			sendRedirect(url, request, response);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		HttpSession session = request.getSession(true);
		sessionId = session.getId();
		if (session.getAttribute("userId") != null) {
			userId = (Integer) session.getAttribute("userId");
			userEmail = (String) session.getAttribute("email");

			action = request.getParameter("my_action");
			action = ((action != null)) ? new String(Base64.getDecoder().decode(action)) : null;

			if (action == null) {
				response.getWriter().append("Served at: ").append(request.getContextPath());
			} else if (action.equals(Constant.INBOX)) {
				doPostInbox(request, response);
			}  else {
				doPostCheckStatus(request, response);
			}
		} else {
			url = "/authentication/authentication";
			sendRedirect(url, request, response);
		}
	}

	private void doPostSearch(HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		try {
			String search_type = (request.getParameter("search_type") == null)? Constant.MAILBOX_REQUEST : request.getParameter("search_type");
			String input = (request.getParameter("search_input") == null)? "" : request.getParameter("search_input");
			String startDate = (request.getParameter("startDate") == null)? "" : request.getParameter("startDate");
			String endDate = (request.getParameter("endDate") == null)? "" : request.getParameter("endDate");
			request.setAttribute("startDate", startDate);
			request.setAttribute("endDate", endDate);
			if (startDate.equals("") && !endDate.equals(""))
				startDate = "2023-01-01";
			if (!startDate.equals("") && endDate.equals("")) {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				Date end = Calendar.getInstance().getTime();
				endDate = dateFormat.format(end);
			}
			SearchDTO searchObject = new SearchDTO(userId, search_type, input, startDate, endDate);
			List<MailBoxDTO> mail = new MailHandler(sessionId, userEmail).searchAndFilter(searchObject);
			String currentPage = (String) ((request.getParameter("currentPage") == null) ? "1"
					: request.getParameter("currentPage"));
			System.out.println(currentPage + "----");
			request.setAttribute("currentPage", currentPage);
			request.setAttribute("listmail", mail);
			request.setAttribute("isProcessed", true);
			request.setAttribute("my_action", Constant.SEARCH_AND_FILTER);
			request.setAttribute("search_input", input);
//			request.setAttribute("startDate", startDate);
//			request.setAttribute("endDate", endDate);
			url = "/mail/mail-box.jsp";
			RequestDispatcher rd = getServletContext().getRequestDispatcher(url);
			rd.forward(request, response);
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private synchronized void doGetDetails(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String currentPage = (String) ((request.getParameter("currentPage") == null) ? "1"
				: request.getParameter("currentPage"));
		request.setAttribute("currentPage", currentPage);
		String mailStr = request.getParameter("mailId");
		request.setAttribute("mailID", mailStr);
		String decodingStr = new String(Base64.getDecoder().decode(mailStr));
		
		int mailId = Integer.parseInt(decodingStr);
		List<MailDetailDTO> listMailDetail = new MailHandler(sessionId, userEmail).getDetailMail(mailId, userId);
		request.setAttribute("listDetail", listMailDetail);
		// Xử lý thông tin email
		requestDispacherForward("/mail/detail-email.jsp", request, response);
	}

	private synchronized void doGetReply(String action, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		int mailID = Integer.parseInt(request.getParameter("mailID"));
		// Gui yeu cau ve server
		MailDetailDTO detail = new MailHandler(sessionId, userEmail).getReplyTo(action, mailID, userId);
		// Lay thong tin
		request.setAttribute("mailDetail", detail);
		requestDispacherForward("/mail/inbox.jsp", request, response);
	}

	private synchronized void doGetMailBox(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		if (action.equals(Constant.SEARCH_AND_FILTER)) {
			doPostSearch(request, response);
		} else {
			Object[] object = new MailHandler(sessionId, userEmail).getMailboxbyId(userId, action);
			ArrayList<MailBoxDTO> mail = (ArrayList<MailBoxDTO>) object[0];
			String currentPage = (String) ((request.getParameter("currentPage") == null) ? "1"
					: request.getParameter("currentPage"));
			if (action != null && action.equals(Constant.INCOMING_MAILBOX)) {
				int countUnread = (Integer) object[1];
				HttpSession session = request.getSession();
				session.setAttribute(Constant.COUNT_UNREAD, countUnread);
			}
			request.setAttribute("currentPage", currentPage);
			request.setAttribute("listmail", mail);
			request.setAttribute("isProcessed", true);
			request.setAttribute(Constant.MY_ACTION, action);
			requestDispacherForward("/mail/mail-box.jsp", request, response);
		}
	}

	private synchronized void doPostCheckStatus(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String[] check = request.getParameterValues("selectedValues[]");
		String new_action = request.getParameter("my_new_action");
		new MailHandler(sessionId, userEmail).checkMailStatus(check, userId, new_action);
	}

	private String extractFileName(Part part) {
		String contentDisp = part.getHeader("content-disposition");
		String[] items = contentDisp.split(";");
		for (String s : items) {
			if (s.trim().startsWith("filename")) {
				return s.substring(s.indexOf("=") + 2, s.length() - 1);
			}
		}
		return "";
	}

	public File getFolderUpload() {
		File folderUpload = new File(System.getProperty("user.home") + "/Uploads");
		if (!folderUpload.exists()) {
			folderUpload.mkdirs();
		}
		return folderUpload;
	}

	private synchronized void doPostInbox(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String toMail = request.getParameter("toMail");
		String ccMail = request.getParameter("ccMail");
		String bccMail = request.getParameter("bccMail");
		String subjectMail = request.getParameter("subjectMail");
		String contentMail = request.getParameter("contentMail");
		String replyTo = request.getParameter("rootMailID");
		String att = request.getParameter("fileToUpload");
		String attachment = null;
		Part part = request.getPart("fileToUpload");
		if (!part.getSubmittedFileName().equals("")) {
		    String fileName = extractFileName(part);
		    fileName = new File(fileName).getName(); // Đảm bảo lấy tên file mà không có đường dẫn
		    part.write(this.getFolderUpload().getAbsolutePath() + File.separator + fileName);
		    String filePath = this.getFolderUpload().getAbsolutePath() + File.separator + fileName;
		    System.out.println(filePath);
		    attachment = CreateGoogleFile.getAttachmentLink(filePath);
		    String fileID = CreateGoogleFile.extractFileIdFromUrl(attachment);
//		    System.out.println(attachment);
		    CreateGoogleFile.createPublicPermission(fileID);
		} else {
			// Xử lý khi part là null
		}
		
		contentMail = getHtmlspecialchars(contentMail, request);
		subjectMail = getHtmlspecialchars(subjectMail, request);
		System.out.println("subject Controller: " + subjectMail);
		System.out.println("subject Content: " + subjectMail);
		
		if ((!toMail.equals("") || !ccMail.equals("") || !bccMail.equals(""))) {
			MailDetail newMail = new MailDetail(userId, subjectMail, contentMail);
			List<String> listToMail = splitMail(toMail);
			List<String> listCCMail = splitMail(ccMail);
			List<String> listBCCMail = splitMail(bccMail);
			newMail.setToMail(listToMail);
			newMail.setCcMail(listCCMail);
			newMail.setBccMail(listBCCMail);
			newMail.setAttachment(attachment);

			if (replyTo == null) {
				newMail.setReplyTo(0);
			} else {
				newMail.setReplyTo(Integer.parseInt(replyTo));
			}

			boolean isSuccess = new MailHandler(sessionId, userEmail).sendNewMail(newMail);
			if (isSuccess) {
				url = "/mail/mail?my_action=" + Base64.getEncoder().encodeToString(Constant.OUTBOX.getBytes());
				sendRedirect(url, request, response);
			} else {
				throw new RuntimeException("Gửi mail không thành công");
			}
		} else {
			request.setAttribute("send_status", Constant.ERROR);
			requestDispacherForward("/mail/inbox.jsp", request, response);
		}
	}
	
	private String getHtmlspecialchars(String content, HttpServletRequest request) {
		String key = (String) request.getSession().getAttribute(Constant.KEY);
		String iv = (String) request.getSession().getAttribute(Constant.IV);
		key = AESEncryption.decryptDefault(key);
		iv = AESEncryption.decryptDefault(iv);
		
		// Xu ly cac ky hieu html dac biet
		String result = AESEncryption.decrypt(content, key, iv);
		System.out.println(content + " goc: " + result);
		result = HTMLSpecialChars.htmlspecialchars(result);
		
		// Ma hoa lai
		result = AESEncryption.encrypt(result, key, iv);
		return result;
	}

	private void requestDispacherForward(String url, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		RequestDispatcher rd = getServletContext().getRequestDispatcher(url);
		rd.forward(request, response);
	}

	private void sendRedirect(String destination, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String path = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
				+ request.getContextPath();
		path += destination;
		response.sendRedirect(path);
	}

	private List<String> splitMail(String str) {
		List<String> list = null;
		if (!str.equals("")) {
			list = new ArrayList<String>();
			String[] arr = str.split("\\s+");
			for (int i = 0; i < arr.length; i++)
				list.add(arr[i]);
		}
		return list;
	}

//	public void doGetGet(HttpServletRequest request, HttpServletResponse response)
//			throws ServletException, IOException {
//		// Trả về dữ liệu mới dưới dạng JSON
//		String json = new MailHandler(userEmail).receiveNewEmail();
//		response.setContentType("application/json");
//		response.setCharacterEncoding("UTF-8");
//		PrintWriter out = response.getWriter();
//		out.print(json);
//		out.flush();
//	}

	// Phương thức để lấy dữ liệu mới từ server
//    private String fetchDataFromServer() {
//        // Thực hiện logic để lấy dữ liệu mới từ server
//        return "Dữ liệu mới";
//    }
}
