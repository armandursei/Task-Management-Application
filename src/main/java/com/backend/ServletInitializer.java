/**
 * Clasa pentru configurarea aplicației în contextul unui server servlet (Tomcat).
 * Este utilizată pentru a face aplicația deployabilă pe un server web.
 * @author Ursei George-Armand
 * @version 10 Decembrie 2024
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
