package clientsocket;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dto.MailBoxDTO;
import dto.MailDetail;
import dto.MailDetailDTO;
import dto.SearchDTO;
import enums.Status;
import utils.Constant;
import utils.CreateGoogleFile;
import utils.CustomDateDeserializer;

public class MailHandler {
	private Socket socket;
	private DataInputStream dis;
	private DataOutputStream dos;
	private Gson gson;
	private String sessionId;
//	private static int tmp;

	public MailHandler(String sessionId, String _email) {
		this.sessionId = sessionId;
		socket = ClientSocket.getInstance(sessionId, _email);
//		this.gson = new Gson();
		try {
			dis = new DataInputStream(socket.getInputStream());
			dos = new DataOutputStream(socket.getOutputStream());
			gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new CustomDateDeserializer()).create();
		} catch (IOException e) {
			ClientSocket.disconnectSocket(sessionId);
			e.printStackTrace();
		}
	}

	public boolean sendNewMail(MailDetail newMail) throws ServletException {
		boolean isSuccess = false;
		try {
			dos.writeUTF(Constant.SEND_REQUEST);
			dos.flush();

			String json = gson.toJson(newMail);
			byte[] jsonData = json.getBytes("UTF-8");
			int totalLength = jsonData.length;
			int chunkSize = 1024; // Độ dài mỗi phần
			dos.writeInt(totalLength); // Gửi tổng độ dài của chuỗi JSON
			int offset = 0;
			while (offset < totalLength) {
				int length = Math.min(chunkSize, totalLength - offset);
				dos.write(jsonData, offset, length); // Gửi từng phần nhỏ của chuỗi JSON
				offset += length;
			}

//			Kiem tra xem them thanh cong hay khong
			String result = dis.readUTF();
			if (result.equals(Constant.SUCCESS))
				isSuccess = true;
		} catch (SocketTimeoutException e) {
			ClientSocket.disconnectSocket(sessionId);
			System.err.println("Hệ thống chờ hồi đáp quá lâu");
			throw new ServletException("Kết nối với hệ thống gặp vấn đề");
		} catch (Exception e) {
			ClientSocket.disconnectSocket(sessionId);
			e.printStackTrace();
		}
		return isSuccess;
	}

	public Object[] getMailboxbyId(int userId, String action) throws ServletException {
		int countUnread = 0;
		ArrayList<MailBoxDTO> listMailBox = new ArrayList<MailBoxDTO>();
		try {
			if (action.equals(Constant.INCOMING_MAILBOX)) {
				dos.writeUTF(Constant.MAILBOX_REQUEST);
				dos.flush();
			}
			if (action.equals(Constant.OUTBOX)) {
				// doGetOutBox(request, response);
				dos.writeUTF(Constant.OUTBOX_REQUEST);
				dos.flush();
			}
			if (action.equals(Constant.ALL_MAILBOX)) {
				// doGetAllMailBox(request, response);
				dos.writeUTF(Constant.ALL_REQUEST);
				dos.flush();
			}
			if (action.equals(Constant.DELETED_MAILBOX)) {
				// doGetAllMailBox(request, response);
				dos.writeUTF(Constant.DELETED_REQUEST);
				dos.flush();
			}
			if (action.equals(Constant.SPAM_MAILBOX)) {
				// doGetAllMailBox(request, response);
				dos.writeUTF(Constant.SPAM_REQUEST);
				dos.flush();
			}
			dos.writeUTF(userId + "");
			dos.flush();
			// Nhan kq
			while (true) {
				int totalLength = dis.readInt(); // Nhận tổng độ dài của chuỗi JSON
				ByteArrayOutputStream buffer = new ByteArrayOutputStream();
				int remaining = totalLength;
				while (remaining > 0) {
					byte[] data = new byte[Math.min(1024, remaining)];
					int bytesRead = dis.read(data, 0, data.length);
					buffer.write(data, 0, bytesRead);
					remaining -= bytesRead;
				}
				String receiveStr = new String(buffer.toByteArray(), "UTF-8");
				if (receiveStr.equals(Constant.SUCCESS))
					break;
				MailBoxDTO mailDetail = gson.fromJson(receiveStr, MailBoxDTO.class);
				listMailBox.add(mailDetail);
			}
		} catch (SocketTimeoutException e) {
			ClientSocket.disconnectSocket(sessionId);
			System.err.println("Hệ thống chờ hồi đáp quá lâu");
			throw new ServletException("Kết nối với hệ thống gặp vấn đề");
		} catch (Exception e) {
			ClientSocket.disconnectSocket(sessionId);
			e.printStackTrace();
		}

		if (action != null && action.equals(Constant.INCOMING_MAILBOX)) {
			for (MailBoxDTO mailBoxDTO : listMailBox) {
				int status = mailBoxDTO.getStatus();
				if (status == Status.UNREAD.getValue()) {
					countUnread++;
				}
			}
		}
		return new Object[] { listMailBox, countUnread };
	}

	public List<MailBoxDTO> searchAndFilter(SearchDTO searchObject) throws ServletException {
		List<MailBoxDTO> listMailBox = new ArrayList<MailBoxDTO>();
		try {
			dos.writeUTF(Constant.SEARCH_AND_FILTER_REQUEST);
			dos.flush();
			String action = searchObject.getRequest();
			String json = gson.toJson(searchObject);
			dos.writeUTF(json);
			dos.flush();

			String object;
			// Nhan kq
			while (true) {
				int totalLength = dis.readInt(); // Nhận tổng độ dài của chuỗi JSON
				ByteArrayOutputStream buffer = new ByteArrayOutputStream();
				int remaining = totalLength;
				while (remaining > 0) {
					byte[] data = new byte[Math.min(1024, remaining)];
					int bytesRead = dis.read(data, 0, data.length);
					buffer.write(data, 0, bytesRead);
					remaining -= bytesRead;
				}
				String receiveStr = new String(buffer.toByteArray(), "UTF-8");
				if (receiveStr.equals(Constant.SUCCESS))
					break;
				MailBoxDTO mailDetail = gson.fromJson(receiveStr, MailBoxDTO.class);
				listMailBox.add(mailDetail);
			}
		} catch (SocketTimeoutException e) {
			ClientSocket.disconnectSocket(sessionId);
			System.err.println("Hệ thống chờ hồi đáp quá lâu");
			throw new ServletException("Kết nối với hệ thống gặp vấn đề");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return listMailBox;
	}

	public void checkMailStatus(String mailID[], int userID, String action) throws ServletException {
		try {
			if (action.equals(Constant.DELETE_MAIL)) {
				dos.writeUTF(Constant.DELETE_MAIL_REQUEST);
				dos.flush();
			} else if (action.equals(Constant.UNDELETE_MAIL)) {
				dos.writeUTF(Constant.UNDELETE_MAIL_REQUEST);
				dos.flush();
			} else if (action.equals(Constant.CHECK_UNREAD_MAIL)) {
				dos.writeUTF(Constant.CHECK_UNREAD_MAIL_REQUEST);
				dos.flush();
			} else if (action.equals(Constant.CHECK_READ_MAIL)) {
				dos.writeUTF(Constant.CHECK_READ_MAIL_REQUEST);
				dos.flush();
			} else if (action.equals(Constant.CHECK_SPAM_MAIL)) {
				dos.writeUTF(Constant.CHECK_SPAM_MAIL_REQUEST);
				dos.flush();
			} else if (action.equals(Constant.CHECK_UNSPAM_MAIL)) {
				dos.writeUTF(Constant.CHECK_UNSPAM_MAIL_REQUEST);
				dos.flush();
			}

			dos.writeUTF(userID + "");
			dos.flush();

			String request = "";
			for (int i = 0; i < mailID.length; i++) {
				request += mailID[i] + ";";
			}
			dos.writeUTF(request);
			dos.flush();
		} catch (SocketTimeoutException e) {
			ClientSocket.disconnectSocket(sessionId);
			System.err.println("Hệ thống chờ hồi đáp quá lâu");
			throw new ServletException("Kết nối với hệ thống gặp vấn đề");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			ClientSocket.disconnectSocket(sessionId);
			e.printStackTrace();
		}

	}

	public List<MailDetailDTO> getDetailMail(int mailId, int userId) throws ServletException {
		List<MailDetailDTO> listMailDetail = new ArrayList<MailDetailDTO>();
		try {
			dos.writeUTF(Constant.MAIL_DETAIL_REQUEST);
			dos.flush();
			dos.writeUTF(mailId + "");
			dos.flush();
			dos.writeUTF(userId + "");
			dos.flush();

			while (true) {
				int totalLength = dis.readInt(); // Nhận tổng độ dài của chuỗi JSON
				ByteArrayOutputStream buffer = new ByteArrayOutputStream();
				int remaining = totalLength;
				while (remaining > 0) {
					byte[] data = new byte[Math.min(1024, remaining)];
					int bytesRead = dis.read(data, 0, data.length);
					buffer.write(data, 0, bytesRead);
					remaining -= bytesRead;
				}
				String receiveStr = new String(buffer.toByteArray(), "UTF-8");
				if (receiveStr.equals(Constant.SUCCESS))
					break;
				MailDetailDTO mailDetail = gson.fromJson(receiveStr, MailDetailDTO.class);
				listMailDetail.add(mailDetail);
			}
			for (MailDetailDTO detail : listMailDetail) {
				if (detail.getMailContent().getAttachment() != null) {
					detail.setAttachmentName(CreateGoogleFile.getFileName(detail.getMailContent().getAttachment()));
				}
			}
		} catch (SocketTimeoutException e) {
			ClientSocket.disconnectSocket(sessionId);
			System.err.println("Hệ thống chờ hồi đáp quá lâu");
			throw new ServletException("Kết nối với hệ thống gặp vấn đề");
		} catch (Exception e) {
			ClientSocket.disconnectSocket(sessionId);
			e.printStackTrace();
		}
		return listMailDetail;
	}

	public MailDetailDTO getReplyTo(String action, int mailID, int userID) {
		MailDetailDTO detail = null;
		try {
			dos.writeUTF(action);
			dos.flush();
			dos.writeUTF(mailID + "");
			dos.flush();
			dos.writeUTF(userID + "");
			dos.flush();
//			int rootMailID = Integer.parseInt(dis.readUTF());
			String object = dis.readUTF();
			detail = gson.fromJson(object, MailDetailDTO.class);
//			detail.setMailID(rootMailID);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return detail;
	}
}
