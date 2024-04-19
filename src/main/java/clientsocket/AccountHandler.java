//package clientsocket;
//
//import java.io.DataInputStream;
//import java.io.DataOutputStream;
//import java.io.IOException;
//import java.net.Socket;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//import com.google.gson.Gson;
//
//import dto.User;
//import utils.Constant;
//
//public class AccountHandler {
//	private Socket socket;
//	private DataInputStream dis;
//	private DataOutputStream dos;
//	private Gson gson;
//	public AccountHandler(String _email) {
//		socket = ClientSocket.getInstance(_email);
//		try {
//			dis = new DataInputStream(socket.getInputStream());
//			dos = new DataOutputStream(socket.getOutputStream());
//			gson = new Gson();
//		} catch (IOException e) {
//			ClientSocket.disconnectSocket();
//			e.printStackTrace();
//		}
//	}
//	
//	public Object[] getUserbyEmailandPassword(String email, String password) {
//		User user = null;
//		String result = null;
//		try {
//			dos.writeUTF(Constant.LOGIN_REQUEST);
//			dos.flush();
//			dos.writeUTF(email);
//			dos.flush();
//			dos.writeUTF(password);
//			dos.flush();
//			
//			result = dis.readUTF();
//			if (result.equals(Constant.SUCCESS)) {
//				String json = dis.readUTF();
//				String[] userStr = json.split(";");
//				
//				user = new User();
//				user.setUserId(Integer.parseInt(userStr[0]));
//				user.setName(userStr[1]);
//				user.setEmail(userStr[2]);
//			}
//		} catch (Exception e) {
//			ClientSocket.disconnectSocket();
//			e.printStackTrace();
//		}
//		return new Object[] { result, user};
//	}
//	
//	public String registerAccount (User user) {
//		String res = Constant.ERROR;
//		try {
//			dos.writeUTF(Constant.SIGNUP_REQUEST);
//			dos.flush();
//			dos.writeUTF("email:" + user.getEmail());
//			dos.flush();
//			
//			res = dis.readUTF();
//			if (res.equals(Constant.EMAIL_UNEXISTED)) {
//				String json = gson.toJson(user);
//				dos.writeUTF(json);
//				dos.flush();
//				
//				res = dis.readUTF();
//			}
//		} catch (Exception e) {
//			ClientSocket.disconnectSocket();
//			e.printStackTrace();
//		}
////		Cac gia tri res co the nhan: EMAIL_EXISTED, ERROR, userID
//		return res;
//	}
//	
//
//	public User getDetailAccount(String email) {
//		User user = null;
//		try {
//			dos.writeUTF(Constant.GET_ACCOUNT_REQUEST);
//			dos.flush();
//			dos.writeUTF(email);
//			dos.flush();
//			
//			String json = dis.readUTF();
//			user = gson.fromJson(json, User.class);
//			
//		    if (user.getBirth() != null) {
//		    	SimpleDateFormat inputDateFormat = new SimpleDateFormat("MMM dd, yyyy");
//			    SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//		    	Date date = inputDateFormat.parse(user.getBirth());
//			    String formattedDate = outputDateFormat.format(date);
//			    user.setBirth(formattedDate);
//		    }
//		} catch (Exception e) {
//			ClientSocket.disconnectSocket();
//			e.printStackTrace();
//		}
//		return user;
//	}
//	
//	public boolean updateAccount(User user) {
//		try {
//			dos.writeUTF(Constant.UPDATE_ACCOUNT_REQUEST);
//			dos.flush();
//			
//			String json = gson.toJson(user);
//			dos.writeUTF(json);
//			dos.flush();
//			
//			String result = dis.readUTF();
//			if (result.equals(Constant.SUCCESS))
//				return true;
//		} catch (Exception e) {
//			ClientSocket.disconnectSocket();
//			e.printStackTrace();
//		}
//		return false;
//	}
//	
//	public void logout () {
//		try {
//			dis.close();
//			dos.close();
//			ClientSocket.disconnectSocket();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//	}
//}
package clientsocket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;

import com.google.gson.Gson;

import dto.User;
import utils.Constant;

public class AccountHandler {
	private Socket socket;
	private DataInputStream dis;
	private DataOutputStream dos;
	private Gson gson;
	private String sessionID;

	public AccountHandler(String sessionID, String _email) {
		this.sessionID = sessionID;
		socket = ClientSocket.getInstance(sessionID, _email);
		try {
			dis = new DataInputStream(socket.getInputStream());
			dos = new DataOutputStream(socket.getOutputStream());
			gson = new Gson();
		} catch (IOException e) {
			ClientSocket.disconnectSocket(sessionID);
			e.printStackTrace();
		}
	}

	public Object[] getUserbyEmailandPassword(String email, String password) throws ServletException {
		User user = null;
		String result = null;
		try {
			dos.writeUTF(Constant.LOGIN_REQUEST);
			dos.flush();
			dos.writeUTF(email);
			dos.flush();
			dos.writeUTF(password);
			dos.flush();

			result = dis.readUTF();
			if (result.equals(Constant.SUCCESS)) {
				String json = dis.readUTF();
				user = gson.fromJson(json, User.class);
			}
		} catch (SocketTimeoutException e) {
			ClientSocket.disconnectSocket(sessionID);
			System.err.println("Hệ thống chờ hồi đáp quá lâu");
            throw new ServletException("Kết nối với hệ thống gặp vấn đề");
        } catch (Exception e) {
			ClientSocket.disconnectSocket(sessionID);
			e.printStackTrace();
		}
		return new Object[] { result, user };
	}

