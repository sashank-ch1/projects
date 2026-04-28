package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

// @EnableScheduling activates @Scheduled methods like our location update job
@SpringBootApplication
@EnableScheduling
public class LogiTractServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(LogiTractServiceApplication.class, args);
	}

}
