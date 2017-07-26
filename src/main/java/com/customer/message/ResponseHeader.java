package com.customer.message;

public class ResponseHeader {
	
	private String contentType ;
	private String acceptCharSet ;
	private String date ;
	private String ci ;
	
	private String appName;
	
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public String getAcceptCharSet() {
		return acceptCharSet;
	}
	public void setAcceptCharSet(String acceptCharSet) {
		this.acceptCharSet = acceptCharSet;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getCi() {
		return ci;
	}
	public void setCi(String ci) {
		this.ci = ci;
	}
	
	

}
