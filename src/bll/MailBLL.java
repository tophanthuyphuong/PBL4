package bll;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dao.MailContentDAO;
import dao.MailboxDAO;
import dao.ReceiveDAO;
import dao.SendDAO;
import dao.UserDAO;
import model.MailBox;
import model.MailBoxDTO;
import model.MailContent;
import model.MailDetailDTO;
import model.MailInboxDTO;
import model.Receive;
import model.Send;
import model.User;
import model.UserDetailDTO;
import utils.AESEncryption;
import utils.Constant;
import utils.Status;

public class MailBLL {
	// Show Mailbox by request
	public ArrayList<MailBoxDTO> getMailboxByUserID(int userID, String request) {
		ArrayList<MailBoxDTO> listResponse = new ArrayList<MailBoxDTO>();
		ArrayList<Integer> listMailID = null;

		switch (request) {
		case Constant.MAILBOX_REQUEST:
			listMailID = new ReceiveDAO().getAllReceiveByUserID(userID);
			break;
		case Constant.OUTBOX_REQUEST:
			listMailID = new SendDAO().getAllSendByID(userID);
			break;
		case Constant.DELETED_REQUEST:
			listMailID = new MailboxDAO().getAllMailByUserID(userID, Status.DELETE.getValue());
			break;
		case Constant.SPAM_REQUEST:
			listMailID = new MailboxDAO().getAllMailByUserID(userID, Status.SPAM.getValue());
			break;
		case Constant.ALL_REQUEST:
			listMailID = new MailboxDAO().getAllMailByUserID(userID, 2);
			break;
		default:
			return null;
		}

		if (listMailID != null) {
			for (int mailID : listMailID) {
				MailBoxDTO mailbox = getRowMailBox(mailID, userID);
				listResponse.add(mailbox);
			}
		}

		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
		Collections.sort(listResponse, (s1, s2) -> {
			try {
				Date date1 = dateFormat.parse(s1.getTime());
				Date date2 = dateFormat.parse(s2.getTime());
				int dateComparison = date2.compareTo(date1);
				if (dateComparison == 0) {
					// Nếu ngày bằng nhau, sắp xếp theo thứ tự giảm dần của trường id
					return s2.getMailID() - s1.getMailID();
				}
				return dateComparison;
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return 0;
		});

		return listResponse;
	}

	public MailBoxDTO getRowMailBox(int mailID, int userID) {
		int sendID = new SendDAO().getSendByMailID(mailID).getUserID();
		String sendName = new UserDAO().getNameByUserID(sendID);

		int rootID = getRootMailbyMailId(mailID);
		rootID = (rootID == 0) ? mailID : rootID;
		int countReply = new SendDAO().getReplyCountByMailID(rootID, userID);

		MailContent content = new MailContentDAO().getMailContentByMailID(mailID);

		int status = new MailboxDAO().getStatus(mailID, userID);

		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
		MailBoxDTO mailbox = new MailBoxDTO(mailID, sendName, countReply, dateFormat.format(content.getTime()),
				content.getSubject(), content.getMailContent(), status);
		mailbox.setRootID(rootID);
		return mailbox;
	}

	public static List<MailBoxDTO> searchAndFilter(ArrayList<MailBoxDTO> listMailbox, String input,
			String startDateString, String endDateString) {
		List<MailBoxDTO> listResponse = new ArrayList<>();
		boolean isNull = false;
		if (startDateString.equals("") && endDateString.equals(""))
			isNull = true;
		if (listMailbox != null) {
			for (MailBoxDTO mailBox : listMailbox) {
				String lowerInput = input.toLowerCase();
				if ((mailBox.getContent()).toLowerCase().contains(lowerInput)
						|| (mailBox.getSubject()).toLowerCase().contains(lowerInput)) {
					if (!isNull) {
						SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
						SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
						Date startDate = new Date();
						Date endDate = new Date();
						Date mailDate = new Date();
						try {
							startDate = inputFormat.parse(startDateString);
							endDate = inputFormat.parse(endDateString);
							//mailDate = dateFormat.parse(mailBox.getTime());
							startDate = dateFormat.parse(dateFormat.format(startDate));
							endDate = dateFormat.parse(dateFormat.format(endDate));
							mailDate = dateFormat.parse(mailBox.getTime().substring(6));
						} catch (ParseException e) {
							e.printStackTrace();
						}

						if (startDate.compareTo(mailDate) <= 0 && endDate.compareTo(mailDate) >= 0)
							listResponse.add(mailBox);
					} else
						listResponse.add(mailBox);
				}
			}
		}
		return listResponse;
	}

	@SuppressWarnings("unchecked")
	public int createMail(MailInboxDTO newMail, HashMap<String, Integer> mapCorrectMail) {
		Send newSend = new Send(0, newMail.getSendId(), newMail.getReplyTo());
		SendDAO sendDAO = new SendDAO();
		int mailID = sendDAO.insertSend(newSend); // Tao bang ghi send

		if (mailID != 0) {
			// Giai ma subject & content
			Object[] keyiv = new UserDAO().getPrivateKeyIV(newMail.getSendId());
			System.out.println("Send, sendid:" + newMail.getSendId());
			String content = AESEncryption.decrypt(newMail.getContentMail(), (String) keyiv[0], (String) keyiv[1]);
			String subject = AESEncryption.decrypt(newMail.getSubjectMail(), (String) keyiv[0], (String) keyiv[1]);
			System.out.println("Send, subject:" + subject);
			// Tao bang ghi mailcontent
			MailContent mailcontent = new MailContent(mailID, subject, content, newMail.getAttachment(),
					new Timestamp(System.currentTimeMillis()));
			boolean inserted = new MailContentDAO().insertMailContent(mailcontent);
			if (!inserted) {
				sendDAO.deleteSend(mailID);
				return 0;
			}

			// Tao bang ghi Receiver & Mailbox
			// Insert list receiver(by email). If receiver email is not available --> add
			// into listErrorMail
			Object[] lists = insertReceiveUsers(mailID, newMail.getSendId(), newMail.getToMail(), newMail.getCcMail(),
					newMail.getBccMail());
			List<String> listErrorMail = (List<String>) lists[0];
			mapCorrectMail.putAll((HashMap<String, Integer>) lists[1]);
			if (listErrorMail.size() != 0) {
				int rootID = (newMail.getReplyTo() != 0) ? newMail.getReplyTo() : mailID;
				sendMailError(rootID, newMail.getSendId(), listErrorMail); // Neu co mail khong ton tai, gui thu loi
			}
		}

		return mailID;
	}

	private void sendMailError(int rootMailID, int userID, List<String> listErrorMail) {
		// Table SEND
		Send newErrorMail = new Send(0, 1, rootMailID); // UserID = 1: Admin userID
		int mailErrorID = new SendDAO().insertSend(newErrorMail);
		// Table MAILCONTENT
		StringBuilder builder = new StringBuilder();
		for (String string : listErrorMail) {
			builder.append(string).append(", ");
		}
		builder.delete(builder.length() - 2, builder.length() - 1);
		MailContent mailErrorContent = new MailContent(mailErrorID, Constant.SUBJECT_DELIVERY_FAILURE,
				builder.toString(), null, new Timestamp(System.currentTimeMillis()));
		new MailContentDAO().insertMailContent(mailErrorContent);
		// Table RECEIVE
		Receive receive = new Receive(mailErrorID, userID, true, false, false);
		new ReceiveDAO().insertRecieve(receive);
		// Table MAILBOX
		MailBox mailBox = new MailBox(mailErrorID, userID, Status.UNREAD.getValue());
		new MailboxDAO().insertMailBox(mailBox);
	}

	private List<Integer> getListReceiveId(List<String> listStr, HashMap<String, Integer> listErrorMail,
			HashMap<String, Integer> listReceiver) {
		List<Integer> listID = null;
		if (listStr != null) {
			listID = new ArrayList<Integer>();

			for (String mail : listStr) {
				User userTo = new UserDAO().getUserbyEmail(mail);
				if (userTo != null) {
					listID.add(userTo.getUserID());

					if (!listReceiver.containsKey(mail)) {
						listReceiver.put(mail, userTo.getUserID());
					}
				} else {
					if (!listErrorMail.containsKey(mail)) {
						listErrorMail.put(mail, 0);
					}
				}
			}
		}
		return listID;
	}

	// Insert row of table receiver and maibox
	private Object[] insertReceiveUsers(int mailID, int sendId, List<String> listToMail, List<String> listCcMail,
			List<String> listBccMail) {
		HashMap<String, Integer> mapErrorMail = new HashMap<String, Integer>(); // List email that is not available in
																				// database
		HashMap<String, Integer> mapReceiver = new HashMap<String, Integer>();
		List<Receive> receives = new ArrayList<Receive>();
		List<Integer> listToID = getListReceiveId(listToMail, mapErrorMail, mapReceiver);
		List<Integer> listCcID = getListReceiveId(listCcMail, mapErrorMail, mapReceiver);
		List<Integer> listBccID = getListReceiveId(listBccMail, mapErrorMail, mapReceiver);

		// Loop for listToID and add into list receives
		if (listToID != null) {
			for (Integer userID : listToID) {
				receives.add(new Receive(mailID, userID, true, false, false));
			}
		}

		// Loop for CcID
		// If list receivers contains ccID, then update variable isCC = true
		// If not, add into list receives
		if (listCcID != null) {
			for (Integer userID : listCcID) {
				boolean found = false;
				for (Receive receive : receives) {
					if (userID == receive.getUserID()) {
						receive.setCC(true);
						found = true;
						break;
					}
				}
				if (!found) {
					receives.add(new Receive(mailID, userID, false, true, false));
				}
			}
		}

		// Loop for BccID
		// If list receivers contains BccID, then update variable isBcc = true
		// If not, add into list receives
		if (listBccID != null) {
			for (Integer userID : listBccID) {
				boolean found = false;
				for (Receive receive : receives) {
					if (userID == receive.getUserID()) {
						receive.setBCC(true);
						found = true;
						break;
					}
				}
				if (!found) {
					receives.add(new Receive(mailID, userID, false, false, true));
				}
			}
		}

		for (Receive receive : receives) {
			new ReceiveDAO().insertRecieve(receive);
		}

		MailboxDAO mailboxDAO = new MailboxDAO();
		// Tao hop trang thai
		boolean isSend = false;
		for (Receive receive : receives) {
			if (receive.getUserID() == sendId)
				isSend = true;
			MailBox mailBox = new MailBox(mailID, receive.getUserID(), Status.UNREAD.getValue());
			mailboxDAO.insertMailBox(mailBox);
		}

		// Tao mailBox cho nguoi gui
		if (!isSend) {
			MailBox mailBox = new MailBox(mailID, sendId, Status.READ.getValue());
			mailboxDAO.insertMailBox(mailBox);
		}

		List<String> listErrorMail = new ArrayList<>(mapErrorMail.keySet());
		return new Object[] { listErrorMail, mapReceiver };
	}

	public void setMailStatusByMailID(String mailID[], int userID, int status) {
		if (mailID != null) {
			for (int i = 0; i < mailID.length; i++) {
				// List<Integer> replyList = new
				// SendDAO().getAllMailDetailbyMailID(Integer.parseInt(mailID[i]));
				int rootID = getRootMailbyMailId(Integer.parseInt(mailID[i]));
				new MailboxDAO().updateStatus(new MailBox(rootID, userID, status));
				// replyList.forEach((n) -> new MailboxDAO().updateStatus(new MailBox(n, userID,
				// status)));
			}
		}
	}

	// GetAllDetailMail
	public List<MailDetailDTO> getAllDetailMail(int rootMailID, int UserID) {
		List<MailDetailDTO> listMail = null;
		List<Integer> listIdMailDetail = new SendDAO().getAllMailDetailbyMailID(rootMailID);
		// Chay va lay ra tung mail 1 trong cuoc tro chuyen
		if (listIdMailDetail != null) {
			listMail = new ArrayList<MailDetailDTO>();
			for (Integer mailId : listIdMailDetail) {
				MailDetailDTO mailDetail = getMailDetail(mailId, UserID);
				if (mailDetail != null) {
					// ... Ma hoa ben handler

					// Đánh dọc đã đọc
					int status = new MailBLL().getStatusByMailID(mailDetail.getMailID(), UserID);
					if (status == Status.UNREAD.getValue())
						checkReadMail(mailDetail.getMailID(), UserID);

					listMail.add(mailDetail);
				}
			}
		}
		return listMail;
	}

	// Find mailDetail: check if user is receiver or sender. If true, return this
	// mailDetail. If not, return null.
	public MailDetailDTO getMailDetail(int mailID, int userID) {
		MailDetailDTO mailDetail = null;
		Send send = new SendDAO().getSendByMailID(mailID);

		if (send != null) {
			boolean isExist = send.getUserID() == userID;
			// isExist: variable check if user is receiver or sender
			UserDetailDTO sendUser = new UserDAO().getUserByUserID(send.getUserID());
			// Danh sach nguoi nhan theo to, cc, bcc
			List<UserDetailDTO> listTo = new ArrayList<UserDetailDTO>();
			List<UserDetailDTO> listCc = new ArrayList<UserDetailDTO>();
			List<UserDetailDTO> listBcc = new ArrayList<UserDetailDTO>();
			// Danh sach tất cả người nhận
			List<Receive> listReceives = new ReceiveDAO().getAllReceiveByMailID(mailID);
			// Đánh dấu loại người nhận
			if (listReceives != null) {
				for (Receive receive : listReceives) {
					if (!isExist && receive.getUserID() == userID)
						isExist = true;
					if (receive.isTo()) {
						listTo.add(new UserDAO().getUserByUserID(receive.getUserID()));
					}
					if (receive.isCC()) {
						listCc.add(new UserDAO().getUserByUserID(receive.getUserID()));
					}
					if (receive.isBCC()) {
						listBcc.add(new UserDAO().getUserByUserID(receive.getUserID()));
					}
				}
			}

			// Nếu như mail đó là mail nhận hoặc gửi của user --> tiếp tục xử lý
			if (isExist) {
				MailContent mailContent = new MailContentDAO().getMailContentByMailID(mailID);
				// isInbox: check whether userID (the one who see mail detail) and the one who
				// RECEIVE the mail is the same
				// if true: "tôi"
				boolean checkReceive = false;
				if (listReceives != null) {
					for (Receive receive : listReceives) {
						if (userID == receive.getUserID()) {
							checkReceive = true;
							break;
						}
					}
				}
//				mailDetail = new MailDetailDTO(mailID, send.getReplyTo(), sendUser, listTo, listCc, listBcc, mailContent, userID == send.getUserID());
				mailDetail = new MailDetailDTO(mailID, send.getReplyTo(), sendUser, listTo, listCc, listBcc,
						mailContent, checkReceive);
			}
		}
		return mailDetail;
	}

	public ArrayList<Integer> getOutbox(List<Integer> listMailID, int userID) {
		List<Integer> result = new ArrayList<>();
		Map<Integer, Boolean> map = new HashMap<Integer, Boolean>();

		for (int mailID : listMailID) {
			int rootID = getRootMailbyMailId(mailID);
			rootID = (rootID == 0) ? mailID : rootID;
			if (!map.containsKey(rootID)) {
				map.put(rootID, true);
			}
		}
		List<Integer> listRootID = new ArrayList<Integer>(map.keySet());
		for (int rootMailID : listRootID) {
			int mailID = rootMailID;
			List<Integer> listReply = new SendDAO().getAllMailDetailbyMailID(rootMailID);
//			for (int replyID : listReply) {
//				Send send = new SendDAO().getSendByMailID(replyID);
//				if (send.getUserID() == userID)
//					mailID = replyID;
//			}

			for (int i = listReply.size() - 1; i >= 0; i--) {
				int replyID = listReply.get(i);
				Send send = new SendDAO().getSendByMailID(replyID);
				if (send.getUserID() == userID) {
					mailID = replyID;
					break;
				}
			}
			result.add(mailID);
		}
		return (ArrayList<Integer>) result;
	}

	public ArrayList<Integer> getMailbox(List<Integer> listMailID, int userID) {
		List<Integer> result = new ArrayList<>();
		Map<Integer, Boolean> map = new HashMap<Integer, Boolean>();

		for (int mailID : listMailID) {
			int rootID = getRootMailbyMailId(mailID);
			rootID = (rootID == 0) ? mailID : rootID;
			if (!map.containsKey(rootID)) {
				map.put(rootID, true);
			}
		}
		List<Integer> listRootID = new ArrayList<Integer>(map.keySet());
		for (int rootMailID : listRootID) {
			List<MailDetailDTO> listReply = getAllDetailMail(rootMailID, userID);
//			try {
			result.add(listReply.get(listReply.size() - 1).getMailID());
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
		}
		return (ArrayList<Integer>) result;
	}

	public int getRootMailbyMailId(int mailID) {
		Send send = new SendDAO().getSendByMailID(mailID);
		return (send != null) ? send.getReplyTo() : mailID; // if rootId = null then send.getReplyTo = 0
	}

	public void checkReadMail(int mailID, int userId) {
		int rootID = getRootMailbyMailId(mailID);
		new MailboxDAO().updateStatus(new MailBox(rootID, userId, Status.READ.getValue()));
	}

	public int getStatusByMailID(int mailID, int userID) {
		return new MailboxDAO().getStatus(mailID, userID);
	}
}
