package com.goviesco.orders;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// @SpringBootApplication is a meta-annotation that pulls in component scanning, autoconfiguration, and property support.
// Spring Boot will fire up a servlet container and serve up the service.
@SpringBootApplication
public class OrdersApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrdersApplication.class, args);
	}

}
