package com.customer.service;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.customer.Entity.AppMetaData;
import com.customer.Entity.BaseEntity;
import com.customer.Entity.Customer;
import com.customer.constants.CustomerConstants;

import com.customer.exceptions.ApplicationException;
import com.customer.message.CustomerGetRequest;
import com.customer.message.CustomerInsertRequest;
import com.customer.message.CustomerUpdateRequest;
import com.customer.util.SolaceManager;
import com.solacesystems.jcsmp.DeliveryMode;
import com.solacesystems.jcsmp.EndpointProperties;
import com.solacesystems.jcsmp.JCSMPException;
import com.solacesystems.jcsmp.JCSMPFactory;
import com.solacesystems.jcsmp.JCSMPSession;
import com.solacesystems.jcsmp.JCSMPStreamingPublishEventHandler;
import com.solacesystems.jcsmp.Queue;
import com.solacesystems.jcsmp.TextMessage;
import com.solacesystems.jcsmp.XMLMessageProducer;

@Service
@Transactional
public class CustomerServices {

	private EntityManager entityManager;
	static Random rnd = new Random();
	private static Logger log = LogManager.getLogger(CustomerServices.class);

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	DiscoveryClient discoveryClient;

	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	private void injectPersistenceContext() {
		BaseEntity.setEntityManager(entityManager);
	}

	public String insertCustomer(CustomerInsertRequest customerInsetReq) throws ApplicationException {
		try {
			injectPersistenceContext();
			Customer customer = new Customer();
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			customer.setCustomerId(getCustUniqueId());
			customer.setName(customerInsetReq.getCustomerInsReqBody().getName());
			customer.setEmail(customerInsetReq.getCustomerInsReqBody().getEmail());
			customer.setPassword(customerInsetReq.getCustomerInsReqBody().getPassword());
			customer.setAccountType(customerInsetReq.getCustomerInsReqBody().getAccountType());
			boolean isEnabled = Boolean.valueOf(customerInsetReq.getCustomerInsReqBody().getIsEnabled());
			customer.setEnabled(isEnabled);
			customer.setLastUpdated(timestamp);
			customer.persist();
			log.info("Inserted Customer details to database");
			return customer.getCustomerId();
		} catch (Exception e) {
			System.out.println(e.toString());
			throw new ApplicationException(CustomerConstants.SERVER_ERROR, "Internal Server Error");

		}
	}

