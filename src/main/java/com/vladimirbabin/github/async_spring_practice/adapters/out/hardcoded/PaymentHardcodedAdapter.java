package com.vladimirbabin.github.async_spring_practice.adapters.out.hardcoded;

import com.vladimirbabin.github.async_spring_practice.application.ports.out.GetPaymentsPort;
import com.vladimirbabin.github.async_spring_practice.domain.model.TradePayment;
import com.vladimirbabin.github.async_spring_practice.domain.model.AccountType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

@Component
public class PaymentHardcodedAdapter implements GetPaymentsPort {

    @Async
    @Override
    public CompletableFuture<List<TradePayment>> getAllPayments() {
        return CompletableFuture.completedFuture(
                LongStream.rangeClosed(1, 10)
                        .boxed()
                        .flatMap(tradeId -> {
                            LocalDate paymentDate = LocalDate.now().minusDays(tradeId);
                            return Stream.of(
                                    // Positive balance
                                    TradePayment.builder().id(tradeId * 2 - 1).tradeId(tradeId)
                                            .type(AccountType.BUYER)
                                            .amount(BigDecimal.valueOf(1500))
                                            .currency("USD")
                                            .paymentDate(paymentDate)
                                            .build(),
                                    TradePayment.builder()
                                            .id(tradeId * 2)
                                            .tradeId(tradeId)
                                            .type(AccountType.VENDOR)
                                            .amount(BigDecimal.valueOf(1000))
                                            .currency("USD")
                                            .paymentDate(paymentDate).build(),
                                    // Negative balance
                                    TradePayment.builder()
                                            .id(tradeId * 2 - 1)
                                            .tradeId(tradeId + 10)
                                            .type(AccountType.BUYER)
                                            .amount(BigDecimal.valueOf(1000))
                                            .currency("USD")
                                            .paymentDate(paymentDate).build(),
                                    TradePayment.builder()
                                            .id(tradeId * 2)
                                            .tradeId(tradeId + 10)
                                            .type(AccountType.VENDOR)
                                            .amount(BigDecimal.valueOf(1500))
                                            .currency("USD")
                                            .paymentDate(paymentDate).build(),
                                    // Zero balance
                                    TradePayment.builder()
                                            .id(tradeId * 2 - 1)
                                            .tradeId(tradeId + 20)
                                            .type(AccountType.BUYER)
                                            .amount(BigDecimal.valueOf(1000))
                                            .currency("USD")
                                            .paymentDate(paymentDate).build(),
                                    TradePayment.builder()
                                            .id(tradeId * 2)
                                            .tradeId(tradeId + 20)
                                            .type(AccountType.VENDOR)
                                            .amount(BigDecimal.valueOf(1000))
                                            .currency("USD")
                                            .paymentDate(paymentDate).build()
                            );
                        })
                        .collect(Collectors.toList())
        );
    }
}
