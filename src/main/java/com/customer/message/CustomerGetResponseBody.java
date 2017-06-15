package com.customer.message;

public class CustomerGetResponseBody {
	
	private ResponseStatus status;
	private String customerid;
	private String name;
	private String email;
	private String accounttype;
	private String isenabled;
	private ErrorDetails error;
	public ResponseStatus getStatus() {
		return status;
	}
	public void setStatus(ResponseStatus status) {
		this.status = status;
	}
	public String getCustomerid() {
		return customerid;
	}
	public void setCustomerid(String customerid) {
		this.customerid = customerid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAccounttype() {
		return accounttype;
	}
	public void setAccounttype(String accounttype) {
		this.accounttype = accounttype;
	}
	public String getIsenabled() {
		return isenabled;
	}
	public void setIsenabled(String isenabled) {
		this.isenabled = isenabled;
	}
	public ErrorDetails getError() {
		return error;
	}
	public void setError(ErrorDetails error) {
		this.error = error;
	}
	 
	

}
