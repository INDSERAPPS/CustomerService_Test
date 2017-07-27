package com.customer.controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.CountDownLatch;

//import javax.jms.Connection;
//import javax.jms.DeliveryMode;
//import javax.jms.Destination;
//import javax.jms.Message;
//import javax.jms.MessageProducer;
//import javax.jms.Session;
//import javax.jms.TextMessage;
//import javax.jms.Topic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.customer.Entity.Customer;
import com.customer.constants.CustomerConstants;
import com.customer.exceptions.ApplicationException;
import com.customer.mapper.CustomerMessageMapper;
import com.customer.message.CustomerGetRequest;
import com.customer.message.CustomerGetRequestBody;
import com.customer.message.CustomerGetResponse;
import com.customer.message.CustomerGetResponseBody;
import com.customer.message.CustomerInsertRequest;
import com.customer.message.CustomerInsertResponse;
import com.customer.message.CustomerInsertResponseBody;
import com.customer.message.CustomerUpdateRequest;
import com.customer.message.CustomerUpdateResponse;
import com.customer.message.CustomerUpdateResponseBody;
import com.customer.message.ErrorDetails;
import com.customer.message.MessageRequestHeader;

import com.customer.message.ResponseStatus;
import com.customer.service.CustomerServices;
import com.customer.validators.CustomerRequstValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.solacesystems.jcsmp.BytesXMLMessage;
import com.solacesystems.jcsmp.ConsumerFlowProperties;
import com.solacesystems.jcsmp.DeliveryMode;
import com.solacesystems.jcsmp.EndpointProperties;
import com.solacesystems.jcsmp.FlowReceiver;
import com.solacesystems.jcsmp.JCSMPException;
import com.solacesystems.jcsmp.JCSMPFactory;
import com.solacesystems.jcsmp.JCSMPProperties;
import com.solacesystems.jcsmp.JCSMPSession;
import com.solacesystems.jcsmp.JCSMPStreamingPublishEventHandler;
import com.solacesystems.jcsmp.Queue;
import com.solacesystems.jcsmp.TextMessage;
import com.solacesystems.jcsmp.XMLMessageListener;
import com.solacesystems.jcsmp.XMLMessageProducer;
import com.solacesystems.jms.SolConnectionFactory;
import com.solacesystems.jms.SolJmsUtility;

@Controller
@RequestMapping(value = "/v1.1/*")
public class ManageCustomerController {

	@Autowired
	private CustomerServices custServices;

	@Autowired
	private CustomerRequstValidator custReqVal;

	@Autowired
	private CustomerMessageMapper messageMapper;

	private static Logger log = LogManager.getLogger(ManageCustomerController.class);
	private static Logger reqRespLog = LogManager.getLogger("REQRESP");

	@RequestMapping(value = "/customers", method = RequestMethod.POST, headers = "Accept=application/json")
	public @ResponseBody ResponseEntity<String> insertCustomerInfo(@RequestBody String customerRequst,
			@RequestHeader(value = "Content-Type", required = false) String type,
			@RequestHeader(value = "Accept-Charset", required = false) String charSet,
			@RequestHeader(value = "Date", required = false) String date,
			@RequestHeader(value = "Server", required = false) String server,
			@RequestHeader(value = "Tocken", required = false) String tocken,
			@RequestHeader(value = "AppName", required = false) String appName)
			throws JsonProcessingException, ApplicationException {
		log.info("Received insert customer request and started mapping");

		Timestamp receivedTime = new Timestamp(System.currentTimeMillis());
		MessageRequestHeader reqHeader = setReqHeaderObj(type, charSet, date, server, tocken,appName);

		CustomerInsertResponse customerInsertResp = new CustomerInsertResponse();

		HttpHeaders respHeaders = null;

		try {

			CustomerInsertRequest customerInsertReq = messageMapper.convertJSONtoInsertReq(customerRequst, reqHeader);
			System.out.println("before validation");
			if (custReqVal.validateCustomerInsertRequest(customerInsertReq).equals("success")) {

				String customerId = custServices.insertCustomer(customerInsertReq);
				log.info("Set Response Headers");
				respHeaders = setRespHeaders(tocken);
				String customerResponse = buildCustomerInsertSuccessResponse(customerInsertReq, customerInsertResp,
						customerId, receivedTime);
				displayResponseInLogger(respHeaders, customerResponse);
				return new ResponseEntity(customerResponse, respHeaders, HttpStatus.OK);
			}
		} catch (ApplicationException e) {

			log.info("Set Response Headers");
			respHeaders = setRespHeaders(tocken);
			String customerResponse = buildCustomerInsertErrorResponse(customerInsertResp, e, receivedTime);
			displayResponseInLogger(respHeaders, customerResponse);

			return new ResponseEntity(customerResponse, respHeaders, HttpStatus.OK);

		} catch (IOException e) {
			log.info("Set Response Headers");
			respHeaders = setRespHeaders(tocken);
			int error_code = 700;
			ApplicationException e1 = new ApplicationException(error_code, "Mandatory fields are null");
			String customerResponse = buildCustomerInsertErrorResponse(customerInsertResp, e1, receivedTime);
			displayResponseInLogger(respHeaders, customerResponse);
			return new ResponseEntity(customerResponse, respHeaders, HttpStatus.OK);
		}
		return null;

	}

