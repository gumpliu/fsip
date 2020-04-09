package com.yss.fsip.web;

import com.yss.fsip.web.config.FSIPJndiProperties;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jndi.JndiObjectFactoryBean;

import javax.naming.NamingException;
import javax.sql.DataSource;

@Configuration
@AutoConfigureBefore(ServletWebServerFactoryAutoConfiguration.class)
@ConditionalOnProperty(prefix="spring.datasource.fsip", name="factory")
public class JNDIAutoConfiguration {

	private static final String JNDI_PREFIX= "java:/comp/env/";

	@Autowired
	private DataSourceProperties dataSourceProperties;

	@Autowired
	private FSIPJndiProperties fsipJndiProperties;

	@Bean
	public TomcatServletWebServerFactory tomcatServletWebServerFactory() {
		return new TomcatServletWebServerFactory() {
			@Override
			protected TomcatWebServer getTomcatWebServer(Tomcat tomcat) {
				tomcat.enableNaming();
				return super.getTomcatWebServer(tomcat);
			}

			@Override
			protected void postProcessContext(Context context) {
				ContextResource resource = new ContextResource();
				resource.setName(getShortJndiName());
				resource.setType(DataSource.class.getName());
				resource.setProperty("factory", fsipJndiProperties.getFactory());
				resource.setProperty("driverClassName", dataSourceProperties.getDriverClassName());
				resource.setProperty("url", dataSourceProperties.getUrl());
				resource.setProperty("username", dataSourceProperties.getUsername());
				resource.setProperty("password", dataSourceProperties.getPassword());
				context.getNamingResources().addResource(resource);
			}
		};
	}

	@Bean
	public DataSource jndiDataSource() throws IllegalArgumentException, NamingException {
		JndiObjectFactoryBean bean = new JndiObjectFactoryBean();
		bean.setJndiName(getJndiName());
		bean.setProxyInterface(DataSource.class);
		bean.setLookupOnStartup(false);
		bean.afterPropertiesSet();
		return (DataSource) bean.getObject();
	}

	private String getShortJndiName() {
		String jndiNamePro = dataSourceProperties.getJndiName();

		if(jndiNamePro.startsWith(JNDI_PREFIX)) {
			return jndiNamePro.replace(JNDI_PREFIX, "");
		}

		return jndiNamePro;

	}

	private String getJndiName() {
		String jndiNamePro = dataSourceProperties.getJndiName();

		if(!jndiNamePro.startsWith(JNDI_PREFIX)) {
			return JNDI_PREFIX.concat(jndiNamePro);
		}

		return jndiNamePro;

	}
}