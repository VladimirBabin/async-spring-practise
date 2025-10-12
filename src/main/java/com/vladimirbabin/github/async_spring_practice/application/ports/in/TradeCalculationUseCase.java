package com.vladimirbabin.github.async_spring_practice.application.ports.in;

import com.vladimirbabin.github.async_spring_practice.domain.dto.TradeCalculationDto;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface TradeCalculationUseCase {
    CompletableFuture<List<TradeCalculationDto>> getTradeCalculations();
}
