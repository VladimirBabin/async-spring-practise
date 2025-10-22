package com.vladimirbabin.github.async_spring_practice.adapters.in.web;

import com.vladimirbabin.github.async_spring_practice.application.ports.in.TradeCalculationUseCase;
import com.vladimirbabin.github.async_spring_practice.application.service.observability.ExecutorMonitoringService;
import com.vladimirbabin.github.async_spring_practice.domain.dto.ExecutorStatusDto;
import com.vladimirbabin.github.async_spring_practice.domain.dto.TradeCalculationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/dashboards")
@RequiredArgsConstructor
public class TradeDashboardController {

    private final TradeCalculationUseCase tradeCalculationUseCase;
    private final ExecutorMonitoringService executorMonitoringService;


    @GetMapping("/trade-calculations")
    public CompletableFuture<List<TradeCalculationDto>> getTradeCalculations() {
        return tradeCalculationUseCase.getTradeCalculations();
    }

    /**
     * Provides real-time monitoring of all configured ThreadPoolTaskExecutor beans.
     * This is useful for observing the load on different thread pools.
     * <p>
     * A high number of active threads or a consistently growing queue size can indicate a bottleneck,
     * often caused by slow downstream services or blocking I/O operations. While this endpoint
     * cannot definitively state a thread is "blocked", these metrics are strong indicators.
     *
     * @return A map of executor bean names to their current status.
     */
    @GetMapping("/executor-status")
    public Map<String, ExecutorStatusDto> getExecutorStatus() {
        return executorMonitoringService.getExecutorStatuses();
    }
}
