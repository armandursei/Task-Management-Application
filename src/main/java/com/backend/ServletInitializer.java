/**
 * Configures the application when it is deployed as a WAR to an external
 * servlet container (e.g. Tomcat).
 * @author Ursei George-Armand
 * @version 10 December 2024
 */


package com.backend;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

public class ServletInitializer extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(BackendApplication.class);
	}

}
