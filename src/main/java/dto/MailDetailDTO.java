package dto;

import java.util.List;

public class MailDetailDTO {
	private int mailID;
	private int replyTo;
	private UserDetailDTO sender;
	private List<UserDetailDTO> listTo;
	private List<UserDetailDTO> listCc;
	private List<UserDetailDTO> listBcc;
	private MailContent mailContent;
	private String attachmentName;

	public String getAttachmentName() {
		return attachmentName;
	}

	public void setAttachmentName(String attachmentName) {
		this.attachmentName = attachmentName;
	}

	private boolean isInbox; // hop thu den?

	public MailDetailDTO() {
		super();
	}

	public MailDetailDTO(int mailID, int replyTo, UserDetailDTO sender, List<UserDetailDTO> listTo,
			List<UserDetailDTO> listCc, List<UserDetailDTO> listBcc, MailContent mailContent, boolean isInbox) {
		super();
		this.mailID = mailID;
		this.replyTo = replyTo;
		this.sender = sender;
		this.listTo = listTo;
		this.listCc = listCc;
		this.listBcc = listBcc;
		this.mailContent = mailContent;
		this.isInbox = isInbox;
	}

	public int getMailID() {
		return mailID;
	}

	public void setMailID(int mailID) {
		this.mailID = mailID;
	}

	public int getReplyTo() {
		return replyTo;
	}

	public void setReplyTo(int replyTo) {
		this.replyTo = replyTo;
	}

	public UserDetailDTO getSender() {
		return sender;
	}

	public void setSender(UserDetailDTO sender) {
		this.sender = sender;
	}

	public List<UserDetailDTO> getListTo() {
		return listTo;
	}

	public void setListTo(List<UserDetailDTO> listTo) {
		this.listTo = listTo;
	}

	public List<UserDetailDTO> getListCc() {
		return listCc;
	}

	public void setListCc(List<UserDetailDTO> listCc) {
		this.listCc = listCc;
	}

	public List<UserDetailDTO> getListBcc() {
		return listBcc;
	}

	public void setListBcc(List<UserDetailDTO> listBcc) {
		this.listBcc = listBcc;
	}

	public MailContent getMailContent() {
		return mailContent;
	}

	public void setMailContent(MailContent mailContent) {
		this.mailContent = mailContent;
	}

	public boolean isInbox() {
		return isInbox;
	}

	public void setInbox(boolean isInbox) {
		this.isInbox = isInbox;
	}
}
