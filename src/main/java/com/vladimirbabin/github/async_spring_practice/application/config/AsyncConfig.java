package com.vladimirbabin.github.async_spring_practice.application.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;


@Configuration
public class AsyncConfig {

    public static final String DATABASE_EXECUTOR = "databaseTaskExecutor";

    @Bean(name = DATABASE_EXECUTOR)
    public Executor databaseTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);   // Number of threads to keep in the pool
        executor.setMaxPoolSize(50);    // Max number of threads allowed
        executor.setQueueCapacity(100); // Max number of tasks to queue before rejecting
        executor.setThreadNamePrefix("db-io-"); // Crucial for observability
        executor.initialize();
        return executor;
    }

}
