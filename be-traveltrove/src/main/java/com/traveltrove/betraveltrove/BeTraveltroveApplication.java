package com.traveltrove.betraveltrove;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class BeTraveltroveApplication {

	public static void main(String[] args) {
		SpringApplication.run(BeTraveltroveApplication.class, args);
	}

	@Bean
	RestTemplate restTemplate()
	{
		return new RestTemplate();
	}

}
