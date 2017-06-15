package com.customer.message;

public class CustomerInsertResponse {
	
	private ResponseHeader respHeader ;
	private CustomerInsertResponseBody customerInsResBody;
	
	public ResponseHeader getRespHeader() {
		return respHeader;
	}
	public void setRespHeader(ResponseHeader respHeader) {
		this.respHeader = respHeader;
	}
	public CustomerInsertResponseBody getCustomerInsResBody() {
		return customerInsResBody;
	}
	public void setCustomerInsResBody(CustomerInsertResponseBody customerInsResBody) {
		this.customerInsResBody = customerInsResBody;
	}
	
}
