package com.vladimirbabin.github.async_spring_practice.domain.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Represents the state of a single thread.
 */
@Data
@Builder
public class ThreadStateDto {
    private long id;
    private String name;
    private Thread.State state;
}
