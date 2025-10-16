package com.vladimirbabin.github.async_spring_practice.adapters.out.persistence;

import com.vladimirbabin.github.async_spring_practice.application.ports.out.GetTradesPort;
import com.vladimirbabin.github.async_spring_practice.domain.model.Trade;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TradePersistenceAdapter implements GetTradesPort {

    private final TradeRepository tradeRepository;

    // TODO: try supply async with custom executor
    // TODO: try flux in a separate git branch (return flux instead of CompletableFuture)
    // executors? try different realisations of executors (simple, thread full task, etc.). Differs by settings that you define.
    // Check how to create your own executors (put tasks in queue)
    @Async
    @Override
    public CompletableFuture<List<Trade>> getAllTrades() {
        return CompletableFuture.completedFuture(
                tradeRepository.findAll().stream()
                        .map(this::toDomain)
                        .collect(Collectors.toList())
        );
    }

    private Trade toDomain(TradeEntity entity) {
        return Trade.builder()
                .id(entity.getId())
                .buyingAccountId(entity.getBuyingAccountId())
                .vendorAccountId(entity.getVendorAccountId())
                .productName(entity.getProductName())
                .productQuantity(entity.getProductQuantity())
                .productBuyingPrice(entity.getProductBuyingPrice())
                .productSellingPrice(entity.getProductSellingPrice())
                .build();
    }
}
