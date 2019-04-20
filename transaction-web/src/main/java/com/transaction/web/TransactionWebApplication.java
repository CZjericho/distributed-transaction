package com.transaction.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource(locations={"classpath:dubbo-consumer.xml"})
@ComponentScan(basePackages = {"com.transaction.common.redis", "com.transaction.web"})
public class TransactionWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(TransactionWebApplication.class, args);
	}

}
