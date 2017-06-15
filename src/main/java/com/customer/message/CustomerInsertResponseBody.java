package com.customer.message;

public class CustomerInsertResponseBody {
	
	private ResponseStatus respStatus ;
	private String customerId ;
	private ErrorDetails error ;
	
	
	public ResponseStatus getRespStatus() {
		return respStatus;
	}
	public void setRespStatus(ResponseStatus respStatus) {
		this.respStatus = respStatus;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public ErrorDetails getError() {
		return error;
	}
	public void setError(ErrorDetails error) {
		this.error = error;
	}
	

}
