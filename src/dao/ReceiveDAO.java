package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.Receive;
import utils.ConnectSQL;

public class ReceiveDAO {
	private Connection cnn;

	public ReceiveDAO() {
		cnn = ConnectSQL.getConnection();
	}

	public boolean insertRecieve(Receive receive) {
		String query = "insert into receive (UserID, MailID, isTo, isCC, isBCC) values (?,?,?,?,?)";
		try {
			PreparedStatement p = cnn.prepareStatement(query);
			p.setInt(1, receive.getUserID());
			p.setInt(2, receive.getMailID());
			p.setBoolean(3, receive.isTo());
			p.setBoolean(4, receive.isCC());
			p.setBoolean(5, receive.isBCC());
			int rowsAffected = p.executeUpdate();
			if (rowsAffected > 0) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectSQL.disconnect();
		}
		return false;
	}

	public boolean findInReceive(int UserID, int MailID) {
		// true: chua co
		// false da ton tai
		boolean check = false;
		String query = "select count(*) from receive where UserID = ? and MailID = ?";
		try {
			PreparedStatement p = cnn.prepareStatement(query);
			p.setInt(1, UserID);
			p.setInt(2, MailID);
			ResultSet res = p.executeQuery();
			while (res.next()) {
				if (res.getInt(1) == 0)
					check = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectSQL.disconnect();
		}
		return check;
	}

	public ArrayList<Receive> getAllReceiveByMailID(int mailID) {
		ArrayList<Receive> list = null;
		String query = "SELECT * FROM receive where MailID = ?;";
		try {
			PreparedStatement p = cnn.prepareStatement(query);
			p.setInt(1, mailID);
			ResultSet res = p.executeQuery();

			if (res.isBeforeFirst())
				list = new ArrayList<Receive>();

			while (res.next()) {
				Receive receive = new Receive(mailID, res.getInt("UserID"), res.getBoolean("isTo"),
						res.getBoolean("isCC"), res.getBoolean("isBCC"));
				list.add(receive);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectSQL.disconnect();
		}
		return list;
	}

	
		// Tra ve danh sach mailId trong hop thu den cua user
		public ArrayList<Integer> getAllReceiveByUserID(int userID) {
			ArrayList<Integer> list = new ArrayList<Integer>();
			
			String query = "select max(SubqueryAlias.MailID) as MailID from "
					+ "(select receive.MailID, send.ReplyTo from receive "
					+ "join mailbox on receive.MailID = mailbox.MailID and receive.UserID = mailbox.UserID "
					+ "join send on receive.MailID = send.MailID "
					+ "where receive.UserID = ? and Status <= 2) as SubqueryAlias "
					+ "group by SubqueryAlias.ReplyTo";
			try {
				PreparedStatement p = cnn.prepareStatement(query);
				p.setInt(1, userID);
				ResultSet res = p.executeQuery();
				
				while(res.next()) {
					list.add(res.getInt("MailID"));
				}
			} catch (SQLException e) { 
				e.printStackTrace();
			} finally {
				ConnectSQL.disconnect();
			}
		return list;
	}

	public ArrayList<Integer> getAllReceiveWithoutDeleteAndSpamByID(int userID) {
		ArrayList<Integer> list = null;
		String query = "SELECT * from mailbox WHERE Status in (1,2) and UserID = ? and MailID in "
				+ "(SELECT send.MailID FROM send INNER JOIN receive WHERE receive.UserID = ? AND send.MailID = receive.MailID "
				+ "and (send.ReplyTo IS NULL OR (send.ReplyTo IS NOT NULL AND (receive.MailID, send.ReplyTo) "
				+ "IN (SELECT MAX(receive.MailID), send.ReplyTo FROM send INNER join receive "
				+ "WHERE receive.UserID = ? and receive.MailID = send.MailID GROUP BY ReplyTo))))";
		try {
			PreparedStatement p = cnn.prepareStatement(query);
			p.setInt(1, userID);
			p.setInt(2, userID);
			p.setInt(3, userID);
			ResultSet res = p.executeQuery();

			if (res.isBeforeFirst())
				list = new ArrayList<Integer>();

			while (res.next()) {
				list.add(res.getInt("MailID"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectSQL.disconnect();
		}
		return list;
	}
}
