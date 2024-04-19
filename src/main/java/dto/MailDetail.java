package dto;

import java.util.List;

public class MailDetail {
	private int mailId;
	private int sendId;
	private int replyTo;
	private List<String> toMail;
	private List<String> ccMail;
	private List<String> bccMail;
	private String subjectMail;
	private String contentMail;
	private String attachment;

	public String getAttachment() {
		return attachment;
	}

	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}

	public MailDetail() {
		super();
	}

	public MailDetail(int sendId, String subjectMail, String contentMail) {
		super();
		this.sendId = sendId;
		this.subjectMail = subjectMail;
		this.contentMail = contentMail;
	}

	public MailDetail(int mailId, int sendId, int replyTo, List<String> toMail, List<String> ccMail,
			List<String> bccMail, String subjectMail, String contentMail, String attachment) {
		super();
		this.mailId = mailId;
		this.sendId = sendId;
		this.replyTo = replyTo;
		this.toMail = toMail;
		this.ccMail = ccMail;
		this.bccMail = bccMail;
		this.subjectMail = subjectMail;
		this.contentMail = contentMail;
		this.attachment = attachment;
	}

	public int getMailId() {
		return mailId;
	}

	public void setMailId(int mailId) {
		this.mailId = mailId;
	}

	public List<String> getToMail() {
		return toMail;
	}

	public void setToMail(List<String> toMail) {
		this.toMail = toMail;
	}

	public List<String> getCcMail() {
		return ccMail;
	}

	public void setCcMail(List<String> ccMail) {
		this.ccMail = ccMail;
	}

	public List<String> getBccMail() {
		return bccMail;
	}

	public void setBccMail(List<String> bccMail) {
		this.bccMail = bccMail;
	}

	public String getSubjectMail() {
		return subjectMail;
	}

	public void setSubjectMail(String subjectMail) {
		this.subjectMail = subjectMail;
	}

	public String getContentMail() {
		return contentMail;
	}

	public void setContentMail(String contentMail) {
		this.contentMail = contentMail;
	}

	public int getSendId() {
		return sendId;
	}

	public void setSendId(int sendId) {
		this.sendId = sendId;
	}

	public int getReplyTo() {
		return replyTo;
	}

	public void setReplyTo(int replyTo) {
		this.replyTo = replyTo;
	}

}
