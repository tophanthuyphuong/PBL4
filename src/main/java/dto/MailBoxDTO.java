package dto;

public class MailBoxDTO {
	private int rootID;
	private int mailID;
	private String sendName;
	private int count;
	private String time;
	private String subject;
	private String content;
	private int status;

	public MailBoxDTO() {
		super();
	}

	public MailBoxDTO(int mailID, String sendName, int count, String time, String subject, String content, int status) {
		super();
		this.mailID = mailID;
		this.sendName = sendName;
		this.setCount(count);
		this.time = time;
		this.subject = subject;
		this.content = content;
		this.status = status;
	}

	public int getMailID() {
		return mailID;
	}

	public void setMailID(int mailID) {
		this.mailID = mailID;
	}

	public String getSendName() {
		return sendName;
	}

	public void setSendName(String sendName) {
		this.sendName = sendName;
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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getStatus() {
		return status;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getRootID() {
		return rootID;
	}

	public void setRootID(int rootID) {
		this.rootID = rootID;
	}
}