	@RequestMapping(value = "/customers", method = RequestMethod.PUT, headers = "Accept=application/json")
	public @ResponseBody ResponseEntity<String> updateCustomerInfo(@RequestBody String customerRequest,
			@RequestHeader(value = "Content-Type", required = false) String type,
			@RequestHeader(value = "Accept-Charset", required = false) String charSet,
			@RequestHeader(value = "Date", required = false) String date,
			@RequestHeader(value = "Server", required = false) String server,
			@RequestHeader(value = "Tocken", required = false) String tocken,
			@RequestHeader(value = "AppName", required = false) String appName)
			throws JsonProcessingException, ApplicationException {
		Timestamp receivedTime = new Timestamp(System.currentTimeMillis());
		log.info("Received update customer request and started mapping");
		HttpHeaders respHeaders = null;
		CustomerUpdateResponse custUpdateResp = new CustomerUpdateResponse();
		MessageRequestHeader reqHeader = setReqHeaderObj(type, charSet, date, server, tocken,appName);

		try {
			CustomerUpdateRequest custUpdateReq = messageMapper.convertJSONtoUpdateReq(customerRequest, reqHeader);

			if (custReqVal.validateCustomerUpdateRequest(custUpdateReq).equals("success")) {
				String isUpdate = custServices.updateCustomer(custUpdateReq);
				if (isUpdate.equals("true")) {
					log.info("Set Response Headers");
					respHeaders = setRespHeaders(tocken);
					String customerResponse = buildCustomerUpdateSuccessResponse(custUpdateReq, custUpdateResp,
							receivedTime);
					displayResponseInLogger(respHeaders, customerResponse);
					return new ResponseEntity(customerResponse, respHeaders, HttpStatus.OK);
				}
			}

		} catch (ApplicationException e) {
			log.info("Set Response Headers");
			respHeaders = setRespHeaders(tocken);
			String customerResponse = buildCustUpdateErrorResponse(custUpdateResp, e, receivedTime);
			displayResponseInLogger(respHeaders, customerResponse);
			return new ResponseEntity(customerResponse, respHeaders, HttpStatus.OK);

		}
		return null;

	}

