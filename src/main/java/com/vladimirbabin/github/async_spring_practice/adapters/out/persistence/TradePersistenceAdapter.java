package com.vladimirbabin.github.async_spring_practice.adapters.out.persistence;

import com.vladimirbabin.github.async_spring_practice.adapters.out.persistence.mapper.TradeMapper;
import com.vladimirbabin.github.async_spring_practice.application.ports.out.GetTradesPort;
import com.vladimirbabin.github.async_spring_practice.domain.model.Trade;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * So what happens in reality with @Async?
 * If @Async is configured correctly, the entire blocking operation (findAll + mapping)
 * is simply shifted from the calling thread (e.g., a web request thread)
 * to a background thread from Spring's task executor pool.
 * <p>
 * <p>
 * Is it asynchronous?
 *      Yes, from the caller's perspective. The web thread is freed up immediately.
 * <p>
 * Is it efficient?
 *      Not really. You are still blocking a thread.
 *      Instead of blocking a valuable web thread, you are now blocking a thread from your async pool.
 *      If you have many concurrent requests, you can easily exhaust your async thread pool,
 *      and new tasks will be queued or rejected.
 *      This is better than crashing your web server, but it's not true non-blocking I/O.
 */
@Component
@RequiredArgsConstructor
public class TradePersistenceAdapter implements GetTradesPort {

    private final TradeRepository tradeRepository;
    private final TradeMapper tradeMapper;
    private final Executor databaseTaskExecutor;

    // TODO: try supply async with custom executor
    // TODO: try flux in a separate git branch (return flux instead of CompletableFuture)
    @Async
    @Override
    public CompletableFuture<List<Trade>> getAllTrades() {
        return CompletableFuture.supplyAsync(() -> {
            // This entire block of code will be executed on a background thread.
            // This is a blocking database call.
            List<TradeEntity> entities = tradeRepository.findAll();

            // This is the mapping logic.
            return entities.stream()
                    .map(tradeMapper::toDomain)
                    .collect(Collectors.toList());
        }, databaseTaskExecutor); // This logic now runs on your dedicated "db-io-" thread pool
    }

}
