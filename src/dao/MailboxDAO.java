package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.MailBox;
import utils.ConnectSQL;

public class MailboxDAO {
	private Connection cnn;

	public MailboxDAO() {
		cnn = ConnectSQL.getConnection();
	}
	public boolean insertMailBox(MailBox mailBox) {
		String query = "insert into mailbox values (?, ?, ?)";
		try {
			PreparedStatement p = cnn.prepareStatement(query);
			p.setInt(1, mailBox.getUserID());
			p.setInt(2, mailBox.getMailID());
			p.setInt(3, mailBox.getStatus());
			int row = p.executeUpdate();
			if(row !=0) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		ConnectSQL.disconnect();
		return false;
	}
	
	public boolean updateStatus(MailBox mailBox) {
		String query = "UPDATE mailbox SET Status = ? "
				+ "WHERE (mailbox.MailID, mailbox.UserID) IN ( "
				+ "    SELECT mailbox.MailID, mailbox.UserID "
				+ "    FROM mailbox"
				+ "    INNER JOIN send ON send.MailID = mailbox.MailID "
				+ "    WHERE mailbox.UserID = ? AND ReplyTo = ?)";
		try {
			PreparedStatement p = cnn.prepareStatement(query);
			p.setInt(1, mailBox.getStatus());
			p.setInt(2, mailBox.getUserID());
			p.setInt(3, mailBox.getMailID()); // rootID
			int row = p.executeUpdate();
			if(row !=0) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		ConnectSQL.disconnect();
		return false;
	}
	
	public synchronized int getStatus(int mailID, int userID) {
		String query = "select Status from mailbox where UserID = ? and MailID = ?";
		int status = 0;
		try {
			PreparedStatement p = cnn.prepareStatement(query);
			p.setInt(1, userID);
			p.setInt(2, mailID);
			ResultSet res = p.executeQuery();
			while(res.next()) {
				 status = res.getInt("Status");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		ConnectSQL.disconnect();
		return status;
	}
	//Lay tat ca mail
	public ArrayList<Integer> getAllMailByUserID(int userID, int status) {
		ArrayList<Integer> list = new ArrayList<Integer>();
//		String query = "SELECT MAX(SubqueryAlias.MailID) as MailID FROM "
//			    + "(select mailbox.MailID from mailbox "
//			    + "JOIN send ON send.MailID = mailbox.MailID "
//			    + "WHERE where mailbox.UserID = ? and mailbox.Status = ?) AS SubqueryAlias "
//			    + "GROUP BY SubqueryAlias.ReplyTo"; 
		String query;
		// status = 2 => lay tat ca
		if (status != 2) {
			query = "SELECT MAX(SubqueryAlias.MailID) as MailID FROM "
				    + "(select mailbox.MailID, send.ReplyTo from mailbox "
				    + "JOIN send ON send.MailID = mailbox.MailID "
				    + "WHERE mailbox.UserID = ? and mailbox.Status = ?) AS SubqueryAlias "
				    + "GROUP BY SubqueryAlias.ReplyTo"; 
		} else {
			query = "SELECT MAX(SubqueryAlias.MailID) as MailID FROM "
				    + "(select mailbox.MailID, send.ReplyTo from mailbox "
				    + "JOIN send ON send.MailID = mailbox.MailID "
				    + "WHERE mailbox.UserID = ? and mailbox.Status <= ?) AS SubqueryAlias "
				    + "GROUP BY SubqueryAlias.ReplyTo"; 
		}
		try {
			PreparedStatement p = cnn.prepareStatement(query);
			p.setInt(1, userID);
			p.setInt(2, status);
			ResultSet res = p.executeQuery();
			
//			if (res.isBeforeFirst())
//				list = new ArrayList<Integer>();
			
			while(res.next()) {
				list.add(res.getInt("MailID"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ConnectSQL.disconnect();
		return list;
	}
	
	//Lay tat ca mail tru mail SPAM va DELETE
		public ArrayList<Integer> getAllMailWithoutDeleteAndSpamByUserID(int userID) {
			ArrayList<Integer> list = null;
			String query = "select MailID from mailbox where UserID = ? and Status = 3";
			try {
				PreparedStatement p = cnn.prepareStatement(query);
				p.setInt(1, userID);
				p.setInt(2, userID);
				p.setInt(3, userID);
				ResultSet res = p.executeQuery();
				
				if (res.isBeforeFirst())
					list = new ArrayList<Integer>();
				
				while(res.next()) {
					list.add(res.getInt("MailID"));
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ConnectSQL.disconnect();
			return list;
		}
	
	public ArrayList<Integer> getAllDeletedMailByUserID(int userID) {
		ArrayList<Integer> list = null;
		String query = "select MailID from mailbox where UserID = ? and Status = 3";
		try {
			PreparedStatement p = cnn.prepareStatement(query);
			p.setInt(1, userID);
			p.setInt(2, userID);
			p.setInt(3, userID);
			ResultSet res = p.executeQuery();
			
			if (res.isBeforeFirst())
				list = new ArrayList<Integer>();
			
			while(res.next()) {
				list.add(res.getInt("MailID"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ConnectSQL.disconnect();
		return list;
	}
	
	public ArrayList<Integer> getAllSpamMailByUserID(int userID) {
		ArrayList<Integer> list = null;
		String query = "select MailID from mailbox where UserID = ? and Status = 4 ";
		try {
			PreparedStatement p = cnn.prepareStatement(query);
			p.setInt(1, userID);
			p.setInt(2, userID);
			p.setInt(3, userID);
			ResultSet res = p.executeQuery();
			
			if (res.isBeforeFirst())
				list = new ArrayList<Integer>();
			
			while(res.next()) {
				list.add(res.getInt("MailID"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ConnectSQL.disconnect();
		return list;
	}
	
	
}
