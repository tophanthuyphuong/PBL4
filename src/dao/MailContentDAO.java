package dao;

import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import model.MailContent;
import utils.ConnectSQL;

public class MailContentDAO {
	private Connection cnn;

	public MailContentDAO() {
		cnn = ConnectSQL.getConnection();
	}
	public boolean insertMailContent(MailContent mail) {
		String query = "insert into mailcontent values (null,?,?,?,?,?)";
		try {
			PreparedStatement p = cnn.prepareStatement(query);
			p.setInt(1, mail.getMailID());
			p.setString(2, mail.getSubject());
			p.setString(3, mail.getMailContent());
			p.setString(4, mail.getAttachment());
			p.setTimestamp(5, mail.getTime());
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
	public synchronized MailContent getMailContentByMailID(int mailID) {
		MailContent mailContent = null;
		String query = "SELECT * FROM mailcontent where MailID = ?;";

		try {
			PreparedStatement p = cnn.prepareStatement(query);
			p.setInt(1, mailID);
			ResultSet res = p.executeQuery();


			while(res.next()) {
			    String subject = res.getString("Subject");
			    String attachment = res.getString("Attachment");
			    Timestamp time = res.getTimestamp("Time");

			    Reader reader = res.getCharacterStream("MailContent");
			    StringBuilder mailContentBuilder = new StringBuilder();
			    char[] buffer = new char[1024];
			    int bytesRead;
			    while ((bytesRead = reader.read(buffer)) != -1) {
			        mailContentBuilder.append(buffer, 0, bytesRead);
			    }
			    String content = mailContentBuilder.toString();

			    mailContent = new MailContent(mailID, subject, content, attachment, time);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ConnectSQL.disconnect();
		return mailContent;
	}
}