	public String registerAccount(User user) throws ServletException {
		String res = Constant.ERROR;
		try {
			dos.writeUTF(Constant.SIGNUP_REQUEST);
			dos.flush();
			dos.writeUTF(Constant.EMAIL + ":" + user.getEmail());
			dos.flush();

			res = dis.readUTF();
			if (res.equals(Constant.EMAIL_UNEXISTED)) {
				String json = gson.toJson(user);
				dos.writeUTF(json);
				dos.flush();

				res = dis.readUTF();
			}
		} catch (SocketTimeoutException e) {
			ClientSocket.disconnectSocket(sessionID);
			System.err.println("Hệ thống chờ hồi đáp quá lâu");
            throw new ServletException("Kết nối với hệ thống gặp vấn đề");
        } catch (Exception e) {
			ClientSocket.disconnectSocket(sessionID);
			e.printStackTrace();
		}
//		Cac gia tri res co the nhan: EMAIL_EXISTED, ERROR, userID
		return res;
	}

	public boolean authorizeGmailAndEmail(String gmail, String email) throws ServletException {
		boolean result = false;
		try {
			dos.writeUTF(Constant.AUTHORIZE_GMAIL_EMAIL_REQUEST);
			dos.flush();
			dos.writeUTF(gmail);
			dos.flush();
			dos.writeUTF(email);
			dos.flush();

			String res = dis.readUTF();
			if (res.equals(Constant.SUCCESS)) {
				result = true;
			} 
		} catch (SocketTimeoutException e) {
			ClientSocket.disconnectSocket(sessionID);
			System.err.println("Hệ thống chờ hồi đáp quá lâu");
            throw new ServletException("Kết nối với hệ thống gặp vấn đề");
        } catch (Exception e) {
			ClientSocket.disconnectSocket(sessionID);
			e.printStackTrace();
		}
//		Cac gia tri res co the nhan: EMAIL_EXISTED, ERROR, userID
		return result;
	}
	
	
	public User getDetailAccount(String email) throws ServletException {
		User user = null;
		try {
			dos.writeUTF(Constant.GET_ACCOUNT_REQUEST);
			dos.flush();
			dos.writeUTF(email);
			dos.flush();

			String json = dis.readUTF();
			user = gson.fromJson(json, User.class);

			if (user.getBirth() != null) {
				SimpleDateFormat inputDateFormat = new SimpleDateFormat("MMM dd, yyyy");
				SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
				Date date = inputDateFormat.parse(user.getBirth());
				String formattedDate = outputDateFormat.format(date);
				user.setBirth(formattedDate);
			}
		} catch (SocketTimeoutException e) {
			ClientSocket.disconnectSocket(sessionID);
			System.err.println("Hệ thống chờ hồi đáp quá lâu");
            throw new ServletException("Kết nối với hệ thống gặp vấn đề");
        } catch (Exception e) {
			ClientSocket.disconnectSocket(sessionID);
			e.printStackTrace();
		}
		return user;
	}

	public boolean updateAccount(User user) throws ServletException {
		try {
			dos.writeUTF(Constant.UPDATE_ACCOUNT_REQUEST);
			dos.flush();

			String json = gson.toJson(user);
			dos.writeUTF(json);
			dos.flush();

			String result = dis.readUTF();
			if (result.equals(Constant.SUCCESS))
				return true;
		} catch (SocketTimeoutException e) {
			ClientSocket.disconnectSocket(sessionID);
			System.err.println("Hệ thống chờ hồi đáp quá lâu");
            throw new ServletException("Kết nối với hệ thống gặp vấn đề");
        } catch (Exception e) {
			ClientSocket.disconnectSocket(sessionID);
			e.printStackTrace();
		}
		return false;
	}

	public void logout() {
		try {
			dis.close();
			dos.close();
			ClientSocket.disconnectSocket(sessionID);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public String changePassword(String email, String password, String newPassword) {
		String result = null;
		try {
			dos.writeUTF(Constant.CHANGE_PASSWORD);
			dos.flush();
			dos.writeUTF(email);
			dos.flush();
			dos.writeUTF(password);
			dos.flush();
			dos.writeUTF(newPassword);
			dos.flush();
//			System.out.println(password);
//			System.out.println(newPassword);
			result = dis.readUTF();
		} catch(Exception e) {
			ClientSocket.disconnectSocket(sessionID);
			e.printStackTrace();
		}
		return result;
	}
	
	public String forgetPassword(String email, String newPassword) {
		String result = null;
		try {
			dos.writeUTF(Constant.FORGET_PASSWORD_REQUEST);
			dos.flush();
			dos.writeUTF(email);
			dos.flush();
			dos.writeUTF(newPassword);
			dos.flush();
//			System.out.println(password);
//			System.out.println(newPassword);
			result = dis.readUTF();
		} catch(Exception e) {
			ClientSocket.disconnectSocket(sessionID);
			e.printStackTrace();
		}
		return result;
	}
}