	String randomString(int len) {
		String testStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++)
			sb.append(testStr.charAt(rnd.nextInt(testStr.length())));
		return sb.toString();
	}

	public String updateCustomer(CustomerUpdateRequest custUpdateReq) throws ApplicationException {

		try {
			injectPersistenceContext();
			Customer customer = new Customer();
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			customer = Customer.findById(custUpdateReq.getCustomerUpdateReqBody().getCustomerId());
			if (custUpdateReq.getCustomerUpdateReqBody().getName() != null) {
				customer.setName(custUpdateReq.getCustomerUpdateReqBody().getName());
			}
			if (custUpdateReq.getCustomerUpdateReqBody().getAccountType() != null) {
				customer.setAccountType(custUpdateReq.getCustomerUpdateReqBody().getAccountType());
			}
			if (custUpdateReq.getCustomerUpdateReqBody().getEmail() != null) {
				customer.setEmail(custUpdateReq.getCustomerUpdateReqBody().getEmail());
			}
			if (custUpdateReq.getCustomerUpdateReqBody().getPassword() != null) {
				customer.setPassword(custUpdateReq.getCustomerUpdateReqBody().getPassword());
			}
			if (custUpdateReq.getCustomerUpdateReqBody().getIsEnabled() != null) {
				boolean isEnabled = Boolean.valueOf(custUpdateReq.getCustomerUpdateReqBody().getIsEnabled());
				customer.setEnabled(isEnabled);
			}
			customer.setLastUpdated(timestamp);
			log.info("Updated Customer details to database");
			return "true";
		} catch (Exception e) {
			throw new ApplicationException(CustomerConstants.SERVER_ERROR, "Internal Server Error");
		}
	}

	public Customer getCustomer(CustomerGetRequest custGetReq) throws ApplicationException {

		try {
			injectPersistenceContext();
			Customer customer = new Customer();
			if (custGetReq.getCustomerGetReqBody().getCustomerid() != null) {
				customer = Customer.findById(custGetReq.getCustomerGetReqBody().getCustomerid());

				return customer;
			} else {
				customer = Customer.findByEmail(custGetReq.getCustomerGetReqBody().getEmail());
				return customer;
			}
		} catch (Exception e) {
			throw new ApplicationException(CustomerConstants.SERVER_ERROR, "Internal Server Error");
		}
	}

	public String checkCustomerExist(String email) throws ApplicationException {
		injectPersistenceContext();
		Customer customer = Customer.findByEmail(email);
		if (customer == null)
			return "true";
		else
			return "false";
	}

	public Customer fetchCustomer(String custId) throws ApplicationException {
		try {
			injectPersistenceContext();
			Customer customer = Customer.findById(custId);
			return customer;
		} catch (Exception e) {
			throw new ApplicationException(CustomerConstants.SERVER_ERROR, "Internal Server Error");
		}

	}

	public String fetchMetaData() {
		injectPersistenceContext();
		return AppMetaData.findSchemaVersion();
	}

	private String getCustUniqueId() {

		String customerId = null;
		ServiceInstance instance = null;

		List<ServiceInstance> instances = discoveryClient.getInstances("TCS-POC-MS-IDGENERATOR");
		if (instances != null && instances.size() > 0) {
			instance = instances.get(0);

			System.out.println("host:" + instance.getHost());
			System.out.println("inside instance" + instance.getUri());

		}
		/*
		 * URI productUri = URI.create(String
		 * .format("http://%s:%s/idgenerator/ID?type=PR" + instance.getHost(),
		 * instance.getPort()));
		 */
		// String custId1 =
		// restTemplate.getForObject("http://ec2-52-87-243-207.compute-1.amazonaws.com:8080/idgenerator/ID?type=PR",
		// String.class);
		// System.out.println("ID:"+custId1);
		System.out.println("String URL: " + instance.getUri() + "/idgenerator/ID?type=PR");
		customerId = restTemplate.getForObject("http://TCS-POC-MS-IDGENERATOR/idgenerator/ID?type=PR", String.class);

		System.out.println("id:" + customerId);

		return customerId;
	}

	public void addMessagetoQueue(String messageHeader, String messageBody) {
		try {
			SolaceManager manager = new SolaceManager();
			JCSMPSession session = manager.getSolaceConnection();
			String queueName = "Q.interac.customerservice";
			System.out.printf("Attempting to provision the queue '%s' on the appliance.%n", queueName);
			final EndpointProperties endpointProps = new EndpointProperties();
			endpointProps.setPermission(EndpointProperties.PERMISSION_CONSUME);
			endpointProps.setAccessType(EndpointProperties.ACCESSTYPE_EXCLUSIVE);
			final Queue queue = JCSMPFactory.onlyInstance().createQueue(queueName);
			session.provision(queue, endpointProps, JCSMPSession.FLAG_IGNORE_ALREADY_EXISTS);
			final XMLMessageProducer messageProducer = session.getMessageProducer(new JCSMPStreamingPublishEventHandler() {
				
				@Override
				public void responseReceived(String messageID) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void handleError(String messageID, JCSMPException e, long timeStamp) {
					// TODO Auto-generated method stub
					
				}
			});
			
			TextMessage textMessage = JCSMPFactory.onlyInstance().createMessage(TextMessage.class);
			textMessage.setDeliveryMode(DeliveryMode.PERSISTENT);
	        textMessage.setText(messageHeader+";"+messageBody);

	        // Send message directly to the queue
	        messageProducer.send(textMessage, queue);
	        System.out.println("Message sent. Exiting.");
	        
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * @LoadBalanced
	 * 
	 * @Bean RestTemplate restTemplate() { return new RestTemplate(); }
	 */

}
