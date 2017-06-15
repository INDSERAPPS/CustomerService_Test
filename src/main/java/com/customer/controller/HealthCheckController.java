package com.customer.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.customer.constants.CustomerConstants;
import com.customer.exceptions.ApplicationException;
import com.customer.mapper.CustomerMessageMapper;
import com.customer.message.HealthCheckResponse;
import com.customer.service.CustomerServices;

@Controller
@RequestMapping(value = "/v1.1/*")
public class HealthCheckController {
	
	@Autowired
	private CustomerServices custServices ;
	
	@Autowired
	  private Environment env;
	
	@Autowired
	private CustomerMessageMapper messageMapper ;
	
	@RequestMapping(value="/Health", method = RequestMethod.POST, headers = "Accept=application/json")
	public @ResponseBody ResponseEntity<String> checkHealthStatus() throws ApplicationException
	{
		HealthCheckResponse healthResp = new HealthCheckResponse();
		HttpHeaders respHeaders =null ;
		String healthCheckResp = null ;
		
		try {
			String schemaVersion = custServices.fetchMetaData();
			String appVersion = env.getProperty("application.version") ;
			 respHeaders = setRespHeaders() ;
			
			healthResp.setAppVersion(appVersion);
			healthResp.setSchemaVersion(schemaVersion);
			healthCheckResp = messageMapper.convertMetaDataObjToJson(healthResp);
			return new ResponseEntity (healthCheckResp,respHeaders,HttpStatus.OK) ;
			
		} catch (ApplicationException e) {
			respHeaders = setRespHeaders() ;
			return new ResponseEntity (healthCheckResp,respHeaders,HttpStatus.OK) ;

		}
	}
	
	public HttpHeaders setRespHeaders() throws ApplicationException 
	{
		HttpHeaders respHeader = new HttpHeaders()  ;
		respHeader.add("Accept-Charset", CustomerConstants.ACCEPT_CHARSET) ;
		
		respHeader.add("Content-Type", CustomerConstants.CONTENT_TYPE);
		respHeader.add("Date", getDateInGMT());
		
		return respHeader ;
		
	}
	
	public String getDateInGMT()
	{
		Date date = new Date();
		 SimpleDateFormat sd = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z");
		    sd.setTimeZone(TimeZone.getTimeZone("GMT"));
		    return sd.format(date) ;
	}

}
