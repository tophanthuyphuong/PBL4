package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.User;
import model.UserDetailDTO;
import utils.ConnectSQL;

public class UserDAO {
	private Connection cnn;
	public UserDAO() {
		cnn = ConnectSQL.getConnection();
	}
	
	public User getUserbyEmail(String email) {
		String query = "select * from user where Email = ?";
		User user = null;
		try {
			PreparedStatement p = cnn.prepareStatement(query);
			p.setString(1, email);
			ResultSet res = p.executeQuery();
			while(res.next()) {
				user = new User();
				user.setUserID(res.getInt("UserID"));
				user.setEmail(res.getString("Email"));
				user.setName(res.getString("Name"));
				user.setPassword(res.getString("Password"));
				user.setPhone(res.getString("Phone"));
				user.setBirth(res.getDate("Birth"));
				user.setGender(res.getBoolean("Gender"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		ConnectSQL.disconnect();
		return user;
	}
	
	public Object[] getPrivateKeyIV(int userID) {
		String key = null, iv = null;
		String query = "select * from user where UserID = ?";
		try {
			PreparedStatement p = cnn.prepareStatement(query);
			p.setInt(1, userID);
			ResultSet res = p.executeQuery();
			while(res.next()) {
				key = res.getString("PrivateKey");
				iv = res.getString("IV");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		ConnectSQL.disconnect();
		return new Object[] {key, iv};
	}
	
//	public User getUserbyAccount(String email, String password) {
//		User user = null;
//		String query = "select * from user where Email = ? and Password = ?";
//		try {
//			PreparedStatement p = cnn.prepareStatement(query);
//			p.setString(1, email);
//			p.setString(2, password);
//			ResultSet res = p.executeQuery();
//			while(res.next()) {
//				user = new User();
//				user.setUserID(res.getInt("UserID"));
//				user.setName(res.getString("Name"));
//				user.setEmail(res.getString("Email"));
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		ConnectSQL.disconnect();
//		return user;
//	}
	public synchronized String getNameByUserID(int userID) {
		String query = "select Name from user where UserID = ?";
		String name = null;
		try {
			PreparedStatement p = cnn.prepareStatement(query);
			p.setInt(1, userID);
			ResultSet res = p.executeQuery();
			while(res.next()) {
				name = res.getString("Name");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		ConnectSQL.disconnect();
		return name;
	}
	
	public UserDetailDTO getUserByUserID(int userID) {
		String query = "select Name, Email from user where UserID = ?";
		UserDetailDTO user = null;
		try {
			PreparedStatement p = cnn.prepareStatement(query);
			p.setInt(1, userID);
			ResultSet res = p.executeQuery();
			while(res.next()) {
				user = new UserDetailDTO(userID, res.getString("Name"), res.getString("Email"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		ConnectSQL.disconnect();
		return user;
	}
	
	public int insertUser(User user, String key, String iv) {
		String query = "insert into user(Email, Password, Name, Birth, Phone, PrivateKey, IV) "
						+ "values (?,?,?,?,?,?,?)";
		int userID = 0;
		try {
			PreparedStatement p = cnn.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS);
			p.setString(1, user.getEmail());
			p.setString(2, user.getPassword());
			p.setString(3, user.getName());
			p.setDate(4, user.getBirth());
			p.setString(5, user.getPhone());
			p.setString(6, key);
			p.setString(7, iv);

			int row = p.executeUpdate();
			if (row != 0) {
				ResultSet generatedKeys = p.getGeneratedKeys();
				if (generatedKeys.next()) {
					userID = generatedKeys.getInt(1);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		ConnectSQL.disconnect();
		return userID;
	}
	
	public boolean updateUser(User user) {
		try {
			String query = "update user set Name=?, Phone=?, Birth=?, Gender=? where Email=?";
			PreparedStatement p = cnn.prepareStatement(query);
			p.setString(1, user.getName());
			p.setString(2, user.getPhone());
			p.setDate(3, user.getBirth());
			p.setBoolean(4, user.getGender());
			System.out.println(user.getGender());
			p.setString(5, user.getEmail());

			p.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		ConnectSQL.disconnect();
		return false;
	}
	public boolean updatePassword(String email, String newPassword) {
		try {
			System.out.println(email + " " + newPassword);
			String query = "update user set Password = ? where Email=?";
			PreparedStatement p = cnn.prepareStatement(query);
			p.setString(2, email);
			p.setString(1, newPassword);
			p.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		ConnectSQL.disconnect();
		return false;
	}
	
	public boolean isGmailAuthorized(String gmail, String email) {
		try {
			String query = "select * from user where Gmail=? and Email=?";
			PreparedStatement p = cnn.prepareStatement(query);
			p.setString(1, gmail);
			p.setString(2, email);
			
			ResultSet rs = p.executeQuery();
			if (rs.next()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		ConnectSQL.disconnect();
		return false;
	}
}
