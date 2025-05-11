package com.smartinvoice;

import org.springframework.boot.SpringApplication;

public class TestSmartInvoiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(SmartInvoiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
