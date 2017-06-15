package com.customer.message;

public class CustomerUpdateResponseBody {
	
	private ResponseStatus respStatus ;
	private ErrorDetails errorDetails ;
	
	public ResponseStatus getRespStatus() {
		return respStatus;
	}
	public void setRespStatus(ResponseStatus respStatus) {
		this.respStatus = respStatus;
	}
	public ErrorDetails getErrorDetails() {
		return errorDetails;
	}
	public void setErrorDetails(ErrorDetails errorDetails) {
		this.errorDetails = errorDetails;
	}

}
