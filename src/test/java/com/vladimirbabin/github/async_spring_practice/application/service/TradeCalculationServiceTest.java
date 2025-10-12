package com.vladimirbabin.github.async_spring_practice.application.service;

import com.vladimirbabin.github.async_spring_practice.application.ports.out.GetPaymentsPort;
import com.vladimirbabin.github.async_spring_practice.application.ports.out.GetTradesPort;
import com.vladimirbabin.github.async_spring_practice.domain.dto.TradeCalculationDto;
import com.vladimirbabin.github.async_spring_practice.domain.model.Payment;
import com.vladimirbabin.github.async_spring_practice.domain.model.PaymentType;
import com.vladimirbabin.github.async_spring_practice.domain.model.Trade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TradeCalculationServiceTest {

    @Mock
    private GetTradesPort getTradesPort;

    @Mock
    private GetPaymentsPort getPaymentsPort;

    @InjectMocks
    private TradeCalculationService tradeCalculationService;

    @Test
    void getTradeCalculations() throws ExecutionException, InterruptedException {
        // Given
        Trade trade1 = Trade.builder().id(1L).productQuantity(10).productBuyingPrice(BigDecimal.valueOf(100)).productSellingPrice(BigDecimal.valueOf(150)).build();
        Trade trade2 = Trade.builder().id(2L).productQuantity(5).productBuyingPrice(BigDecimal.valueOf(200)).productSellingPrice(BigDecimal.valueOf(250)).build();
        List<Trade> trades = List.of(trade1, trade2);

        Payment payment1 = Payment.builder().id(1L).tradeId(1L).type(PaymentType.BUYER).amount(BigDecimal.valueOf(1500)).paymentDate(LocalDate.now()).build();
        Payment payment2 = Payment.builder().id(2L).tradeId(1L).type(PaymentType.VENDOR).amount(BigDecimal.valueOf(1000)).paymentDate(LocalDate.now()).build();
        Payment payment3 = Payment.builder().id(3L).tradeId(2L).type(PaymentType.BUYER).amount(BigDecimal.valueOf(1000)).paymentDate(LocalDate.now()).build();
        Payment payment4 = Payment.builder().id(4L).tradeId(2L).type(PaymentType.VENDOR).amount(BigDecimal.valueOf(1250)).paymentDate(LocalDate.now()).build();
        List<Payment> payments = List.of(payment1, payment2, payment3, payment4);

        when(getTradesPort.getAllTrades()).thenReturn(CompletableFuture.completedFuture(trades));
        when(getPaymentsPort.getAllPayments()).thenReturn(CompletableFuture.completedFuture(payments));

        // When
        CompletableFuture<List<TradeCalculationDto>> resultFuture = tradeCalculationService.getTradeCalculations();
        List<TradeCalculationDto> result = resultFuture.get();

        // Then
        assertEquals(2, result.size());

        TradeCalculationDto result1 = result.stream().filter(r -> r.getTradeId() == 1L).findFirst().get();
        assertEquals(0, new BigDecimal("1000").compareTo(result1.getGrossProfit()));
        assertEquals(0, new BigDecimal("1500").compareTo(result1.getCostOfGoods()));
        assertEquals(0, new BigDecimal("500").compareTo(result1.getBalance()));

        TradeCalculationDto result2 = result.stream().filter(r -> r.getTradeId() == 2L).findFirst().get();
        assertEquals(0, new BigDecimal("1000").compareTo(result2.getGrossProfit()));
        assertEquals(0, new BigDecimal("1250").compareTo(result2.getCostOfGoods()));
        assertEquals(0, new BigDecimal("-250").compareTo(result2.getBalance()));
    }
}
