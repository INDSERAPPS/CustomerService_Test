package com.customer.message;

public class CustomerUpdateRequest {
	
	private CustomerUpdateRequestBody customerUpdateReqBody ;
	
	private MessageRequestHeader requestHeader ;
	
	public CustomerUpdateRequestBody getCustomerUpdateReqBody() {
		return customerUpdateReqBody;
	}
	public void setCustomerUpdateReqBody(
			CustomerUpdateRequestBody customerUpdateReqBody) {
		this.customerUpdateReqBody = customerUpdateReqBody;
	}
	public MessageRequestHeader getRequestHeader() {
		return requestHeader;
	}
	public void setRequestHeader(MessageRequestHeader requestHeader) {
		this.requestHeader = requestHeader;
	}
	

}
