package com.vladimirbabin.github.async_spring_practice.application.service;

import com.vladimirbabin.github.async_spring_practice.domain.model.Trade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("TradeMetricsCalculator")
class TradeMetricsCalculatorTest {

    private TradeMetricsCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new TradeMetricsCalculator();
    }

    @Nested
    @DisplayName("calculateGrossProfit")
    class CalculateGrossProfit {

        @ParameterizedTest(name = "{0}")
        @MethodSource("grossProfitTestCases")
        @DisplayName("should calculate gross profit correctly for various inputs")
        void shouldCalculateCorrectGrossProfit_WhenGivenTradeData(
                String description, BigDecimal buyingPrice, int quantity, BigDecimal expectedProfit) {
            // given
            Trade trade = createTrade(buyingPrice, BigDecimal.ZERO, quantity);

            // when
            BigDecimal actualProfit = calculator.calculateGrossProfit(trade);

            // then
            then(actualProfit).isEqualByComparingTo(expectedProfit);
        }

        static Stream<Arguments> grossProfitTestCases() {
            return Stream.of(
                    arguments("positive price and quantity", new BigDecimal("10.50"), 10, new BigDecimal("105.00")),
                    arguments("integer price and quantity", new BigDecimal("20"), 5, new BigDecimal("100")),
                    arguments("zero quantity", new BigDecimal("99.99"), 0, BigDecimal.ZERO),
                    arguments("zero price", BigDecimal.ZERO, 100, BigDecimal.ZERO),
                    arguments("quantity of one", new BigDecimal("123.45"), 1, new BigDecimal("123.45"))
            );
        }
    }

    @Nested
    @DisplayName("calculateCostOfGoods")
    class CalculateCostOfGoods {

        @ParameterizedTest(name = "{0}")
        @MethodSource("costOfGoodsTestCases")
        @DisplayName("should calculate cost of goods correctly for various inputs")
        void shouldCalculateCorrectCostOfGoods_WhenGivenTradeData(
                String description, BigDecimal sellingPrice, int quantity, BigDecimal expectedCost) {
            // given
            Trade trade = createTrade(BigDecimal.ZERO, sellingPrice, quantity);

            // when
            BigDecimal actualCost = calculator.calculateCostOfGoods(trade);

            // then
            then(actualCost).isEqualByComparingTo(expectedCost);
        }

        static Stream<Arguments> costOfGoodsTestCases() {
            return Stream.of(
                    arguments(
                            "positive price and quantity",
                            new BigDecimal("25.25"),
                            10,
                            new BigDecimal("252.50")
                    ),
                    arguments(
                            "integer price and quantity",
                            new BigDecimal("50"),
                            4,
                            new BigDecimal("200")
                    ),
                    arguments(
                            "zero quantity",
                            new BigDecimal("150.00"),
                            0,
                            BigDecimal.ZERO
                    ),
                    arguments(
                            "zero price",
                            BigDecimal.ZERO,
                            200,
                            BigDecimal.ZERO
                    ),
                    arguments(
                            "quantity of one",
                            new BigDecimal("987.65"),
                            1,
                            new BigDecimal("987.65")
                    )
            );
        }
    }

    private Trade createTrade(BigDecimal buyingPrice, BigDecimal sellingPrice, int quantity) {
        return Trade.builder()
                .productBuyingPrice(buyingPrice)
                .productSellingPrice(sellingPrice)
                .productQuantity(quantity)
                .build();
    }
}
