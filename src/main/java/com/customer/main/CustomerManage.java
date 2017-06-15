package com.customer.main;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.LocalEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaSessionFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;



@Configuration
@EnableAutoConfiguration
@EntityScan("com.customer.Entity")
@ComponentScan("com.customer.*")
@EnableEurekaClient
@ImportResource({"classpath:servlet-context.xml"})
@EnableTransactionManagement
@SpringBootApplication
@RibbonClient(name = "TCS-POC-MS-IDGENERATOR", configuration = RibbonConfig.class)
public class CustomerManage extends SpringBootServletInitializer {

	@Autowired
	  private Environment env;

	  @Autowired
	  private DataSource dataSource;

	  @Autowired
	  private LocalContainerEntityManagerFactoryBean entityManagerFactory;
	  
	  @Override
	    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
	        return application.sources(CustomerManage.class);
	    }
	
	public static void main(String[] args) {
		
		 System.out.println("Hello");
	        SpringApplication.run(CustomerManage.class, args);

	}
	
	@LoadBalanced
	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}
	
	@Bean
	  public DataSource dataSource() {
	    DriverManagerDataSource dataSource = new DriverManagerDataSource();
	    dataSource.setDriverClassName(env.getProperty("db.driver"));
	    dataSource.setUrl(env.getProperty("db.url"));
	    dataSource.setUsername(env.getProperty("db.username"));
	    dataSource.setPassword(env.getProperty("db.password"));
	    return dataSource;
	  }

	  /**
	   * Declare the JPA entity manager factory.
	   */
	  @Bean
	  public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
	    LocalContainerEntityManagerFactoryBean entityManagerFactory =
	        new LocalContainerEntityManagerFactoryBean();
	    
	    entityManagerFactory.setDataSource(dataSource);
	    
	    // Classpath scanning of @Component, @Service, etc annotated class
	    entityManagerFactory.setPackagesToScan(
	        env.getProperty("entitymanager.packagesToScan"));
	    
	    // Vendor adapter
	    HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
	    entityManagerFactory.setJpaVendorAdapter(vendorAdapter);
	    
	    // Hibernate properties
	    Properties additionalProperties = new Properties();
	    additionalProperties.put(
	        "hibernate.dialect", 
	        env.getProperty("hibernate.dialect"));
	    additionalProperties.put(
	        "hibernate.show_sql", 
	        env.getProperty("hibernate.show_sql"));
	    additionalProperties.put(
	        "hibernate.hbm2ddl.auto", 
	        env.getProperty("hibernate.hbm2ddl.auto"));
	    entityManagerFactory.setJpaProperties(additionalProperties);
	    
	    return entityManagerFactory;
	  }

	  /**
	   * Declare the transaction manager.
	   */
	  @Bean
	  public JpaTransactionManager transactionManager() {
	    JpaTransactionManager transactionManager = 
	        new JpaTransactionManager();
	    transactionManager.setEntityManagerFactory(
	        entityManagerFactory.getObject());
	    return transactionManager;
	  }
	  
	  /**
	   * PersistenceExceptionTranslationPostProcessor is a bean post processor
	   * which adds an advisor to any bean annotated with Repository so that any
	   * platform-specific exceptions are caught and then rethrown as one
	   * Spring's unchecked data access exceptions (i.e. a subclass of 
	   * DataAccessException).
	   */
	  @Bean
	  public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
	    return new PersistenceExceptionTranslationPostProcessor();
	  }
	  
	  @Bean
	    public HibernateJpaSessionFactoryBean sessionFactory(EntityManagerFactory emf) {
	         HibernateJpaSessionFactoryBean factory = new HibernateJpaSessionFactoryBean();
	         factory.setEntityManagerFactory(emf);
	         return factory;
	    }

	 


	
	 

	    
}
