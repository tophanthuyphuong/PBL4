package enums;

public enum Status {
	UNREAD(1), READ(2), DELETE(3), SPAM(4);

	private final int value;

	private Status(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static String getStatus(int value) {
		String s = "";
		switch (value) {
		case 1:
			s = "UNREAD";
			break;
		case 2:
			s = "READ";
			break;
		case 3:
			s = "DELETE";
			break;
		case 4:
			s = "SPAM";
			break;
		}
		return s;
	}
}
