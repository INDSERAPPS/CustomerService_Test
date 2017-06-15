package com.customer.mapper;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.customer.constants.CustomerConstants;

import com.customer.exceptions.ApplicationException;
import com.customer.message.CustomerGetRequest;

import com.customer.message.CustomerGetResponseBody;
import com.customer.message.CustomerInsertRequest;
import com.customer.message.CustomerInsertRequestBody;

import com.customer.message.CustomerInsertResponseBody;
import com.customer.message.CustomerUpdateRequest;
import com.customer.message.CustomerUpdateRequestBody;

import com.customer.message.CustomerUpdateResponseBody;
import com.customer.message.HealthCheckResponse;
import com.customer.message.MessageRequestHeader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;




@Component
public class CustomerMessageMapper {
	
	private static ObjectMapper jacksonMapper = new ObjectMapper();
	private static Logger log = LogManager.getLogger(CustomerMessageMapper.class);
	private static Logger reqRespLog =LogManager.getLogger("REQRESP") ;
	
	public CustomerInsertRequest convertJSONtoInsertReq(String customerReq,MessageRequestHeader reqHeader) throws ApplicationException
	{
		displayRequestinLogger(customerReq,reqHeader);
		CustomerInsertRequest customerInsertReq = new CustomerInsertRequest();
		CustomerInsertRequestBody customerInsertReqBody = null ;
		try {
			customerInsertReqBody = jacksonMapper.readValue(customerReq, CustomerInsertRequestBody.class);
			customerInsertReq.setCustomerInsReqHeader(reqHeader);
			customerInsertReq.setCustomerInsReqBody(customerInsertReqBody);
			log.info("Mapped customer insert request to object CustomerInsertRequest") ;
		} 
		 catch (IOException e) {
			 log.error("Invalid json format :"+e.getMessage()) ;
			throw new ApplicationException(CustomerConstants.CLIENT_ERROR,"Invalid JSON Format");
		}
		return customerInsertReq;
	}
	
	public CustomerUpdateRequest convertJSONtoUpdateReq(String customerReq,MessageRequestHeader reqHeader) throws ApplicationException
	{
		displayRequestinLogger(customerReq,reqHeader);
		CustomerUpdateRequest customerUpdateReq = new CustomerUpdateRequest() ;
		CustomerUpdateRequestBody custUpdateReqBody =null ;
		try {
			custUpdateReqBody = jacksonMapper.readValue(customerReq, CustomerUpdateRequestBody.class);
			customerUpdateReq.setRequestHeader(reqHeader);
			customerUpdateReq.setCustomerUpdateReqBody(custUpdateReqBody);
			log.info("Mapped customer update request to object CustomerInsertRequest") ;
		} 
		 catch (IOException e) {
			 log.error("Invalid json format :"+e.getMessage()) ;
			throw new ApplicationException(CustomerConstants.CLIENT_ERROR,"Invalid JSON Format");
		}
		return customerUpdateReq;
	}
	
	public CustomerGetRequest convertJSONtoGetReq(String customerReq) throws ApplicationException
	{
		CustomerGetRequest customerGetReq = null ;
		try {
			customerGetReq = jacksonMapper.readValue(customerReq, CustomerGetRequest.class);
		} 
		 catch (IOException e) {
			throw new ApplicationException(CustomerConstants.CLIENT_ERROR,"Invalid JSON Format");
		}
		return customerGetReq;
	}
	
	public String convertInsertRespObjToJson(CustomerInsertResponseBody customerResponse) throws ApplicationException
	{
		String customerInsertResponse = null ;
		try {
			 customerInsertResponse = jacksonMapper.writeValueAsString(customerResponse);
			 log.info("Created customer insert response") ;
		} catch (IOException e) {
			log.error("Unable to convert Object to Json:"+e.getMessage()) ;
			throw new ApplicationException(CustomerConstants.SYSTEM_ERROR,"Unable to generate Response");
			
		}
		return customerInsertResponse ;
	}
	
	public String convertUpdateRespObjToJson(CustomerUpdateResponseBody customerResponse) throws ApplicationException
	{
		String customerUpdateResponse = null ;
		try {
			customerUpdateResponse = jacksonMapper.writeValueAsString(customerResponse);
			log.info("Created customer update response") ;
		} catch (IOException e) {
			log.error("Unable to convert Object to Json:"+e.getMessage()) ;
			throw new ApplicationException(CustomerConstants.SYSTEM_ERROR,"Unable to generate Response");
			
		}
		return customerUpdateResponse ;
	}
	
	public String convertGetRespObjToJson(CustomerGetResponseBody customerResponse) throws ApplicationException
	{
		String customerGetResponse = null ;
		try {
			customerGetResponse = jacksonMapper.writeValueAsString(customerResponse);
			log.info("Created customer get response") ;
		} catch (IOException e) {
			log.error("Unable to convert Object to Json:"+e.getMessage()) ;
			throw new ApplicationException(CustomerConstants.SYSTEM_ERROR,"Unable to generate Response");
			
		}
		return customerGetResponse ;
	}
	
	public void displayRequestinLogger(String body ,MessageRequestHeader reqHeader)
	{
		
		reqRespLog.info("\n"+"Request Message:"+"\n"+"Headers"+"\n"+"Content-Type:"
		+reqHeader.getContentType()+"\n"+"Accept-Charset:"+reqHeader.getAcceptCharSet()
				+"\n"+"Date:"+reqHeader.getDate()+"\n"+"Server:"+reqHeader.getCi()+"\n"+"Tocken:"+reqHeader.getTocken()
				+"\n"+"Response Body"+"\n"+body);
		
	}
	
	public String convertMetaDataObjToJson(HealthCheckResponse healthResponse) throws ApplicationException
	{
		String healthCheckResponse = null ;
		try {
			healthCheckResponse = jacksonMapper.writeValueAsString(healthResponse);
			log.info("Created Health check response") ;
		} catch (JsonProcessingException e) {
			throw new ApplicationException(CustomerConstants.SYSTEM_ERROR,"Unable to generate Health check Response");
		}
		log.info("Created customer update response") ;
		return healthCheckResponse ;
	}
	
	

}
