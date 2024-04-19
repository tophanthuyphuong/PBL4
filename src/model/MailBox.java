package model;

public class MailBox {
	private int userID;
	private int mailID;
	private int status;
	
	public MailBox() { }
	
	public MailBox(int mailID, int userID, int status) {
		super();
		this.userID = userID;
		this.mailID = mailID;
		this.status = status;
	}
	public int getUserID() {
		return userID;
	}
	public void setUserID(int userID) {
		this.userID = userID;
	}
	public int getMailID() {
		return mailID;
	}
	public void setMailID(int mailID) {
		this.mailID = mailID;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
	
}
