package com.transaction.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource(locations={"classpath:dubbo-consumer.xml"})
public class TransactionWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(TransactionWebApplication.class, args);
	}

}
