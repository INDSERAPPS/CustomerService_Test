package com.customer.message;

public class CustomerGetRequest {
	
	private MessageRequestHeader customerGetReqHeader ;
	private CustomerGetRequestBody customerGetReqBody ;
	public MessageRequestHeader getCustomerGetReqHeader() {
		return customerGetReqHeader;
	}
	public void setCustomerGetReqHeader(MessageRequestHeader customerGetReqHeader) {
		this.customerGetReqHeader = customerGetReqHeader;
	}
	public CustomerGetRequestBody getCustomerGetReqBody() {
		return customerGetReqBody;
	}
	public void setCustomerGetReqBody(CustomerGetRequestBody customerGetReqBody) {
		this.customerGetReqBody = customerGetReqBody;
	}
	
	

}
