package com.vladimirbabin.github.async_spring_practice.application.ports.out;

import com.vladimirbabin.github.async_spring_practice.domain.model.Trade;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface GetTradesPort {
    CompletableFuture<List<Trade>> getAllTrades();
}
