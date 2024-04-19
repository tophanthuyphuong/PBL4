package dto;

public class User {
	private int userID;
	private String email;
	private String password;
	private String name;
	private String phone;
	private String gmail;
	private boolean gender;
	private String birth;
	private String key;
	private String iv;

	public User() {
	}

	public User(int userID, String email, String password) {
		super();
		this.userID = userID;
		this.email = email;
		this.password = password;
	}
	public User(String email, String password, String name, String phone, String birth, String gmail) {
		super();
		this.email = email;
		this.password = password;
		this.name = name;
		this.phone = phone;
		this.birth = birth;
	}

	public User(String email, String password, String name, String phone, String birth, boolean gender) {
		super();
		this.email = email;
		this.password = password;
		this.name = name;
		this.phone = phone;
		this.birth = birth;
		this.gender = gender;
	}

	public int getUserId() {
		return userID;
	}

	public void setUserId(int userID) {
		this.userID = userID;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getBirth() {
		return birth;
	}

	public void setBirth(String birth) {
		this.birth = birth;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getIv() {
		return iv;
	}

	public void setIv(String iv) {
		this.iv = iv;
	}

	public boolean getGender() {
		return gender;
	}

	public void setGender(boolean gender) {
		this.gender = gender;
	}

	public String getGmail() {
		return gmail;
	}

	public void setGmail(String gmail) {
		this.gmail = gmail;
	}
}
