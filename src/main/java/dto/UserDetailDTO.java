package dto;

public class UserDetailDTO {
	private int userID;
	private String username;
	private String email;

	public UserDetailDTO() {
		super();
	}

	public UserDetailDTO(int userID, String username, String email) {
		super();
		this.userID = userID;
		this.username = username;
		this.email = email;
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return email + "#" + username;
	}
}
