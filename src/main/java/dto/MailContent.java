package dto;

import java.security.Timestamp;
import java.text.SimpleDateFormat;

public class MailContent {
	private int mailID;
	private String time;
	private String subject;
	private String mailContent;
	private String attachment;

	public MailContent() {
	}

	public MailContent(int mailID, String subject, String mailContent, String attachment, String time) {
		super();
		this.mailID = mailID;
		this.time = time;
		this.subject = subject;
		this.mailContent = mailContent;
		this.attachment = attachment;
	}

	public int getMailID() {
		return mailID;
	}

	public void setMailID(int mailID) {
		this.mailID = mailID;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMailContent() {
		return mailContent;
	}

	public void setMailContent(String mailContent) {
		this.mailContent = mailContent;
	}

	public String getAttachment() {
		return attachment;
	}

	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}

	@Override
	public String toString() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String timeSend = dateFormat.format(time);
		return subject + "#" + mailContent + "#" + timeSend;
	}
}
