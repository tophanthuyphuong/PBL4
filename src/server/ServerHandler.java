package server;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import bll.MailBLL;
import bll.UserBLL;
import model.MailBoxDTO;
import model.MailDetailDTO;
import model.MailInboxDTO;
import model.SearchDTO;
import model.User;
import utils.AESEncryption;
import utils.ClientIden;
import utils.Constant;
import utils.CustomDateDeserializer;
import utils.LogEmailObject;
import utils.Status;

public class ServerHandler extends Thread {
	private Socket client;
	private DatagramPacket datagramPacket;
	private String _clientEmail;
	private DataInputStream dis;
	private DataOutputStream dos;
	private Gson gson;
	private long lastActionTime;

	public String getClientEmail() {
		return _clientEmail;
	}

	public ServerHandler(Socket client) {
		try {
			gson = new GsonBuilder().registerTypeAdapter(Date.class, new CustomDateDeserializer()).create();
			if (this.client == null) {
				this.client = client;
				dis = new DataInputStream(this.client.getInputStream());
				dos = new DataOutputStream(this.client.getOutputStream());

				String json = dis.readUTF();
				ClientIden infor = gson.fromJson(json, ClientIden.class);
				if (infor.getEmail() != null) {
					_clientEmail = infor.getEmail();
					System.out.println(_clientEmail + " dang ket noi");
					String log = _clientEmail + " online";
					logFile(log);
				}
				datagramPacket = new DatagramPacket(new byte[0], 0);
				datagramPacket.setAddress(InetAddress.getByName(infor.getAddress()));
				datagramPacket.setPort(infor.getPort());
				lastActionTime = System.currentTimeMillis();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void disconnectSocket() {
		try {
			if (client != null) {
				if (_clientEmail != null) {
					System.out.println(_clientEmail + " da ngat ket noi");
					String log = _clientEmail + " offline";
					logFile(log);
				}
					
				dis.close();
				dos.close();
				if (client != null && !client.isClosed())
					client.close();
				client = null;
				Server.removeServerHandler(this);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void run() {
		Thread timerThread = new Thread(() -> {
			while (!Thread.currentThread().isInterrupted()) {
				long currentTime = System.currentTimeMillis();

				if (currentTime - lastActionTime > 10 * 60 * 1000) {
					disconnectSocket();
					Thread.currentThread().interrupt();
					break;
				}

				try {
					Thread.sleep(1000); // Kiểm tra mỗi giây một lần
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		timerThread.start();

		while (client != null) {
			try {
				String request = dis.readUTF();
				switch (request) {
				case Constant.LOGIN_REQUEST:
					login();
					lastActionTime = System.currentTimeMillis();
					if (_clientEmail != null) {
						String log = _clientEmail + " online";
						logFile(log);
					}
					break;
				case Constant.SIGNUP_REQUEST:
					registerAccount();
					lastActionTime = System.currentTimeMillis();
					if (_clientEmail != null) {
						String log = _clientEmail + " register";
						logFile(log);
					}
					break;
				case Constant.GET_ACCOUNT_REQUEST:
					getDetailAccount();
					lastActionTime = System.currentTimeMillis();
					break;
				case Constant.UPDATE_ACCOUNT_REQUEST:
					updateAccount();
					lastActionTime = System.currentTimeMillis();
					break;
				case Constant.SEND_REQUEST:
					sendNewMail();
					lastActionTime = System.currentTimeMillis();
					break;
				case Constant.MAILBOX_REQUEST:
				case Constant.OUTBOX_REQUEST:
				case Constant.ALL_REQUEST:
				case Constant.DELETED_REQUEST:
				case Constant.SPAM_REQUEST:
					getMailBox(request);
					lastActionTime = System.currentTimeMillis();
					break;
				case Constant.DELETE_MAIL:
				case Constant.UNDELETE_MAIL:
				case Constant.CHECK_READ_MAIL:
				case Constant.CHECK_UNREAD_MAIL:
				case Constant.CHECK_SPAM_MAIL:
				case Constant.CHECK_UNSPAM_MAIL:
					setMailStatus(request);
					lastActionTime = System.currentTimeMillis();
					break;
				case Constant.MAIL_DETAIL_REQUEST:
					showDetailMail();
					lastActionTime = System.currentTimeMillis();
					break;
				case Constant.REPLY:
				case Constant.REPLY_ALL:
					getReplyToDetail(request);
					break;
				case Constant.SEARCH_AND_FILTER_REQUEST:
					searchAndFilter();
					lastActionTime = System.currentTimeMillis();
					break;
				case Constant.CHANGE_PASSWORD:
					changePassword();
					break;
				case Constant.FORGET_PASSWORD_REQUEST:
					forgetPassword();
					break;
				case Constant.AUTHORIZE_GMAIL_EMAIL_REQUEST:
					authorizeGmailAndEmail();
					break;
				default:
					break;
				}
			} catch (IOException e) {
				disconnectSocket();
			}
		}
	}

	private void login() {
		try {
			String email = dis.readUTF();
			String password = dis.readUTF();
			User user = UserBLL.login(email, password);

			if (user != null) {
				if (user.getUserID() != 0) {
					dos.writeUTF(Constant.SUCCESS);
					dos.flush();

					String response = gson.toJson(user);
//					String response = user.getUserID() + user.getEmail() + user.getName() + user.key + user.iv;
					dos.writeUTF(response);
					dos.flush();

					this._clientEmail = email;
					System.out.println(this._clientEmail + " dang ket noi"); // log
				} else {
					dos.writeUTF(Constant.WRONG_PASSWORD);
					dos.flush();
				}
			} else {
				dos.writeUTF(Constant.EMAIL_UNEXISTED);
				dos.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void changePassword() {
		try {
			String email = dis.readUTF();
			String password = dis.readUTF();
			String newPassword = dis.readUTF();
			System.out.println(password);

			String changeResult = UserBLL.changePassword(email, password, newPassword);
			dos.writeUTF(changeResult);
			dos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void authorizeGmailAndEmail() {
		try {
			String gmail = dis.readUTF();
			String email = dis.readUTF();

			boolean isTrue = UserBLL.isGmailAuthorized(gmail, email);
			if (isTrue) {
				dos.writeUTF(Constant.SUCCESS);
			} else {
				dos.writeUTF(Constant.ERROR);
			}
			dos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void forgetPassword() {
		try {
			String email = dis.readUTF();
//			String password = dis.readUTF();
			String newPassword = dis.readUTF();
//			System.out.println(password);

			String changeResult = UserBLL.forgetPassword(email, newPassword);
			dos.writeUTF(changeResult);
			dos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Ham dang ky
	private void registerAccount() {
		try {
			// Đọc email truyền về và kiểm tra email tồn tại chưa
			String[] emailRequest = dis.readUTF().split(":");
			if (emailRequest[0].equals(Constant.EMAIL)) {
				boolean isExist = UserBLL.isEmailExisted(emailRequest[1]);
				if (!isExist) {
					dos.writeUTF(Constant.EMAIL_UNEXISTED);
					dos.flush();

					this._clientEmail = emailRequest[1];
					System.out.println(this._clientEmail + " dang ket noi"); // log
				} else {
					dos.writeUTF(Constant.EMAIL_EXISTED);
					dos.flush();
					return;
				}
			} else {
				dos.writeUTF(Constant.ERROR);
				dos.flush();
				return;
			}

			// Tiếp tục đăng ký nếu email chưa tồn tại.
			String userJson = dis.readUTF(); // Đọc tiếp thông tin đăng ký
			User user = gson.fromJson(userJson, User.class);
			Object[] registerObject = UserBLL.registerUser(user); // Đăng ký
			int userID = (int) registerObject[0];
			if (userID == 0) {
				dos.writeUTF(Constant.ERROR_VALIDATE); // Báo lỗi nếu không đăng ký thành công
			} else {
				String encryptedKey = (String) registerObject[1];
				String encryptedIV = (String) registerObject[2];
				// Gui UserID kem bo key & iv di
				String response = userID + ";" + encryptedKey + ";" + encryptedIV;
				dos.writeUTF(response);
			}
			dos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void getDetailAccount() {
		try {
			String email = dis.readUTF();
			User user = UserBLL.getUserbyEmail(email);
			String json = gson.toJson(user);
			dos.writeUTF(json);
			dos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void updateAccount() {
		try {
			String json = dis.readUTF();
			User user = gson.fromJson(json, User.class);

			boolean result = UserBLL.updateUser(user);
			String sendResult = result ? Constant.SUCCESS : Constant.ERROR;
			dos.writeUTF(sendResult);
			dos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Show Mailbox
	private void getMailBox(String request) {
		try {
			int userID = Integer.parseInt(dis.readUTF());
			Object[] keyiv = UserBLL.getPrivateKeyIV(userID); // Tim cap private key-iv cua user
			// Tim danh sach thu tuong ung va gui di
			ArrayList<MailBoxDTO> listResponse = new MailBLL().getMailboxByUserID(userID, request);
			for (MailBoxDTO response : listResponse) {
				int contentMaxLength = Math.min(response.getContent().length(), 100);
				int subjectMaxLength = Math.min(response.getSubject().length(), 100);
				String content = response.getContent().substring(0, contentMaxLength);
				String subject = response.getSubject().substring(0, subjectMaxLength);
				// Ma hoa subject va content de truyen di
				content = AESEncryption.encrypt(content, (String) keyiv[0], (String) keyiv[1]);
				subject = AESEncryption.encrypt(subject, (String) keyiv[0], (String) keyiv[1]);
				response.setContent(content);
				response.setSubject(subject);

				String json = gson.toJson(response);
				sendJson(json, dos);
			}
			sendJson(Constant.SUCCESS, dos);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void alertForReceiver(int mailID, int userID) {
		System.out.println("Gui cho: " + userID);
		try {
//			dos.writeUTF(Constant.NEW_EMAIL);
//			dos.flush();
			MailBoxDTO mailBox = new MailBLL().getRowMailBox(mailID, userID);
			mailBox.setTime(mailBox.getTime().substring(0, 5));
			// Chi gui 1 phan tin nhan di
			int contentMaxLength = Math.min(mailBox.getContent().length(), 100);
			int subjectMaxLength = Math.min(mailBox.getSubject().length(), 100);
			String subject = mailBox.getSubject().substring(0, subjectMaxLength);
			String content = mailBox.getContent().substring(0, contentMaxLength);
			// Ma hoa tin nhan truoc khi gui di
			Object[] keyiv = UserBLL.getPrivateKeyIV(userID);
			subject = AESEncryption.encrypt(subject, (String) keyiv[0], (String) keyiv[1]);
			content = AESEncryption.encrypt(content, (String) keyiv[0], (String) keyiv[1]);

			mailBox.setContent(content);
			mailBox.setSubject(subject);
			// Chuyen qua json
			String json = gson.toJson(mailBox);
			byte[] sendData = new byte[1024];
			sendData = json.getBytes();
			datagramPacket.setData(sendData);
			datagramPacket.setLength(sendData.length);
			Server.sendPacket(datagramPacket);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// Tao thu moi
	private void sendNewMail() {
		try {
			// Nhan mail
			int totalLength = dis.readInt(); // Nhận tổng độ dài của chuỗi JSON
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			int remaining = totalLength;
			while (remaining > 0) {
				byte[] data = new byte[Math.min(1024, remaining)];
				int bytesRead = dis.read(data, 0, data.length);
				buffer.write(data, 0, bytesRead);
				remaining -= bytesRead;
			}
			buffer.size();
			String json = new String(buffer.toByteArray(), "UTF-8");
			MailInboxDTO newMail = gson.fromJson(json, MailInboxDTO.class);
			json = gson.toJson(newMail);

			// Tao mail moi
			HashMap<String, Integer> mapCorrectMail = new HashMap<String, Integer>();
			int mailID = new MailBLL().createMail(newMail, mapCorrectMail);

			if (mailID != 0) {
				dos.writeUTF(Constant.SUCCESS);
				dos.flush();
				Server.multicastEmail(mailID, mapCorrectMail);

				LogEmailObject logObject = new LogEmailObject(newMail.getToMail(), newMail.getCcMail(),
						newMail.getBccMail());
				String log = _clientEmail + " send an email to " + gson.toJson(logObject);
				logFile(log);
			} else {
				dos.writeUTF(Constant.ERROR);
				dos.flush();
				String log = _clientEmail + " send email error";
				logFile(log);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void setMailStatus(String request) {
		try {
			int userID = Integer.parseInt(dis.readUTF());
			String list = dis.readUTF();
			String listMailID[] = list.split(";");
			switch (request) {
			case Constant.DELETE_MAIL:
				new MailBLL().setMailStatusByMailID(listMailID, userID, Status.DELETE.getValue());
				break;
			case Constant.UNDELETE_MAIL:
				new MailBLL().setMailStatusByMailID(listMailID, userID, Status.UNREAD.getValue());
				break;
			case Constant.CHECK_READ_MAIL:
				new MailBLL().setMailStatusByMailID(listMailID, userID, Status.READ.getValue());
				break;
			case Constant.CHECK_UNREAD_MAIL:
				new MailBLL().setMailStatusByMailID(listMailID, userID, Status.UNREAD.getValue());
				break;
			case Constant.CHECK_SPAM_MAIL:
				new MailBLL().setMailStatusByMailID(listMailID, userID, Status.SPAM.getValue());
				break;
			case Constant.CHECK_UNSPAM_MAIL:
				new MailBLL().setMailStatusByMailID(listMailID, userID, Status.UNREAD.getValue());
				break;
			default:
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void showDetailMail() {
		try {
			int mailId = Integer.parseInt(dis.readUTF());
			int userId = Integer.parseInt(dis.readUTF());

			int rootID = new MailBLL().getRootMailbyMailId(mailId);
			rootID = (rootID == 0) ? mailId : rootID;
			List<MailDetailDTO> listReply = new MailBLL().getAllDetailMail(rootID, userId);// get list detail mail of
																							// rootID,
																							// where user is receiver or
																							// sender
			if (listReply != null) {
				Object[] keyiv = UserBLL.getPrivateKeyIV(userId); // Tim cap private key-iv cua user
				for (MailDetailDTO mailDetail : listReply) {
					if (!mailDetail.getMailContent().getSubject().equals(Constant.SUBJECT_DELIVERY_FAILURE)) {
						// Ma hoa subject va content de truyen di
						String content = AESEncryption.encrypt(mailDetail.getMailContent().getMailContent(),
								(String) keyiv[0], (String) keyiv[1]);
						String subject = AESEncryption.encrypt(mailDetail.getMailContent().getSubject(),
								(String) keyiv[0], (String) keyiv[1]);
						mailDetail.getMailContent().setMailContent(content);
						mailDetail.getMailContent().setSubject(subject);
					}
					String json = gson.toJson(mailDetail);
					sendJson(json, dos);
				}
			}
			sendJson(Constant.SUCCESS, dos);
		} catch (IOException e) {
			try {
				dos.writeUTF(e.getMessage());
				dos.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	private void getReplyToDetail(String request) {
		try {
			int mailID = Integer.parseInt(dis.readUTF());
			int userID = Integer.parseInt(dis.readUTF());
			MailDetailDTO detail = new MailBLL().getMailDetail(mailID, userID);
			int rootID = new MailBLL().getRootMailbyMailId(mailID); // Lay id mail goc
			detail.setMailID(rootID);
			detail.getMailContent().setMailContent(null);

			detail.setListCc(null);
			detail.setListBcc(null);
			if (request.equals(Constant.REPLY)) {
				detail.setListTo(null);
			}

			String json = gson.toJson(detail);
			dos.writeUTF(json);
			dos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void searchAndFilter() {
		try {
			String object = dis.readUTF();
			SearchDTO searchObject = gson.fromJson(object, SearchDTO.class);
			int userID = searchObject.getUserID();
			String request = searchObject.getRequest();
			String input = searchObject.getInput();
			String startDate = searchObject.getStartDate();
			String endDate = searchObject.getEndDate();
			ArrayList<MailBoxDTO> list = new MailBLL().getMailboxByUserID(userID, request);
			List<MailBoxDTO> listResponse = MailBLL.searchAndFilter(list, input, startDate, endDate);
			if (listResponse.size() != 0) {
				Object[] keyiv = UserBLL.getPrivateKeyIV(userID); // Tim cap private key-iv cua user
				for (MailBoxDTO response : listResponse) {
					int contentMaxLength = Math.min(response.getContent().length(), 100);
					int subjectMaxLength = Math.min(response.getSubject().length(), 100);
					String content = response.getContent().substring(0, contentMaxLength);
					String subject = response.getSubject().substring(0, subjectMaxLength);
					// Ma hoa subject va content de truyen di
					content = AESEncryption.encrypt(content, (String) keyiv[0], (String) keyiv[1]);
					subject = AESEncryption.encrypt(subject, (String) keyiv[0], (String) keyiv[1]);
					response.setContent(content);
					response.setSubject(subject);

					String json = gson.toJson(response);
					sendJson(json, dos);
				}
			}
			sendJson(Constant.SUCCESS, dos);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sendJson(String json, DataOutputStream dos) {
		try {
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void logFile(String content) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");
		String currentTime = formatter.format(LocalDateTime.now());
		content = "\n" + currentTime + " " + content;

		try (FileWriter writer = new FileWriter("log.txt", true)) {
			writer.write(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
