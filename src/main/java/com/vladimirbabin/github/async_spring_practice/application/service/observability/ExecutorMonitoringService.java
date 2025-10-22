package com.vladimirbabin.github.async_spring_practice.application.service.observability;

import com.vladimirbabin.github.async_spring_practice.domain.dto.ExecutorStatusDto;
import com.vladimirbabin.github.async_spring_practice.domain.dto.ThreadStateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExecutorMonitoringService {

    private final ApplicationContext applicationContext;

    /**
     * Finds all ThreadPoolTaskExecutor beans in the Spring context and reports their current status.
     *
     * @return A map where the key is the executor's bean name and the value is its status DTO.
     */
    public Map<String, ExecutorStatusDto> getExecutorStatuses() {
        // This retrieves all beans of the specified type, along with their names.
        Map<String, ThreadPoolTaskExecutor> executorBeans = applicationContext.getBeansOfType(ThreadPoolTaskExecutor.class);

        return executorBeans.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> buildStatusDto(entry.getValue())
                ));
    }

    private ExecutorStatusDto buildStatusDto(ThreadPoolTaskExecutor executor) {
        return ExecutorStatusDto.builder()
                .threadNamePrefix(executor.getThreadNamePrefix())
                .corePoolSize(executor.getCorePoolSize())
                .maxPoolSize(executor.getMaxPoolSize())
                .queueCapacity(executor.getQueueCapacity())
                .currentPoolSize(executor.getPoolSize())
                .activeThreads(executor.getActiveCount())
                .queuedTasks(executor.getThreadPoolExecutor().getQueue().size())
                .completedTaskCount(executor.getThreadPoolExecutor().getCompletedTaskCount())
                .threads(
                        findThreadsByPrefix(executor.getThreadNamePrefix())
                )
                .build();
    }

    /**
     * Scans all live threads in the JVM and returns details for those matching the given prefix.
     *
     * @param prefix The thread name prefix to search for.
     * @return A list of ThreadDetailDto objects.
     */
    private List<ThreadStateDto> findThreadsByPrefix(String prefix) {
        if (prefix == null || prefix.isBlank()) {
            return Collections.emptyList();
        }
        // Thread.getAllStackTraces().keySet() is a safe, standard way to get a snapshot of all live threads.
        return Thread.getAllStackTraces().keySet().stream()
                .filter(thread -> thread.getName().startsWith(prefix))
                .map(thread -> ThreadStateDto.builder()
                        .id(thread.threadId())
                        .name(thread.getName())
                        .state(thread.getState())
                        .build())
                .collect(Collectors.toList());
    }
}