	@RequestMapping(value = "/customers", method = RequestMethod.GET, headers = "Accept=application/json")
	public @ResponseBody ResponseEntity<String> getCustomerInfo(
			@RequestParam(value = "customerId", required = false) String customerId,
			@RequestParam(value = "email", required = false) String email,
			@RequestHeader(value = "Content-Type", required = false) String type,
			@RequestHeader(value = "Accept-Charset", required = false) String charSet,
			@RequestHeader(value = "Date", required = false) String date,
			@RequestHeader(value = "Server", required = false) String server,
			@RequestHeader(value = "Tocken", required = false) String tocken,
			@RequestHeader(value = "AppName", required = false) String appName)
			throws JsonProcessingException, ApplicationException {
		Timestamp receivedTime = new Timestamp(System.currentTimeMillis());
		log.info("Received get customer request and started mapping");
		CustomerGetResponse custGetResp = new CustomerGetResponse();
		CustomerGetRequest custGetReq = new CustomerGetRequest();

		log.info("Mapped request headers");
		MessageRequestHeader reqHeader = setReqHeaderObj(type, charSet, date, server, tocken,appName);
		CustomerGetRequestBody custGetReqBody = new CustomerGetRequestBody();

		custGetReqBody.setCustomerid(customerId);
		custGetReqBody.setEmail(email);

		HttpHeaders respHeaders = null;

		custGetReq.setCustomerGetReqHeader(reqHeader);
		custGetReq.setCustomerGetReqBody(custGetReqBody);

		try {
			// CustomerGetRequest custGetReq =
			// messageMapper.convertJSONtoGetReq(customerRequest);
			if (custReqVal.validateGetCustomerRequest(custGetReq).equals("success")) {
				Customer customer = new Customer();
				customer = custServices.getCustomer(custGetReq);
				log.info("Set response headers");
				respHeaders = setRespHeaders(tocken);
				String customerResponse = buildGetCustomerSuccessResponse(custGetReq, custGetResp, customer,
						receivedTime);
				displayResponseInLogger(respHeaders, customerResponse);
				return new ResponseEntity(customerResponse, respHeaders, HttpStatus.OK);

			}
		} catch (ApplicationException e) {
			log.info("Set response headers");
			respHeaders = setRespHeaders(tocken);
			String customerResponse = buildGetCustErrorResponse(custGetResp, e, receivedTime);
			displayResponseInLogger(respHeaders, customerResponse);
			return new ResponseEntity(customerResponse, respHeaders, HttpStatus.OK);

		}
		return null;
	}

	public CustomerServices getCustServices() {
		return custServices;
	}

	public void setCustServices(CustomerServices custServices) {
		this.custServices = custServices;
	}

	private String buildCustomerInsertSuccessResponse(CustomerInsertRequest customerInsertReq,
			CustomerInsertResponse customerInsertResp, String customerId, Timestamp receivedTime)
			throws JsonProcessingException, ApplicationException {
		log.info("Building customer insert success response");
		// ResponseHeader respHeader = new ResponseHeader();
		CustomerInsertResponseBody custInsertRespBody = new CustomerInsertResponseBody();

		/*
		 * respHeader.setAcceptCharSet(customerInsertReq.getCustomerInsReqHeader().
		 * getAcceptCharSet());
		 * respHeader.setCi(customerInsertReq.getCustomerInsReqHeader().getCi());
		 * respHeader.setContentType(customerInsertReq.getCustomerInsReqHeader().
		 * getContentType());
		 * respHeader.setDate(customerInsertReq.getCustomerInsReqHeader().getDate());
		 */
		custInsertRespBody.setCustomerId(customerId);

		ResponseStatus responseStatus = new ResponseStatus();
		responseStatus.setHttpStatusCode(CustomerConstants.HTTP_STATUS_CODE);
		responseStatus.setStatus(CustomerConstants.SUCCESS_STATUS);
		responseStatus.setStatusCode(CustomerConstants.SUCCESS_CODE);
		Timestamp sendTime = new Timestamp(System.currentTimeMillis());
		responseStatus.setResponseTime(sendTime.getTime() - receivedTime.getTime());
		custInsertRespBody.setRespStatus(responseStatus);

		// customerInsertResp.setRespHeader(respHeader) ;
		// customerInsertResp.setCustomerInsResBody(custInsertRespBody);

		String customerResponse = messageMapper.convertInsertRespObjToJson(custInsertRespBody);
		log.info("Sent customer insert success response");
		return customerResponse;

	}

