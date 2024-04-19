package model;

import java.util.ArrayList;

public class MailDTO {
	private int mailID;
	private int sendID;
	private int replyTo;
	private ArrayList<Receive> receiveID;
	private MailContent content;
	
	public MailDTO() {}

	public MailDTO(int mailID, int sendID, ArrayList<Receive> receiveID, MailContent content) {
		super();
		this.mailID = mailID;
		this.sendID = sendID;
		this.receiveID = receiveID;
		this.content = content;
	}

	public int getMailID() {
		return mailID;
	}

	public void setMailID(int mailID) {
		this.mailID = mailID;
	}

	public int getSendID() {
		return sendID;
	}

	public void setSendID(int sendID) {
		this.sendID = sendID;
	}

	public ArrayList<Receive> getReceiveID() {
		return receiveID;
	}

	public void setReceiveID(ArrayList<Receive> receiveID) {
		this.receiveID = receiveID;
	}

	public MailContent getContent() {
		return content;
	}

	public void setContent(MailContent content) {
		this.content = content;
	}

	public int getReplyTo() {
		return replyTo;
	}

	public void setReplyTo(int replyTo) {
		this.replyTo = replyTo;
	}
}
