package com.vladimirbabin.github.async_spring_practice.application.service;

import com.vladimirbabin.github.async_spring_practice.application.ports.in.TradeCalculationUseCase;
import com.vladimirbabin.github.async_spring_practice.application.ports.out.GetPaymentsPort;
import com.vladimirbabin.github.async_spring_practice.application.ports.out.GetTradesPort;
import com.vladimirbabin.github.async_spring_practice.domain.dto.TradeCalculationDto;
import com.vladimirbabin.github.async_spring_practice.domain.model.Trade;
import com.vladimirbabin.github.async_spring_practice.domain.model.TradePayment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class TradeCalculationsServiceTest {

    private static final Long TRADE_ID_1 = 1L;
    private static final Long TRADE_ID_2 = 2L;
    private static final BigDecimal GROSS_PROFIT_1 = new BigDecimal("1000.50");
    private static final BigDecimal COST_OF_GOODS_1 = new BigDecimal("500.25");
    private static final BigDecimal BALANCE_1 = new BigDecimal("250.00");
    private static final BigDecimal GROSS_PROFIT_2 = new BigDecimal("200.00");
    private static final BigDecimal COST_OF_GOODS_2 = new BigDecimal("150.00");
    private static final BigDecimal BALANCE_2 = new BigDecimal("50.75");

    @Mock
    private GetTradesPort getTradesPort;
    @Mock
    private GetPaymentsPort getPaymentsPort;
    @Mock
    private TradeMetricsCalculator tradeMetricsCalculator;
    @Mock
    private TradePaymentMetricsCalculator tradePaymentMetricsCalculator;

    private TradeCalculationUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new TradeCalculationsService(
                getTradesPort,
                getPaymentsPort,
                tradeMetricsCalculator,
                tradePaymentMetricsCalculator
        );
    }

    @Nested
    @DisplayName("Happy Path Scenarios")
    class HappyPath {

        @Test
        @DisplayName("should return correct trade calculations when trades and payments exist")
        void shouldReturnCorrectTradeCalculations_WhenTradesAndPaymentsExist() {
            // given
            Trade trade1 = createTrade(TRADE_ID_1);
            Trade trade2 = createTrade(TRADE_ID_2);
            List<Trade> trades = List.of(trade1, trade2);

            TradePayment payment1ForTrade1 = createPayment(TRADE_ID_1, new BigDecimal("100.00"));
            TradePayment payment2ForTrade1 = createPayment(TRADE_ID_1, new BigDecimal("150.00"));
            TradePayment payment1ForTrade2 = createPayment(TRADE_ID_2, new BigDecimal("50.75"));
            List<TradePayment> payments = List.of(payment1ForTrade1, payment2ForTrade1, payment1ForTrade2);

            List<TradePayment> paymentsForTrade1 = List.of(payment1ForTrade1, payment2ForTrade1);
            List<TradePayment> paymentsForTrade2 = List.of(payment1ForTrade2);

            given(getTradesPort.getAllTrades())
                    .willReturn(CompletableFuture.completedFuture(trades));
            given(getPaymentsPort.getAllPayments())
                    .willReturn(CompletableFuture.completedFuture(payments));

            given(tradeMetricsCalculator.calculateGrossProfit(trade1))
                    .willReturn(GROSS_PROFIT_1);
            given(tradeMetricsCalculator.calculateCostOfGoods(trade1))
                    .willReturn(COST_OF_GOODS_1);
            given(tradePaymentMetricsCalculator.calculateTradePaymentsBalance(paymentsForTrade1))
                    .willReturn(BALANCE_1);

            given(tradeMetricsCalculator.calculateGrossProfit(trade2))
                    .willReturn(GROSS_PROFIT_2);
            given(tradeMetricsCalculator.calculateCostOfGoods(trade2))
                    .willReturn(COST_OF_GOODS_2);
            given(tradePaymentMetricsCalculator.calculateTradePaymentsBalance(paymentsForTrade2))
                    .willReturn(BALANCE_2);

            // when
            CompletableFuture<List<TradeCalculationDto>> resultFuture = useCase.getTradeCalculations();

            // then
            then(resultFuture).succeedsWithin(Duration.ofSeconds(1))
                    .satisfies(resultList -> {
                        then(resultList).hasSize(2);

                        then(resultList).first()
                                .satisfies(dto -> {
                                    then(dto.getTradeId()).isEqualTo(TRADE_ID_1);
                                    then(dto.getGrossProfit()).isEqualTo(GROSS_PROFIT_1);
                                    then(dto.getCostOfGoods()).isEqualTo(COST_OF_GOODS_1);
                                    then(dto.getBalance()).isEqualTo(BALANCE_1);
                                });

                        then(resultList.get(1))
                                .satisfies(dto -> {
                                    then(dto.getTradeId()).isEqualTo(TRADE_ID_2);
                                    then(dto.getGrossProfit()).isEqualTo(GROSS_PROFIT_2);
                                    then(dto.getCostOfGoods()).isEqualTo(COST_OF_GOODS_2);
                                    then(dto.getBalance()).isEqualTo(BALANCE_2);
                                });
                    });
        }
    }

    @Nested
    @DisplayName("Edge Case Scenarios")
    class EdgeCases {

        @Test
        @DisplayName("should return calculations with zero balance when no payments exist")
        void shouldReturnCalculationsWithZeroBalance_WhenNoPaymentsExist() {
            // given
            Trade trade1 = createTrade(TRADE_ID_1);
            List<Trade> trades = List.of(trade1);

            given(getTradesPort.getAllTrades())
                    .willReturn(CompletableFuture.completedFuture(trades));
            given(getPaymentsPort.getAllPayments())
                    .willReturn(CompletableFuture.completedFuture(Collections.emptyList()));

            given(tradeMetricsCalculator.calculateGrossProfit(trade1))
                    .willReturn(GROSS_PROFIT_1);
            given(tradeMetricsCalculator.calculateCostOfGoods(trade1))
                    .willReturn(COST_OF_GOODS_1);

            given(tradePaymentMetricsCalculator.calculateTradePaymentsBalance(null))
                    .willReturn(BigDecimal.ZERO);

            // when
            CompletableFuture<List<TradeCalculationDto>> resultFuture = useCase.getTradeCalculations();

            // then
            then(resultFuture).succeedsWithin(Duration.ofSeconds(1))
                    .satisfies(resultList -> {
                        then(resultList).hasSize(1);
                        then(resultList).first()
                                .satisfies(dto -> {
                                    then(dto.getTradeId()).isEqualTo(TRADE_ID_1);
                                    then(dto.getGrossProfit()).isEqualTo(GROSS_PROFIT_1);
                                    then(dto.getCostOfGoods()).isEqualTo(COST_OF_GOODS_1);
                                    then(dto.getBalance()).isZero();
                                });
                    });
        }

        @DisplayName("should return an empty list when no trades exist")
        @ParameterizedTest(name = "given {1}")
        @MethodSource("noTradesScenarios")
        void shouldReturnEmptyList_WhenNoTradesExist(List<TradePayment> payments, String scenarioDescription) {
            // given
            given(getTradesPort.getAllTrades())
                    .willReturn(CompletableFuture.completedFuture(Collections.emptyList()));
            given(getPaymentsPort.getAllPayments())
                    .willReturn(CompletableFuture.completedFuture(payments));

            // when
            CompletableFuture<List<TradeCalculationDto>> resultFuture = useCase.getTradeCalculations();

            // then
            then(resultFuture).succeedsWithin(Duration.ofSeconds(1))
                    .isEqualTo(Collections.emptyList());
            verifyNoInteractions(tradeMetricsCalculator, tradePaymentMetricsCalculator);
        }

        private static Stream<Arguments> noTradesScenarios() {
            return Stream.of(
                    Arguments.of(Collections.emptyList(), "no payments exist"),
                    Arguments.of(List.of(createPayment(TRADE_ID_1, BigDecimal.TEN)), "some payments exist")
            );
        }
    }

    private Trade createTrade(Long id) {
        return Trade.builder()
                .id(id)
                .build();
    }

    private static TradePayment createPayment(Long tradeId, BigDecimal amount) {
        return TradePayment.builder()
                .tradeId(tradeId)
                .amount(amount)
                .build();
    }
}
