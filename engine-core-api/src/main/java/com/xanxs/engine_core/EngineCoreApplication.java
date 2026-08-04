package com.xanxs.engine_core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.xanxs.engine_core.client")

public class EngineCoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(EngineCoreApplication.class, args);
	}

}
