package com.vladimirbabin.github.async_spring_practice.application.service;

import com.vladimirbabin.github.async_spring_practice.application.ports.in.TradeCalculationUseCase;
import com.vladimirbabin.github.async_spring_practice.application.ports.out.GetPaymentsPort;
import com.vladimirbabin.github.async_spring_practice.application.ports.out.GetTradesPort;
import com.vladimirbabin.github.async_spring_practice.domain.dto.TradeCalculationDto;
import com.vladimirbabin.github.async_spring_practice.domain.model.Payment;
import com.vladimirbabin.github.async_spring_practice.domain.model.PaymentType;
import com.vladimirbabin.github.async_spring_practice.domain.model.Trade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TradeCalculationService implements TradeCalculationUseCase {

    private final GetTradesPort getTradesPort;
    private final GetPaymentsPort getPaymentsPort;

    @Override
    public CompletableFuture<List<TradeCalculationDto>> getTradeCalculations() {
        CompletableFuture<List<Trade>> tradesFuture = getTradesPort.getAllTrades();
        CompletableFuture<List<Payment>> paymentsFuture = getPaymentsPort.getAllPayments();

        return tradesFuture.thenCombine(paymentsFuture, (trades, payments) -> {
            Map<Long, List<Payment>> paymentsByTradeId = payments.stream()
                    .collect(Collectors.groupingBy(Payment::getTradeId));

            return trades.stream()
                    .map(trade -> {

                        // TODO: extract helper methods for readability
                        BigDecimal grossProfit = trade.getProductBuyingPrice()
                                .multiply(
                                        BigDecimal
                                                .valueOf(
                                                        trade.getProductQuantity()));
                        BigDecimal costOfGoods = trade.getProductSellingPrice()
                                .multiply(
                                        BigDecimal
                                                .valueOf(
                                                        trade.getProductQuantity()));

                        List<Payment> tradePayments = paymentsByTradeId.get(trade.getId());
                        BigDecimal balance = BigDecimal.ZERO;

                        // TODO: add handling of default
                        // TODO: extract to a separate component
                        if (tradePayments != null) {
                            BigDecimal buyerPayments = tradePayments.stream()
                                    .filter(p -> p.getType() == PaymentType.BUYER)
                                    .map(Payment::getAmount)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                            BigDecimal vendorPayments = tradePayments.stream()
                                    .filter(p -> p.getType() == PaymentType.VENDOR)
                                    .map(Payment::getAmount)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                            balance = buyerPayments.subtract(vendorPayments);
                        }

                        return TradeCalculationDto.builder()
                                .tradeId(trade.getId())
                                .grossProfit(grossProfit)
                                .costOfGoods(costOfGoods)
                                .balance(balance)
                                .build();
                    })
                    .collect(Collectors.toList());
        });
    }
}
