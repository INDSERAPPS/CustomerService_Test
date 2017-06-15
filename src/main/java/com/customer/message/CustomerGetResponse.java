package com.customer.message;

public class CustomerGetResponse {

	private ResponseHeader respHeader ;
	private CustomerGetResponseBody customerGetResBody;
	public ResponseHeader getRespHeader() {
		return respHeader;
	}
	public void setRespHeader(ResponseHeader respHeader) {
		this.respHeader = respHeader;
	}
	public CustomerGetResponseBody getCustomerGetResBody() {
		return customerGetResBody;
	}
	public void setCustomerGetResBody(CustomerGetResponseBody customerGetResBody) {
		this.customerGetResBody = customerGetResBody;
	}
	
	
	
}
