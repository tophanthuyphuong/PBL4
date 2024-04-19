package model;

import java.sql.Date;

public class User {
	private int userID;
	private String email;
	private String password;
	private String name;
	private String phone;
	private String gmail;
	private Date birth;
	private boolean gender;
	private String key;
	private String iv;
	
	public User() {
		userID = 0;
	}
	public User(int userID, String email, String password, String name, String phone, Date birth, String gmail) {
		super();
		this.userID = userID;
		this.email = email;
		this.password = password;
		this.name = name;
		this.phone = phone;
		this.birth = birth;
		this.gmail = gmail;
	}
	public int getUserID() {
		return userID;
	}
	public void setUserID(int userID) {
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
	public Date getBirth() {
		return birth;
	}
	public void setBirth(Date birth) {
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
