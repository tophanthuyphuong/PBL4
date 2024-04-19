package enums;

public enum ReceiveType {
	TO(1), CC(2), BCC(3);

	private final int value;

	private ReceiveType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
