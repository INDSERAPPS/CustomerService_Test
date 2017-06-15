package com.customer.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Query;
import javax.persistence.Table;

import org.springframework.stereotype.Component;

@Component
@Entity
@Table(name= "app_data")
@NamedQuery(name = "AppMetaData.findSchema", query = "select a from AppMetaData a")
public class AppMetaData extends BaseEntity {
	
	@Id
	@Column(name = "SCHEMA_VERSION")
	private String schemaVersion ;

	public String getSchemaVersion() {
		return schemaVersion;
	}

	public void setSchemaVersion(String schemaVersion) {
		this.schemaVersion = schemaVersion;
	}
	
	public static String findSchemaVersion()
	{
Query query = em().createNamedQuery("AppMetaData.findSchema");
		
		AppMetaData appMetaData=(AppMetaData) getSingleResultOrNull(query);
		return appMetaData.getSchemaVersion() ;
	}
	
	

}
