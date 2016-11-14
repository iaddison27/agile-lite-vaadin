package org.test.config;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.web.WebApplicationInitializer;

public class MyWebappInitializer implements WebApplicationInitializer {

    @Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		FilterRegistration.Dynamic encodingFilter = servletContext.addFilter("xxFilter", new OpenEntityManagerInViewFilter());
		encodingFilter.addMappingForUrlPatterns(null, false, "/*");
	}

}
