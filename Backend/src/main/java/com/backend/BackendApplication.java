/**
 * Clasa principală a aplicației care inițializează Spring Boot.
 * @author Ursei George-Armand
 * @version 10 Decembrie 2024
 */


package com.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {

		SpringApplication.run(BackendApplication.class, args);
		System.out.println("App started");
	}

}
