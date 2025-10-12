package com.vladimirbabin.github.async_spring_practice.adapters.in.web;

import com.vladimirbabin.github.async_spring_practice.application.ports.in.TradeCalculationUseCase;
import com.vladimirbabin.github.async_spring_practice.domain.dto.TradeCalculationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/dashboards")
@RequiredArgsConstructor
public class TradeDashboardController {

    private final TradeCalculationUseCase tradeCalculationUseCase;

    @GetMapping("/trade-calculations")
    public CompletableFuture<List<TradeCalculationDto>> getTradeCalculations() {
        return tradeCalculationUseCase.getTradeCalculations();
    }
}
