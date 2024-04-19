package utils;
public class ClientIden {
	private String email;
	private String address;
	private int port;
	public ClientIden(String email, String address, int port) {
		super();
		this.email = email;
		this.address = address;
		this.port = port;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
}
