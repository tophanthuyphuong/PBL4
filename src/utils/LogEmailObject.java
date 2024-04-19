package utils;

import java.util.List;

public class LogEmailObject {
	private List<String> listTo;
	private List<String> listCC;
	private List<String> listBCC;
	public LogEmailObject(List<String> listTo, List<String> listCC, List<String> listBCC) {
		super();
		this.listTo = listTo;
		this.listCC = listCC;
		this.listBCC = listBCC;
	}

	public List<String> getListTo() {
		return listTo;
	}
	public void setListTo(List<String> listTo) {
		this.listTo = listTo;
	}
	public List<String> getListCC() {
		return listCC;
	}
	public void setListCC(List<String> listCC) {
		this.listCC = listCC;
	}
	public List<String> getListBCC() {
		return listBCC;
	}
	public void setListBCC(List<String> listBCC) {
		this.listBCC = listBCC;
	}
}
