package com.customer.message;

public class CustomerUpdateResponse {
	
	private  ResponseHeader respHeader ;
	private CustomerUpdateResponseBody customerUpdateRespBody ;
	
	
	public ResponseHeader getRespHeader() {
		return respHeader;
	}
	public void setRespHeader(ResponseHeader respHeader) {
		this.respHeader = respHeader;
	}
	public CustomerUpdateResponseBody getCustomerUpdateRespBody() {
		return customerUpdateRespBody;
	}
	public void setCustomerUpdateRespBody(
			CustomerUpdateResponseBody customerUpdateRespBody) {
		this.customerUpdateRespBody = customerUpdateRespBody;
	}

}
