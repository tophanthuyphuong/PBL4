package model;

public class Receive {
	private int mailID;
	private int userID;
	private boolean isTo;
	private boolean isCC;
	private boolean isBCC;
	
	public Receive() {}
	public Receive(int mailID, int userID, boolean isTo, boolean isCC, boolean isBCC) {
		super();
		this.mailID = mailID;
		this.userID = userID;
		this.isTo = isTo;
		this.isCC = isCC;
		this.isBCC = isBCC;
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
	public boolean isTo() {
		return isTo;
	}
	public void setTo(boolean isTo) {
		this.isTo = isTo;
	}
	public boolean isCC() {
		return isCC;
	}
	public void setCC(boolean isCC) {
		this.isCC = isCC;
	}
	public boolean isBCC() {
		return isBCC;
	}
	public void setBCC(boolean isBCC) {
		this.isBCC = isBCC;
	}
}
