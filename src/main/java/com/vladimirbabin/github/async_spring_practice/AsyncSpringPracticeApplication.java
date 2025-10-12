package com.vladimirbabin.github.async_spring_practice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class AsyncSpringPracticeApplication {

	public static void main(String[] args) {
		SpringApplication.run(AsyncSpringPracticeApplication.class, args);
	}

}
