package com.vladimirbabin.github.async_spring_practice.domain.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * A DTO representing the real-time status of a ThreadPoolTaskExecutor bean.
 */
@Data
@Builder
public class ExecutorStatusDto {
    // Configuration properties
    private String threadNamePrefix;
    private int corePoolSize;
    private int maxPoolSize;
    private int queueCapacity;

    // Real-time metrics
    private int currentPoolSize;
    private int activeThreads;
    private int queuedTasks;
    private long completedTaskCount;

    // Granular thread details
    private List<ThreadStateDto> threads;
}
