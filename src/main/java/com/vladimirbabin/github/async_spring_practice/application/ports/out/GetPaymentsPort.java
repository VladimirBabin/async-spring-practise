package com.vladimirbabin.github.async_spring_practice.application.ports.out;

import com.vladimirbabin.github.async_spring_practice.domain.model.TradePayment;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface GetPaymentsPort {
    CompletableFuture<List<TradePayment>> getAllPayments();
}
