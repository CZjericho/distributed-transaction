package com.transaction.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@ImportResource(locations={
		"classpath:dubbo-consumer.xml",
		"classpath:dubbo-provider.xml"})
public class TransactionOrderApplication {

	public static void main(String[] args) {
		SpringApplication.run(TransactionOrderApplication.class, args);
	}

}
