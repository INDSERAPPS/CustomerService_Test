package com.customer.Entity;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.TypedQuery;

import org.hibernate.annotations.Type;
import org.springframework.stereotype.Component;

import com.customer.constants.CustomerConstants;
import com.customer.exceptions.ApplicationException;

import javax.annotation.Nullable;


@Component
@Entity
@Table(name= "Customers")
@NamedQuery(name = "Customer.findByEmail", query = "select c from Customer c where c.email=:emailId")

public class Customer extends BaseEntity {
	
	 public static final String EMAIL_QUERY = "Customer.findByEmail" ;
	
	@Id
	@Column(name = "CUSTOMER_ID")
	private String customerId ;
	
	@Column(name = "CUSTOMER_NAME")
	private String name ;
	
	@Column(name = "CUSTOMER_EMAIL")
	private String email ;
	
	@Column(name = "CUSTOMER_PASSWORD")
	private String password ;
	
	@Column(name = "ACCOUNT_TYPE")
	private String accountType ;
	
	@Type(type="yes_no")
	@Column(name = "ISENABLED" ,columnDefinition = "char")
	private boolean isEnabled ;
	
	@Column(name = "DATE_UPDATED")
	private Timestamp lastUpdated ;
	
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getAccountType() {
		return accountType;
	}
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	public boolean isEnabled() {
		return isEnabled;
	}
	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}
	public Date getLastUpdated() {
		return lastUpdated;
	}
	public void setLastUpdated(Timestamp lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	
	
	@Nullable
	public static Customer findById(String customerId) throws ApplicationException {
		try{
	        return em().find(Customer.class, customerId);
		}catch(Exception e){
			throw new ApplicationException(CustomerConstants.SERVER_ERROR,"Internal Server Error") ;
		}
	    }
	
	@Nullable
	 
	public static Customer findByEmail(String emailId) throws ApplicationException {
		System.out.println("Inside method");
		Query query = em().createNamedQuery("Customer.findByEmail");
		
		query.setParameter("emailId", emailId);
		System.out.println("query created");
		Customer customer=(Customer) getSingleResultOrNull(query);
		return customer; 
		
	}
}
