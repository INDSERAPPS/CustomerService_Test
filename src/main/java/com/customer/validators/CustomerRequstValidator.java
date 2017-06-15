package com.customer.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.customer.Entity.Customer;
import com.customer.constants.CustomerConstants;

import com.customer.exceptions.ApplicationException;
import com.customer.message.CustomerGetRequest;
import com.customer.message.CustomerInsertRequest;
import com.customer.message.CustomerUpdateRequest;
import com.customer.message.MessageRequestHeader;

import com.customer.service.CustomerServices;


@Component
public class CustomerRequstValidator {
	
	@Autowired
	public CustomerServices custServices ;
	
	private static Logger log = LogManager.getLogger(CustomerRequstValidator.class);
	
	public String validateCustomerInsertRequest(CustomerInsertRequest custInsertReq) throws ApplicationException
	{
		log.info("Validating Customer insert request") ;
		if(!validateHeaderFields(custInsertReq.getCustomerInsReqHeader()))
		{
			log.error("All mandatory header fields are not present") ;
			throw new ApplicationException(CustomerConstants.NOT_ALL_MAND_FIELD_PRESENT,"Invalid Header details") ;
			
		}
		
		if((custInsertReq.getCustomerInsReqBody().getName()==null)||(custInsertReq.getCustomerInsReqBody().getName().equals("")))
				{
			log.error("Name Fields (mandatory) with no value in it") ;
			throw new ApplicationException(CustomerConstants.NULL_MAND_FIELD_VALUE,"Name Fields (mandatory) with no value in it") ;
				}
		
		if((custInsertReq.getCustomerInsReqBody().getEmail()==null)||(custInsertReq.getCustomerInsReqBody().getEmail().equals("")))
		{
			log.error("Email Fields (mandatory) with no value in it") ;
	throw new ApplicationException(CustomerConstants.NULL_MAND_FIELD_VALUE,"Email Fields (mandatory) with no value in it") ;
		}
		
		if(!validateEmail(custInsertReq.getCustomerInsReqBody().getEmail()))
		{
			log.error("Email with invalid characters ") ;
			throw new ApplicationException(CustomerConstants.INVALID_EMAIL,"Email with invalid characters  ") ;

		}
		if((custInsertReq.getCustomerInsReqBody().getPassword()==null)||(custInsertReq.getCustomerInsReqBody().getPassword().equals("")))
		{
			log.error("Password Fields (mandatory) with no value in it") ;
	throw new ApplicationException(CustomerConstants.NULL_MAND_FIELD_VALUE,"Password Fields (mandatory) with no value in it") ;
		}
		
		if((custInsertReq.getCustomerInsReqBody().getName().length()<3)||
				(custInsertReq.getCustomerInsReqBody().getName().length()>50))
		{
			log.error("Name should be a string with length 3-50.") ;
			throw new ApplicationException(CustomerConstants.INVALID_NAME,"Name should be a string with length 3-50.") ;

		}
		
		if(!validateName(custInsertReq.getCustomerInsReqBody().getName()))
		{
			log.error("Name with invalid characters") ;
			throw new ApplicationException(CustomerConstants.INVALID_NAME,"Name with invalid characters") ;
		}
		
		if((custInsertReq.getCustomerInsReqBody().getPassword().length()<10)||
				(custInsertReq.getCustomerInsReqBody().getPassword().length()>100))
		{
			log.error("Password should be a string with length 10-100") ;
			throw new ApplicationException(CustomerConstants.INVALID_PASSWORD,"Password should be a string with length 10-100") ;

		}
		
		if(custInsertReq.getCustomerInsReqBody().getAccountType()!=null)
		{
		if(!custInsertReq.getCustomerInsReqBody().getAccountType().equals("S")&&(!custInsertReq.getCustomerInsReqBody().getAccountType().equals("P")))
		{
			log.error("Account type other than S or P") ;
			throw new ApplicationException(CustomerConstants.INVALID_ACCOUNT_TYPE,"Account type other than S or P") ;
		}
		}
		
		String email = custInsertReq.getCustomerInsReqBody().getEmail();
		if(custServices.checkCustomerExist(email).equals("false"))
		{
			System.out.println("email exist");
			log.error("e-Mail already exist in the system") ;
			throw new ApplicationException(CustomerConstants.EMAIL_ALREADY_EXIST,"e-Mail already exist in the system ") ;
		}
		log.info("Validation completed successfully") ;
		return "success" ;
	}
	
	
	public String validateCustomerUpdateRequest(CustomerUpdateRequest custUpdateReq) throws ApplicationException
	{
		
		if(!validateHeaderFields(custUpdateReq.getRequestHeader()))
		{
			log.error("Invalid Header details") ;
			throw new ApplicationException(CustomerConstants.NOT_ALL_MAND_FIELD_PRESENT,"Invalid Header details") ;
			
		}
		
		if((custUpdateReq.getCustomerUpdateReqBody().getCustomerId()==null)||
				(custUpdateReq.getCustomerUpdateReqBody().getCustomerId().equals("")))
				{
			log.error("Customer_id Fields (mandatory) with no value in it") ;
			throw new ApplicationException(CustomerConstants.NULL_MAND_FIELD_VALUE,"Customer_id Fields (mandatory) with no value in it") ;
				}
		if(custUpdateReq.getCustomerUpdateReqBody().getName()!=null)
		{
		if((custUpdateReq.getCustomerUpdateReqBody().getName().length()<3)
				||(custUpdateReq.getCustomerUpdateReqBody().getName().length()>50))
		{
			log.error("Name should be a string with length 3-50.") ;
			throw new ApplicationException(CustomerConstants.INVALID_NAME,"Name should be a string with length 3-50.") ;
		}
		if(!validateName(custUpdateReq.getCustomerUpdateReqBody().getName()))
		{
			log.error("Name with invalid characters") ;
			throw new ApplicationException(CustomerConstants.INVALID_NAME,"Name with invalid characters") ;
		}
		}
		if(custUpdateReq.getCustomerUpdateReqBody().getEmail()!=null)
		{
			if(!validateEmail(custUpdateReq.getCustomerUpdateReqBody().getEmail()))
			{
				log.error("Email with invalid characters") ;
				throw new ApplicationException(CustomerConstants.INVALID_EMAIL,"Email with invalid characters  ") ;
			}
			if(custServices.checkCustomerExist(custUpdateReq.getCustomerUpdateReqBody().getEmail()).equals("false"))
			{
				
				log.error("e-Mail already exist in the system") ;
				throw new ApplicationException(CustomerConstants.EMAIL_ALREADY_EXIST,"e-Mail already exist in the system ") ;
			}
		}
		if(custUpdateReq.getCustomerUpdateReqBody().getPassword()!=null)
		{
		if((custUpdateReq.getCustomerUpdateReqBody().getPassword().length()<10)||(custUpdateReq.getCustomerUpdateReqBody().getPassword().length()>100))
		{
			log.error("Password should be a string with length 10-100") ;
			throw new ApplicationException(CustomerConstants.INVALID_PASSWORD,"Password should be a string with length 10-100") ;
		}
		}
		if(custUpdateReq.getCustomerUpdateReqBody().getAccountType()!=null)
		{
		if(!(custUpdateReq.getCustomerUpdateReqBody().getAccountType().equals("S"))
				&&!(custUpdateReq.getCustomerUpdateReqBody().getAccountType().equals("P")))
		{
			log.error("Account type other than S or P") ;
			throw new ApplicationException(CustomerConstants.INVALID_ACCOUNT_TYPE,"Account type other than S or P") ;
		}
		}
		
		if(custUpdateReq.getCustomerUpdateReqBody().getCustomerId().length()!=17)
		{
			log.error("Name should be a string with length 17  ") ;
			throw new ApplicationException(CustomerConstants.INVALID_CUST_ID,"Name should be a string with length 10  ") ;
		}
		
		if(custServices.fetchCustomer(custUpdateReq.getCustomerUpdateReqBody().getCustomerId())==null)
		{
			log.error("Customer does not exist   ") ;
			throw new ApplicationException(CustomerConstants.CUST_NOT_EXIST,"Customer does not exist ") ;
		}
		
		log.info("Validation Successful") ;
		return "success" ;
	}
	
