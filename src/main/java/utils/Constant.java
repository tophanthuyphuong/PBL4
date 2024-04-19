package utils;

public class Constant {
	// Request
	public static final String LOGIN_REQUEST = "LOGIN_REQUEST";
	public static final String SIGNUP_REQUEST = "SIGNUP_REQUEST";
	public static final String GET_ACCOUNT_REQUEST = "GET_ACCOUNT_REQUEST";
	public static final String UPDATE_ACCOUNT_REQUEST = "UPDATE_ACCOUNT_REQUEST";
	public static final String LOGOUT_REQUEST = "LOGOUT_REQUEST";
	public static final String MAIL_DETAIL_REQUEST = "MAIL_DETAIL_REQUEST";
	public static final String SEND_REQUEST = "SEND_REQUEST";
	public static final String MAILBOX_REQUEST = "MAILBOX_REQUEST";
	public static final String OUTBOX_REQUEST = "OUTBOX_REQUEST";
	public static final String ALL_REQUEST = "ALL_REQUEST";
	public static final String DELETED_REQUEST = "DELETED_REQUEST";
	public static final String SPAM_REQUEST = "SPAM_REQUEST";
	public static final String DELETE_MAIL_REQUEST = "DELETE_MAIL";
	public static final String UNDELETE_MAIL_REQUEST = "UNDELETE_MAIL";
	public static final String CHECK_READ_MAIL_REQUEST = "CHECK_READ_MAIL";
	public static final String CHECK_UNREAD_MAIL_REQUEST = "CHECK_UNREAD_MAIL";
	public static final String CHECK_SPAM_MAIL_REQUEST = "CHECK_SPAM_MAIL";
	public static final String CHECK_UNSPAM_MAIL_REQUEST = "CHECK_UNSPAM_MAIL";
	public static final String MAIL_SUBJECT_REQUEST = "MAIL_SUBJECT_REQUEST";
	public static final String SEARCH_AND_FILTER_REQUEST = "SEARCH_AND_FILTER_REQUEST";
	public static final String CHANGE_PASSWORD = "CHANGE_PASSWORD";
	public static final String FORGET_PASSWORD_REQUEST = "FORGET_PASSWORD";
	public static final String AUTHORIZE_GMAIL_EMAIL_REQUEST = "AUTHORIZE_GMAIL_EMAIL_REQUEST";
	
	// Action
	public static final String DETAIL = "detail";
	public static final String INCOMING_MAILBOX = "incoming-mail-box";
	public static final String OUTBOX = "out-box";
	public static final String ALL_MAILBOX = "all-mail-box";
	public static final String DELETED_MAILBOX = "deleted-mail-box";
	public static final String SPAM_MAILBOX = "spam-mail-box";
	public static final String LOGOUT = "logout";
	public static final String LOGIN = "login";
	public static final String SIGNUP = "signup";
	public static final String UPDATE_ACCOUNT = "update";
	public static final String SUBMIT_UPDATE_ACCOUNT = "submit-update";
	public static final String INBOX = "inbox";
	public static final String DELETE_MAIL = "delete-mail";
	public static final String UNDELETE_MAIL = "undelete-mail";
	public static final String CHECK_READ_MAIL = "check-read";
	public static final String CHECK_UNREAD_MAIL = "check-unread";
	public static final String CHECK_SPAM_MAIL = "check-spam";
	public static final String CHECK_UNSPAM_MAIL = "check-unspam";
	public static final String REPLY = "reply";
	public static final String REPLY_ALL = "reply-all";
	public static final String SEARCH_AND_FILTER = "search-and-filter";
	public static final String NEW_PASSWORD = "new-password";
	public static final String AUTHORIZE = "authorize";
	public static final String FORGET_PASSWORD = "forget-password";
	
	// Action Result
	public static final String SUBJECT_DELIVERY_FAILURE = "Delivery Status Notification (Failure)";
	public static final String EMAIL_EXISTED = "EMAIL IS EXISTED";
	public static final String EMAIL_UNEXISTED = "EMAIL IS NOT EXISTED";
	public static final String WRONG_PASSWORD = "WRONG PASSWORD";
	public static final String SUCCESS = "SUCCESS";
	public static final String ERROR = "ERROR";
	public static final String ERROR_VALIDATE = "ERROR_VALIDATE";

	public static final String MY_ACTION = "my_action";

	// SESSION
	public static final String USERID = "userId";
	public static final String USERNAME = "username";
	public static final String EMAIL = "email";
	public static final String ADDRESS = "address";
	public static final String UDPPort = "port";
	public static final String NEW_EMAIL = "NEW EMAIL";
	public static final String COUNT_UNREAD = "countUnread";
	public static final String KEY = "private_key";
	public static final String IV = "private_iv";
}
