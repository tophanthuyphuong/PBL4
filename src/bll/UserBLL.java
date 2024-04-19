package bll;

import java.util.regex.Pattern;

import dao.UserDAO;
import model.User;
import utils.AESEncryption;
import utils.Constant;

public class UserBLL {
	public static User login(String email, String password) {
		if (!validateEmail(email)) return null;
		User user = new UserDAO().getUserbyEmail(email); // Kiểm tra email tồn tại không
		if (user != null) {
			String defaultKey = AESEncryption.getDefaultKey();
			String defaultIV = AESEncryption.getDefaultIV();
			String defaultDecryptPass = AESEncryption.decrypt(password, defaultKey, defaultIV);
			// Lay cap private key-iv
			Object[] object = getPrivateKeyIV(user.getUserID());
			String privateEncryptPass = AESEncryption.encrypt(defaultDecryptPass, (String)object[0], (String)object[1]);
			boolean isPassCorrect = privateEncryptPass.equals(user.getPassword()); // So sánh password voi pass private key
			
			if (isPassCorrect) {
				// Ma hoa private key-iv bang defaul key-iv
				String key = AESEncryption.encrypt((String)object[0], defaultKey, defaultIV);
				String iv = AESEncryption.encrypt((String)object[1], defaultKey, defaultIV);
				
				user.setKey(key);
				user.setIv(iv);
				user.setBirth(null);
				user.setPassword(null);
				user.setPhone(null);
			} else {
				user = new User(); // Wrong pass
				user.setUserID(0);
			}
		}
		return user;
	}
	public static String changePassword(String email, String password, String newPassword ) {
		String result = null;
		User user = new UserDAO().getUserbyEmail(email);
		if (user != null) {
			//password = AESEncryption.decrypt(password, (String)keyiv[0], (String)keyiv[1]);
			boolean isPassCorrect = password.equals(user.getPassword());
			
			if (!isPassCorrect) {
				result = Constant.WRONG_PASSWORD;
			} else {
				System.out.println(password);
				Object[] keyiv = UserBLL.getPrivateKeyIV(user.getUserID());
				// newPassword = AESEncryption.decrypt(newPassword, (String)keyiv[0], (String)keyiv[1]); // ra ma hoa bang Default
				String newPassValid = AESEncryption.decrypt(newPassword, (String)keyiv[0], (String)keyiv[1]);
				Boolean isValidated = validatePassword(newPassValid);
				
				if (isValidated) {
					boolean isChanged = new UserDAO().updatePassword(email, newPassword);
					if (isChanged)
						result = Constant.SUCCESS;
				} else {
					result = Constant.ERROR_VALIDATE;
				}
			}
		}
		return result;
	}
	
	public static boolean isGmailAuthorized(String gmail, String email) {
		return new UserDAO().isGmailAuthorized(gmail, email);
	}
	
	public static String forgetPassword(String email, String newPassword ) {
		String result = null;
		System.out.println(email + " -- " + newPassword);
		User user = new UserDAO().getUserbyEmail(email);
		if(user != null) {
			// Lay cap key & iv mac dinh
			String defaultKey = AESEncryption.getDefaultKey();
			String defaultIV = AESEncryption.getDefaultIV();
			String password = AESEncryption.decrypt(newPassword, defaultKey, defaultIV); // pass that
			Boolean isValidated = validatePassword(password);
			
			if (isValidated) {
				Object[] keyiv = UserBLL.getPrivateKeyIV(user.getUserID());
				String encryptedPass = AESEncryption.encrypt(password, (String)keyiv[0], (String)keyiv[1]); // pass ma hoa private
				boolean isChanged = new UserDAO().updatePassword(email, encryptedPass);
				if (isChanged)
					result = Constant.SUCCESS;
			} else {
				result = Constant.ERROR_VALIDATE;
			}
		}
//		if (user != null) {
//			//password = AESEncryption.decrypt(password, (String)keyiv[0], (String)keyiv[1]);
//			boolean isPassCorrect = password.equals(user.getPassword());
//			
//			if (!isPassCorrect) {
//				result = Constant.WRONG_PASSWORD;
//			} else {
//				System.out.println(password);
//				Object[] keyiv = UserBLL.getPrivateKeyIV(user.getUserID());
//				// newPassword = AESEncryption.decrypt(newPassword, (String)keyiv[0], (String)keyiv[1]); // ra ma hoa bang Default
//				String newPassValid = AESEncryption.decrypt(newPassword, (String)keyiv[0], (String)keyiv[1]);
//				Boolean isValidated = validatePassword(newPassValid);
//				
//				if (isValidated) {
//					boolean isChanged = new UserDAO().updatePassword(email, newPassword);
//					if (isChanged)
//						result = Constant.SUCCESS;
//				} else {
//					result = Constant.ERROR_VALIDATE;
//				}
//			}
//		}
		
		return result;
	}
	public static boolean isEmailExisted(String email) {
		User user = new UserDAO().getUserbyEmail(email);
		if (user != null) return true;
		return false;
	}
	
	public static Object[] registerUser(User user) {
		// Lay cap key & iv mac dinh
		String defaultKey = AESEncryption.getDefaultKey();
		String defaultIV = AESEncryption.getDefaultIV();
		// Tao ngau nhien cap key & iv
		String key = AESEncryption.generateRandomKey();
		String iv = AESEncryption.generateRandomIV();
		// Kiem tra dinh dang password
		String password = AESEncryption.decrypt(user.getPassword(), defaultKey, defaultIV); // pass that
		boolean validePass = validatePassword(password);
		
		int userID = 0; // Neu validePass = false --> userID = 0 
		if (validePass) {
			String encryptedPass = AESEncryption.encrypt(password, key, iv); // pass ma hoa private
			user.setPassword(encryptedPass);
			
			userID = new UserDAO().insertUser(user, key, iv);
			// Ma hoa Key va IV de gui len
			key = AESEncryption.encrypt(key, defaultKey, defaultIV);
			iv = AESEncryption.encrypt(iv, defaultKey, defaultIV);
		}
		return new Object[] { userID, key, iv};
	}
	
	private static boolean validatePassword (String password) {
		 String pattern = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])[0-9a-zA-Z]{8,}$";
	     return Pattern.matches(pattern, password);
	}
	
	private static boolean validateEmail (String email) {
		 String pattern = "^\\w+@meowmail.vn$";
	     return Pattern.matches(pattern, email);
	}
	
	public static User getUserbyEmail(String email) {
		User user = new UserDAO().getUserbyEmail(email);
		return user;
	}
	
	public static boolean updateUser(User user) {
		return new UserDAO().updateUser(user);
	}
	public static Object[] getPrivateKeyIV(int userID) {
		return new UserDAO().getPrivateKeyIV(userID);
	}
}
