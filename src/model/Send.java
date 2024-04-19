package model;

public class Send {
	private int mailID;
	private int userID;
	private int replyTo;
	
	public Send () {}

	public Send(int mailID, int userID, int replyTo) {
		super();
		this.mailID = mailID;
		this.userID = userID;
		this.replyTo = replyTo;
	}

	public int getMailID() {
		return mailID;
	}

	public void setMailID(int mailID) {
		this.mailID = mailID;
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public int getReplyTo() {
		return replyTo;
	}

	public void setReplyTo(int replyTo) {
		this.replyTo = replyTo;
	}
}