	private String buildCustomerUpdateSuccessResponse(CustomerUpdateRequest custUpdateReq,
			CustomerUpdateResponse custUpdateResp, Timestamp receivedTime)
			throws JsonProcessingException, ApplicationException {
		log.info("Building customer update success response");
		// ResponseHeader respHeader = new ResponseHeader();
		CustomerUpdateResponseBody custUpdateRespBody = new CustomerUpdateResponseBody();

		/*
		 * respHeader.setAcceptCharSet(custUpdateReq.getRequestHeader().getAcceptCharSet
		 * ()); respHeader.setCi(custUpdateReq.getRequestHeader().getCi());
		 * respHeader.setContentType(custUpdateReq.getRequestHeader().getContentType());
		 * respHeader.setDate(custUpdateReq.getRequestHeader().getDate());
		 */

		ResponseStatus responseStatus = new ResponseStatus();
		responseStatus.setHttpStatusCode(CustomerConstants.HTTP_STATUS_CODE);

		responseStatus.setStatus(CustomerConstants.SUCCESS_STATUS);
		responseStatus.setStatusCode(CustomerConstants.SUCCESS_CODE);
		Timestamp sendTime = new Timestamp(System.currentTimeMillis());
		responseStatus.setResponseTime(sendTime.getTime() - receivedTime.getTime());
		custUpdateRespBody.setRespStatus(responseStatus);

		/*
		 * custUpdateResp.setRespHeader(respHeader);
		 * custUpdateResp.setCustomerUpdateRespBody(custUpdateRespBody);
		 */

		String customerResponse;

		customerResponse = messageMapper.convertUpdateRespObjToJson(custUpdateRespBody);
		log.info("Sent customer update success response");
		return customerResponse;

	}

	private String buildGetCustomerSuccessResponse(CustomerGetRequest custGetReq, CustomerGetResponse custGetResp,
			Customer customer, Timestamp receivedTime) throws JsonProcessingException, ApplicationException {
		log.info("Building customer get success response");
		// ResponseHeader respHeader = new ResponseHeader();
		CustomerGetResponseBody custGetRespBody = new CustomerGetResponseBody();
		custGetRespBody.setCustomerid(customer.getCustomerId());
		custGetRespBody.setName(customer.getName());
		custGetRespBody.setEmail(customer.getEmail());
		custGetRespBody.setAccounttype(customer.getAccountType());
		custGetRespBody.setIsenabled(Boolean.toString(customer.isEnabled()));

		/*
		 * respHeader.setAcceptCharSet(custGetReq.getCustomerGetReqHeader().
		 * getAcceptCharSet());
		 * respHeader.setCi(custGetReq.getCustomerGetReqHeader().getCi());
		 * respHeader.setContentType(custGetReq.getCustomerGetReqHeader().getContentType
		 * ()); respHeader.setDate(custGetReq.getCustomerGetReqHeader().getDate());
		 */

		ResponseStatus responseStatus = new ResponseStatus();
		responseStatus.setHttpStatusCode(CustomerConstants.HTTP_STATUS_CODE);
		responseStatus.setStatus(CustomerConstants.SUCCESS_STATUS);
		responseStatus.setStatusCode(CustomerConstants.SUCCESS_CODE);
		Timestamp sendTime = new Timestamp(System.currentTimeMillis());
		responseStatus.setResponseTime(sendTime.getTime() - receivedTime.getTime());
		custGetRespBody.setStatus(responseStatus);

		/*
		 * custGetResp.setRespHeader(respHeader) ;
		 * custGetResp.setCustomerGetResBody(custGetRespBody);
		 */

		String customerResponse;
		log.info("Sent customer get success response");
		customerResponse = messageMapper.convertGetRespObjToJson(custGetRespBody);

		return customerResponse;
	}

