package com.vladimirbabin.github.async_spring_practice.application.service;

import com.vladimirbabin.github.async_spring_practice.application.ports.in.TradeCalculationUseCase;
import com.vladimirbabin.github.async_spring_practice.application.ports.out.GetPaymentsPort;
import com.vladimirbabin.github.async_spring_practice.application.ports.out.GetTradesPort;
import com.vladimirbabin.github.async_spring_practice.domain.dto.TradeCalculationDto;
import com.vladimirbabin.github.async_spring_practice.domain.model.TradePayment;
import com.vladimirbabin.github.async_spring_practice.domain.model.Trade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TradeCalculationsService implements TradeCalculationUseCase {

    private final GetTradesPort getTradesPort;
    private final GetPaymentsPort getPaymentsPort;

    private final TradeMetricsCalculator tradeMetricsCalculator;
    private final TradePaymentMetricsCalculator tradePaymentMetricsCalculator;

    @Override
    public CompletableFuture<List<TradeCalculationDto>> getTradeCalculations() {
        CompletableFuture<List<Trade>> tradesFuture = getTradesPort.getAllTrades();
        CompletableFuture<List<TradePayment>> paymentsFuture = getPaymentsPort.getAllPayments();

        return tradesFuture.thenCombine(paymentsFuture, (trades, payments) -> {
            Map<Long, List<TradePayment>> paymentsByTradeId = payments.stream()
                    .collect(Collectors.groupingBy(TradePayment::getTradeId));

            return trades.stream()
                    .map(trade -> TradeCalculationDto.builder()
                            .tradeId(trade.getId())
                            .grossProfit(
                                    tradeMetricsCalculator.calculateGrossProfit(trade)
                            )
                            .costOfGoods(
                                    tradeMetricsCalculator.calculateCostOfGoods(trade)
                            )
                            .balance(
                                    tradePaymentMetricsCalculator.calculateTradePaymentsBalance(
                                            paymentsByTradeId.get(trade.getId())
                                    )
                            )
                            .build())
                    .collect(Collectors.toList());
        });
    }


}