	public String validateGetCustomerRequest(CustomerGetRequest custGetReq) throws ApplicationException
	{
		 if(!validateHeaderFields(custGetReq.getCustomerGetReqHeader()))
		 {
			 log.error("Invalid Header details") ;
				throw new ApplicationException(CustomerConstants.NOT_ALL_MAND_FIELD_PRESENT,"Invalid Header details") ;

		 }
		
		if(((custGetReq.getCustomerGetReqBody().getCustomerid()==null)||
				(custGetReq.getCustomerGetReqBody().getCustomerid().equals("")))&&
				((custGetReq.getCustomerGetReqBody().getEmail()==null)||(custGetReq.getCustomerGetReqBody().getEmail().equals(""))))
				{
			log.error("ID or Email is mandatory , both are absent") ;
			throw new ApplicationException(CustomerConstants.NOT_ALL_MAND_FIELD_PRESENT,"ID or Email is mandatory") ;
				}
		if(custGetReq.getCustomerGetReqBody().getCustomerid()!=null)
				{
			if(custGetReq.getCustomerGetReqBody().getCustomerid().length()!=17)
			{
				log.error("Length of customer id is not 17 ") ;
				throw new ApplicationException(CustomerConstants.INVALID_CUST_ID,"Length of customer id is not 17 ") ;
			}
			if(custServices.fetchCustomer(custGetReq.getCustomerGetReqBody().getCustomerid())==null)
			{
				log.error("Customer does not exist ") ;
				throw new ApplicationException(CustomerConstants.CUST_NOT_EXIST,"Customer does not exist ") ;
			}
		
		
				}
		if(custGetReq.getCustomerGetReqBody().getEmail()!=null)
		{
			if(!validateEmail(custGetReq.getCustomerGetReqBody().getEmail()))
			{
				log.error("Email with invalid characters  ") ;
				throw new ApplicationException(CustomerConstants.INVALID_EMAIL,"Email with invalid characters  ") ;
			}
			if(custServices.checkCustomerExist(custGetReq.getCustomerGetReqBody().getEmail()).equals("true"))
			{
				log.error("e-Mail does not exist ") ;
				throw new ApplicationException(CustomerConstants.EMAIL_NOT_EXIST,"e-Mail does not exist ") ;
			}
			
		}
		
		if((custGetReq.getCustomerGetReqBody().getEmail()!=null)&&(custGetReq.getCustomerGetReqBody().getCustomerid()!=null))
		{
			if(validateGetRequestCondInpConflict(custGetReq))
					{
				log.error("Conflict in Cond Inputs:Combination is not present") ;
				throw new ApplicationException(CustomerConstants.CONFLICT_COND_INPUTS,"Conflict in Cond Inputs:Combination is not present") ;
					}
		}
		
		log.info("Validation successful") ;
		return "success" ;
		
	}
	
private boolean validateEmail(String email)
{
String emailPattern =
			"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

Pattern pattern = Pattern.compile(emailPattern);
Matcher matcher = pattern.matcher(email);
return matcher.matches();
}

private boolean validateHeaderFields(MessageRequestHeader reqHeader)
{
	if((reqHeader.getAcceptCharSet()==null)||(reqHeader.getCi()==null)||(reqHeader.getContentType()==null)
			||(reqHeader.getDate()==null)||(reqHeader.getTocken()==null))
	{
		
		log.error("All mandatory header fields are not present") ;
		return false ;
	}
	if((reqHeader.getCi().equals(""))||(reqHeader.getDate().equals(""))||(reqHeader.getTocken().equals("")))
	{
		
		return false ;
	}
	if(!validateString(reqHeader.getTocken()))
	{
		
		log.error("Invalid message tocken format") ;
		return false ;
	}
	if((!reqHeader.getAcceptCharSet().equals(CustomerConstants.ACCEPT_CHARSET))
			||(!reqHeader.getContentType().equals(CustomerConstants.CONTENT_TYPE)))
			{
		
		return false ;
			}
			return true ;
}

private boolean validateName(String name)
{
	String name_pattern ="^[\\p{L} .'-]+$" ;

Pattern pattern = Pattern.compile(name_pattern);
Matcher matcher = pattern.matcher(name);
return matcher.matches();
}

private boolean validateString(String string)
{
	String stringPattern ="^[A-Za-z0-9, ]++$" ;
	Pattern pattern = Pattern.compile(stringPattern);
	Matcher matcher = pattern.matcher(string);
	return matcher.matches();
}

private boolean validateGetRequestCondInpConflict(CustomerGetRequest custGetReq) throws ApplicationException
{
	Customer customer = custServices.fetchCustomer(custGetReq.getCustomerGetReqBody().getCustomerid()) ;
	
	if(customer!=null)
	{
		if(customer.getEmail().equals(custGetReq.getCustomerGetReqBody().getEmail()))
		{
			return false ;
		}
		return true ;
	}
	return true ;
}

}


