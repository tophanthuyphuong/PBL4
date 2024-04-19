package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Send;
import utils.ConnectSQL;

public class SendDAO {
	private Connection cnn;

	public SendDAO() {
		cnn = ConnectSQL.getConnection();
	}

	public int insertSend(Send send) {
		String query = "insert into send values (null,?,?)";
		try {
			PreparedStatement p = cnn.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS);
			p.setInt(1, send.getUserID());
//			if (send.getReplyTo() != 0) {
			p.setInt(2, send.getReplyTo());
//			} else {
//				p.setNull(2, java.sql.Types.INTEGER);
//			}
			int row = p.executeUpdate();
			
			if (row != 0) {
				ResultSet generatedKeys = p.getGeneratedKeys();
				if (generatedKeys.next()) {
					int mailID = generatedKeys.getInt(1);
					if (send.getReplyTo() == 0) {
						query = "update send set ReplyTo = ? where MailID = ?";
						p = cnn.prepareStatement(query);
						p.setInt(1, mailID);
						p.setInt(2, mailID);
						p.executeUpdate();
					}
					return mailID;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectSQL.disconnect();
		}
		return 0;
	}
	
	public void deleteSend(int mailID) {
		String query = "delete from send where MailID=?";
		try {
			PreparedStatement p = cnn.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS);
			p.setInt(1, mailID);
			p.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectSQL.disconnect();
		}
	}

	public Send getSendByMailID(int mailID) {
		Send send = null;
		String query = "SELECT * FROM send where MailID = ?;";
		try {
			PreparedStatement p = cnn.prepareStatement(query);
			p.setInt(1, mailID);
			ResultSet res = p.executeQuery();
			while (res.next()) {
				send = new Send(res.getInt("MailID"), res.getInt("UserID"), res.getInt("ReplyTo"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			ConnectSQL.disconnect();
		}
		return send;
	}

	// Tra ve danh sach mailId trong hop thu da gui cua user
	public ArrayList<Integer> getAllSendByID(int userID) {
		ArrayList<Integer> list = new ArrayList<Integer>();
//		String query = "SELECT MailID from send WHERE UserID = ? and MailID NOT IN (SELECT ReplyTo from send where ReplyTo is not null)"
//				+ " and ReplyTo is null " + "or (UserID = ? and ReplyTo IS NOT NULL AND (MailID, ReplyTo) "
//				+ "IN (SELECT MAX(MailID), ReplyTo FROM send WHERE UserID = ? GROUP BY ReplyTo))"
//				+ "or UserID = 1 and ReplyTo in (SELECT MailID from send WHERE UserID = ?)";
		String query = "SELECT MAX(SubqueryAlias.MailID) as MailID FROM "
			    + "(SELECT send.MailID, send.ReplyTo FROM send "
			    + "JOIN mailbox ON send.MailID = mailbox.MailID AND send.UserID = mailbox.UserID "
			    + "WHERE send.UserID = ? AND mailbox.Status <= 2) AS SubqueryAlias "
			    + "GROUP BY SubqueryAlias.ReplyTo"; 



		try {
			PreparedStatement p = cnn.prepareStatement(query);
			p.setInt(1, userID);
//			p.setInt(2, userID);
//			p.setInt(3, userID);
//			p.setInt(4, userID);
			ResultSet res = p.executeQuery();

			while (res.next()) {
				list.add(res.getInt("MailID"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			ConnectSQL.disconnect();
		}
		return list;
	}

	// Lay cac mail cho hop thu da gui nhung khac DELETE va SPAM
	public ArrayList<Integer> getAllSendWithoutDeleteAndSpamByID(int userID) {
		ArrayList<Integer> list = null;
		String query = "SELECT MailID from mailbox WHERE Status in (1,2) and UserID = ? and MailID in "
				+ "(SELECT MailID FROM send WHERE UserID = ? AND (ReplyTo IS NULL OR (ReplyTo IS NOT NULL AND (MailID, ReplyTo) "
				+ "IN (SELECT MAX(MailID), ReplyTo FROM send WHERE UserID = ? GROUP BY ReplyTo))))";
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			ConnectSQL.disconnect();
		}
		return list;
	}
	// Tra ve mailID goc cua mail Reply

	public int getRootMailByMailID(int mailID) {
		int rootID = 0;
		String query = "select ReplyTo from send where MailID = ?";
		try {
			PreparedStatement p = cnn.prepareStatement(query);
			p.setInt(1, mailID);
			ResultSet res = p.executeQuery();
			while (res.next()) {
				rootID = res.getInt("ReplyTo");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			ConnectSQL.disconnect();
		}
		return (rootID == 0) ? mailID : rootID;
	}

	// Kiem tra so mail reply bang mailID
	public synchronized int getReplyCountByMailID(int mailID, int userID) {
		int count = 0;
		// String query = "select count(MailID) from send where ReplyTo = (SELECT
		// ReplyTo from send WHERE MailID = ?) and UserID = ?";
		String query = "SELECT COUNT(send.MailID) "
				+ "from send INNER join mailbox on send.MailID = mailbox.MailID "
				+ "WHERE mailbox.UserID = ? AND ReplyTo = ?";
		try {
			PreparedStatement p = cnn.prepareStatement(query);
			p.setInt(1, userID);
			p.setInt(2, mailID);
			ResultSet res = p.executeQuery();
			while (res.next()) {
				count = res.getInt(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			ConnectSQL.disconnect();
		}
		return count;
	}

	public List<Integer> getAllMailDetailbyMailID(int mailID) {
		List<Integer> list = null;
		String query = "SELECT MailID FROM Send WHERE ReplyTo = " + mailID + " OR MailID = " + mailID;
		try {
			Statement statement = cnn.createStatement();
			ResultSet res = statement.executeQuery(query);

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
