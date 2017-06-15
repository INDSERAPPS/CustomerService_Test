package com.customer.exceptions;

public class ApplicationException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int errorCode = -1 ;
	
	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int error_message_code) {
		this.errorCode = error_message_code;
	}
	
	public ApplicationException()
	{
		super();
	}
	
	 public ApplicationException(int err_message_code, String message)
	    {
		 
	        super(message);
	        this.errorCode = err_message_code ;
	        
	    }


}