	private String buildCustomerInsertErrorResponse(CustomerInsertResponse customerInsertResp, ApplicationException e,
			Timestamp receivedTime) throws JsonProcessingException, ApplicationException {
		log.info("Building customer insert error response");
		// ResponseHeader respHeader = new ResponseHeader();
		CustomerInsertResponseBody custInsertRespBody = new CustomerInsertResponseBody();

		/*
		 * respHeader.setAcceptCharSet(CustomerConstants.ACCEPT_CHARSET);
		 * respHeader.setCi("Server");
		 * respHeader.setContentType(CustomerConstants.CONTENT_TYPE);
		 * respHeader.setDate(getDateInGMT());
		 */

		ResponseStatus responseStatus = new ResponseStatus();
		responseStatus.setHttpStatusCode(CustomerConstants.HTTP_STATUS_CODE);
		responseStatus.setStatus(CustomerConstants.FAILURE_STATUS);
		responseStatus.setStatusCode(e.getErrorCode());
		Timestamp sendTime = new Timestamp(System.currentTimeMillis());
		responseStatus.setResponseTime(sendTime.getTime() - receivedTime.getTime());

		ErrorDetails errorDetails = new ErrorDetails();
		errorDetails.setError(e.getMessage());
		errorDetails.setErrorDetail(e.getMessage());

		custInsertRespBody.setRespStatus(responseStatus);
		custInsertRespBody.setError(errorDetails);

		/*
		 * customerInsertResp.setRespHeader(respHeader) ;
		 * customerInsertResp.setCustomerInsResBody(custInsertRespBody);
		 */

		String customerResponse = messageMapper.convertInsertRespObjToJson(custInsertRespBody);
		log.info("Sent customer insert error response");
		return customerResponse;

	}

	private String buildCustUpdateErrorResponse(CustomerUpdateResponse custUpdateResp, ApplicationException e,
			Timestamp receivedTime) throws JsonProcessingException, ApplicationException {
		log.info("Building customer update error response");
		// ResponseHeader respHeader = new ResponseHeader();
		CustomerUpdateResponseBody custUpdateRespBody = new CustomerUpdateResponseBody();

		/*
		 * respHeader.setAcceptCharSet(CustomerConstants.ACCEPT_CHARSET);
		 * respHeader.setCi("Server");
		 * respHeader.setContentType(CustomerConstants.CONTENT_TYPE);
		 * respHeader.setDate(getDateInGMT());
		 */

		ResponseStatus responseStatus = new ResponseStatus();
		responseStatus.setHttpStatusCode(CustomerConstants.HTTP_STATUS_CODE);

		responseStatus.setStatus(CustomerConstants.FAILURE_STATUS);
		responseStatus.setStatusCode(e.getErrorCode());
		Timestamp sendTime = new Timestamp(System.currentTimeMillis());
		responseStatus.setResponseTime(sendTime.getTime() - receivedTime.getTime());
		custUpdateRespBody.setRespStatus(responseStatus);

		ErrorDetails errorDetails = new ErrorDetails();
		errorDetails.setError(e.getMessage());
		errorDetails.setErrorDetail(e.getMessage());
		custUpdateRespBody.setErrorDetails(errorDetails);

		/*
		 * custUpdateResp.setRespHeader(respHeader);
		 * custUpdateResp.setCustomerUpdateRespBody(custUpdateRespBody);
		 */

		String customerResponse = messageMapper.convertUpdateRespObjToJson(custUpdateRespBody);
		log.info("Sent customer update error response");
		return customerResponse;
	}

	private String buildGetCustErrorResponse(CustomerGetResponse custGetResp, ApplicationException e,
			Timestamp receivedTime) throws JsonProcessingException, ApplicationException {
		log.info("Building customer get error response");
		// ResponseHeader respHeader = new ResponseHeader();
		CustomerGetResponseBody custGetRespBody = new CustomerGetResponseBody();

		ErrorDetails errorDetails = new ErrorDetails();
		errorDetails.setError(e.getMessage());
		errorDetails.setErrorDetail(e.getMessage());
		custGetRespBody.setError(errorDetails);

		/*
		 * respHeader.setAcceptCharSet(CustomerConstants.ACCEPT_CHARSET);
		 * respHeader.setCi("Server");
		 * respHeader.setContentType(CustomerConstants.CONTENT_TYPE);
		 * respHeader.setDate(getDateInGMT());
		 */

		ResponseStatus responseStatus = new ResponseStatus();
		responseStatus.setHttpStatusCode(CustomerConstants.HTTP_STATUS_CODE);
		responseStatus.setStatus(CustomerConstants.FAILURE_STATUS);
		responseStatus.setStatusCode(e.getErrorCode());
		Timestamp sendTime = new Timestamp(System.currentTimeMillis());
		responseStatus.setResponseTime(sendTime.getTime() - receivedTime.getTime());
		custGetRespBody.setStatus(responseStatus);

		/*
		 * custGetResp.setRespHeader(respHeader) ;
		 * custGetResp.setCustomerGetResBody(custGetRespBody);
		 */

		String customerResponse = messageMapper.convertGetRespObjToJson(custGetRespBody);
		log.info("Sent customer get error response");
		return customerResponse;
	}

