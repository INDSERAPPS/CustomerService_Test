package com.customer.message;

public class CustomerInsertRequest {
	
	private MessageRequestHeader customerInsReqHeader ;
	private CustomerInsertRequestBody customerInsReqBody ;
	
	public MessageRequestHeader getCustomerInsReqHeader() {
		return customerInsReqHeader;
	}
	public void setCustomerInsReqHeader(MessageRequestHeader customerInsReqHeader) {
		this.customerInsReqHeader = customerInsReqHeader;
	}
	public CustomerInsertRequestBody getCustomerInsReqBody() {
		return customerInsReqBody;
	}
	public void setCustomerInsReqBody(CustomerInsertRequestBody customerInsReqBody) {
		this.customerInsReqBody = customerInsReqBody;
	}
	
	
}
