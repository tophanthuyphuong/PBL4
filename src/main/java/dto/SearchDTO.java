package dto;

public class SearchDTO {
	private int userID;
	private String request;
	private String input;
	private String startDate;
	private String endDate;

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public String getRequest() {
		return request;
	}

	public SearchDTO(int userID, String request, String input, String startDate, String endDate) {
		super();
		this.userID = userID;
		this.request = request;
		this.input = input;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
}