	public String getDateInGMT() {
		Date date = new Date();
		SimpleDateFormat sd = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z");
		sd.setTimeZone(TimeZone.getTimeZone("GMT"));
		return sd.format(date);
	}

	public MessageRequestHeader setReqHeaderObj(String type, String charSet, String date, String server,
			String tocken,String appName) {
		MessageRequestHeader reqHeader = new MessageRequestHeader();
		reqHeader.setContentType(type);
		reqHeader.setAcceptCharSet(charSet);
		reqHeader.setCi(server);
		reqHeader.setTocken(tocken);
		reqHeader.setDate(date);
		reqHeader.setAppName(appName);
		return reqHeader;
	}

	public HttpHeaders setRespHeaders(String tocken) throws ApplicationException {
		HttpHeaders respHeader = new HttpHeaders();
		respHeader.add("Accept-Charset", CustomerConstants.ACCEPT_CHARSET);
		respHeader.add("Tocken", tocken);
		respHeader.add("Content-Type", CustomerConstants.CONTENT_TYPE);
		respHeader.add("Date", getDateInGMT());

		return respHeader;

	}

	public void displayResponseInLogger(HttpHeaders httpHeaders, String body) {

		reqRespLog.info("\n" + "Response Message" + "\n" + "Headers" + "\n" + "Content-Type:"
				+ httpHeaders.get("Content-Type") + "\n" + "Accept-Charset:" + httpHeaders.get("Accept-Charset") + "\n"
				+ "Date:" + httpHeaders.get("Date") + "\n" + "Server:" + httpHeaders.get("Server") + "\n" + "Tocken:"
				+ httpHeaders.get("Tocken") + "/n" + "Response Body" + "\n" + body);
		try {

//			final JCSMPProperties properties = new JCSMPProperties();
			final JCSMPProperties properties = new JCSMPProperties();
			properties.setProperty(JCSMPProperties.HOST, "35.182.99.28:55555");
			properties.setProperty(JCSMPProperties.VPN_NAME, "interac_vpn");
			properties.setProperty(JCSMPProperties.USERNAME, "interac");
			properties.setProperty(JCSMPProperties.PASSWORD, "temp123");
			final JCSMPSession session = JCSMPFactory.onlyInstance().createSession(properties);
			session.connect();
			
			
			String queueName = "Q.interac.customerservice";
	        System.out.printf("Attempting to provision the queue '%s' on the appliance.%n", queueName);
	        final EndpointProperties endpointProps = new EndpointProperties();
	        // set queue permissions to "consume" and access-type to "exclusive"
	        endpointProps.setPermission(EndpointProperties.PERMISSION_CONSUME);
	        endpointProps.setAccessType(EndpointProperties.ACCESSTYPE_EXCLUSIVE);
	        // create the queue object locally
	        final Queue queue = JCSMPFactory.onlyInstance().createQueue(queueName);
	        // Actually provision it, and do not fail if it already exists
	        session.provision(queue, endpointProps, JCSMPSession.FLAG_IGNORE_ALREADY_EXISTS);

	        /** Anonymous inner-class for handling publishing events */
	        final XMLMessageProducer prod = session.getMessageProducer(new JCSMPStreamingPublishEventHandler() {
				
				@Override
				public void responseReceived(String messageID) {
					System.out.printf("Producer received response for msg ID #%s%n",messageID);
					
				}
				
				@Override
				public void handleError(String messageID, JCSMPException e, long timestamp) {
					System.out.printf("Producer received error for msg ID %s @ %s - %s%n",
                            messageID,timestamp,e);
					
				}
			});

	        // Publish-only session is now hooked up and running!
	        System.out.printf("Connected. About to send message to queue '%s'...%n",queue.getName());
	        TextMessage msg = JCSMPFactory.onlyInstance().createMessage(TextMessage.class);
	        msg.setDeliveryMode(DeliveryMode.PERSISTENT);
	        String text = "Persistent Queue Tutorial! "+DateFormat.getDateTimeInstance().format(new Date());
	        msg.setText(text);

	        // Send message directly to the queue
	        prod.send(msg, queue);
	        System.out.println("Message sent. Exiting.");
	        
	        
//	        Queue Consuming
	        System.out.printf("Attempting to provision the queue '%s' on the appliance.%n", queueName);
	        final EndpointProperties endPointPropsConsume = new EndpointProperties();
	        // set queue permissions to "consume" and access-type to "exclusive"
	        endPointPropsConsume.setPermission(EndpointProperties.PERMISSION_CONSUME);
	        endPointPropsConsume.setAccessType(EndpointProperties.ACCESSTYPE_EXCLUSIVE);
	        // create the queue object locally
//	        final Queue queueCons = JCSMPFactory.onlyInstance().createQueue(queueName);
	        // Actually provision it, and do not fail if it already exists
	        session.provision(queue, endPointPropsConsume, JCSMPSession.FLAG_IGNORE_ALREADY_EXISTS);

	        final CountDownLatch latch = new CountDownLatch(1); // used for synchronizing b/w threads

	        System.out.printf("Attempting to bind to the queue '%s' on the appliance.%n", queueName);

	        // Create a Flow be able to bind to and consume messages from the Queue.
	        final ConsumerFlowProperties flow_prop = new ConsumerFlowProperties();
	        flow_prop.setEndpoint(queue);
	        flow_prop.setAckMode(JCSMPProperties.SUPPORTED_MESSAGE_ACK_CLIENT);

	        EndpointProperties endpoint_props = new EndpointProperties();
	        endpoint_props.setAccessType(EndpointProperties.ACCESSTYPE_EXCLUSIVE);
	        
	        
	        final FlowReceiver cons = session.createFlow(new XMLMessageListener() {
				
				@Override
				public void onReceive(BytesXMLMessage msg) {
					// TODO Auto-generated method stub
					if (msg instanceof TextMessage) {
	                    System.out.printf("TextMessage received: '%s'%n", ((TextMessage) msg).getText());
	                } else {
	                    System.out.println("Message received.");
	                }
	                System.out.printf("Message Dump:%n%s%n", msg.dump());

	                // When the ack mode is set to SUPPORTED_MESSAGE_ACK_CLIENT,
	                // guaranteed delivery messages are acknowledged after
	                // processing
	                msg.ackMessage();
	                latch.countDown(); // unblock main thread
					
				}
				
				@Override
				public void onException(JCSMPException e) {
					System.out.printf("Consumer received exception: %s%n", e);
	                latch.countDown(); // unblock main threadstub
					
				}
			}, flow_prop, endpoint_props);
	        
	     // Start the consumer
	        System.out.println("Connected. Awaiting message ...");
	        cons.start();
	        latch.await();
	        cons.close();
	        
	        // Close session
	        session.closeSession();
			
//			SolConnectionFactory cf = SolJmsUtility.createConnectionFactory();
//			cf.setHost("35.182.98.253:55555");
//			cf.setVPN("interac_vpn");
//			cf.setUsername("interac");
//			cf.setPassword("temp123");
//
//			Connection connection = cf.createConnection();
//			final Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
//			
//			final Topic topic = session.createTopic("t/interac/poc");
//
//			final MessageProducer producer = session.createProducer(new Destination() {});
//			
//			TextMessage message = session.createTextMessage("Hello world!");
//			producer.send(new Destination() {},
//			            message,
//			            DeliveryMode.NON_PERSISTENT,
//			            Message.DEFAULT_PRIORITY,
//			            Message.DEFAULT_TIME_TO_LIVE);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}