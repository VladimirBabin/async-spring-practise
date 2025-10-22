package com.vladimirbabin.github.async_spring_practice.application.service;

import com.vladimirbabin.github.async_spring_practice.domain.model.AccountType;
import com.vladimirbabin.github.async_spring_practice.domain.model.TradePayment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("TradePaymentMetricsCalculator")
class TradePaymentMetricsCalculatorTest {

    private TradePaymentMetricsCalculator calculator;

    @BeforeEach
    void setUp() {
        // given
        calculator = new TradePaymentMetricsCalculator();
    }

    @Nested
    @DisplayName("calculateTradePaymentsBalance")
    class CalculateTradePaymentsBalance {

        @ParameterizedTest(name = "should return zero when the payment list is {0}")
        @NullAndEmptySource
        @DisplayName("should return zero when the payment list is null or empty")
        void shouldReturnZero_WhenPaymentListIsNullOrEmpty(List<TradePayment> tradePayments) {
            // when
            BigDecimal balance = calculator.calculateTradePaymentsBalance(tradePayments);

            // then
            then(balance).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @ParameterizedTest(name = "should calculate balance correctly {0}")
        @MethodSource("balanceTestCases")
        @DisplayName("should calculate balance correctly for various payment combinations")
        void shouldCalculateCorrectBalance_WhenGivenVariousPayments(
                String description, List<TradePayment> payments, BigDecimal expectedBalance) {
            // when
            BigDecimal actualBalance = calculator.calculateTradePaymentsBalance(payments);

            // then
            then(actualBalance).isEqualByComparingTo(expectedBalance);
        }

        static Stream<Arguments> balanceTestCases() {
            return Stream.of(
                    arguments(
                            "when buyer payments exceed vendor payments",
                            List.of(
                                    createPayment(AccountType.BUYER, "150.75"),
                                    createPayment(AccountType.VENDOR, "50.25")
                            ),
                            new BigDecimal("100.50")
                    ),
                    arguments(
                            "when vendor payments exceed buyer payments",
                            List.of(
                                    createPayment(AccountType.BUYER, "100.00"),
                                    createPayment(AccountType.VENDOR, "250.50")
                            ),
                            new BigDecimal("-150.50")
                    ),
                    arguments(
                            "when only buyer payments exist",
                            List.of(
                                    createPayment(AccountType.BUYER, "200.00"),
                                    createPayment(AccountType.BUYER, "50.50")
                            ),
                            new BigDecimal("250.50")
                    ),
                    arguments(
                            "when only vendor payments exist",
                            List.of(
                                    createPayment(AccountType.VENDOR, "75.00"),
                                    createPayment(AccountType.VENDOR, "25.00")
                            ),
                            new BigDecimal("-100.00")
                    ),
                    arguments(
                            "when payments balance to zero",
                            List.of(
                                    createPayment(AccountType.BUYER, "100.00"),
                                    createPayment(AccountType.VENDOR, "50.00"),
                                    createPayment(AccountType.VENDOR, "50.00")
                            ),
                            BigDecimal.ZERO
                    ),
                    arguments(
                            "when no buyer payments exist in the list",
                            List.of(
                                    createPayment(AccountType.VENDOR, "99.99")
                            ),
                            new BigDecimal("-99.99")
                    ),
                    arguments(
                            "when no vendor payments exist in the list",
                            List.of(
                                    createPayment(AccountType.BUYER, "123.45")
                            ),
                            new BigDecimal("123.45")
                    ),
                    arguments(
                            "when payments include zero amounts",
                            List.of(
                                    createPayment(AccountType.BUYER, "100.00"),
                                    createPayment(AccountType.VENDOR, "0.00"),
                                    createPayment(AccountType.BUYER, "0.00")
                            ),
                            new BigDecimal("100.00")
                    ),
                    arguments(
                            "when both payments are zero",
                            List.of(
                                    createPayment(AccountType.VENDOR, "0.00"),
                                    createPayment(AccountType.BUYER, "0.00")
                            ),
                            BigDecimal.ZERO
                    )
            );
        }
    }

    private static TradePayment createPayment(AccountType type, String amount) {
        return TradePayment.builder()
                .type(type)
                .amount(new BigDecimal(amount))
                .build();
    }
}
