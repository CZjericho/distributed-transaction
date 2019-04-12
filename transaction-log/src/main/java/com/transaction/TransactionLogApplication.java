package com.transaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource(locations={
		"classpath:dubbo-provider.xml"})
public class TransactionLogApplication {

	public static void main(String[] args) {
		SpringApplication.run(TransactionLogApplication.class, args);
	}

}
