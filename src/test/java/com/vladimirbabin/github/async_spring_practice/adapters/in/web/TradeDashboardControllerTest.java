package com.vladimirbabin.github.async_spring_practice.adapters.in.web;

import com.vladimirbabin.github.async_spring_practice.application.ports.in.TradeCalculationUseCase;
import com.vladimirbabin.github.async_spring_practice.domain.dto.TradeCalculationDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(TradeDashboardController.class)
@AutoConfigureMockMvc
@DisplayName("TradeDashboardController")
class TradeDashboardControllerTest {

    private static final Long TRADE_1_ID = 1L;
    private static final BigDecimal TRADE_1_GP = new BigDecimal("1000.50");
    private static final BigDecimal TRADE_1_COG = new BigDecimal("1500.25");
    private static final BigDecimal TRADE_1_BALANCE = new BigDecimal("500.00");
    private static final Long TRADE_2_ID = 2L;
    private static final BigDecimal TRADE_2_GP = new BigDecimal("2000.00");
    private static final BigDecimal TRADE_2_COG = new BigDecimal("1250.75");
    private static final BigDecimal TRADE_2_BALANCE = new BigDecimal("-250.50");

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TradeCalculationUseCase tradeCalculationUseCase;

    private static final String TRADE_CALCULATIONS_URL = "/api/v1/dashboards/trade-calculations";

    @Nested
    @DisplayName("GET /api/v1/dashboards/trade-calculations")
    class GetTradeCalculations {

        @Test
        @DisplayName("should return 200 OK with a list of trade calculations when data is available")
        void shouldReturnOkWithCalculations_WhenDataExists() throws Exception {
            // given
            List<TradeCalculationDto> dtos = createTradeCalculationDtos();
            given(tradeCalculationUseCase.getTradeCalculations())
                    .willReturn(CompletableFuture.completedFuture(dtos));

            // when
            MvcResult mvcResult = mockMvc.perform(get(TRADE_CALCULATIONS_URL))
                    .andReturn();

            // then
            mockMvc.perform(asyncDispatch(mvcResult))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].tradeId", is(TRADE_1_ID.intValue())))
                    .andExpect(jsonPath("$[0].grossProfit", is(TRADE_1_GP.doubleValue())))
                    .andExpect(jsonPath("$[0].costOfGoods", is(TRADE_1_COG.doubleValue())))
                    .andExpect(jsonPath("$[0].balance", is(TRADE_1_BALANCE.doubleValue())))
                    .andExpect(jsonPath("$[1].tradeId", is(TRADE_2_ID.intValue())))
                    .andExpect(jsonPath("$[1].grossProfit", is(TRADE_2_GP.doubleValue())))
                    .andExpect(jsonPath("$[1].costOfGoods", is(TRADE_2_COG.doubleValue())))
                    .andExpect(jsonPath("$[1].balance", is(TRADE_2_BALANCE.doubleValue())));
        }

        @Test
        @DisplayName("should return 200 OK with an empty list when no trade data is available")
        void shouldReturnOkWithEmptyList_WhenNoDataExists() throws Exception {
            // given
            given(tradeCalculationUseCase.getTradeCalculations())
                    .willReturn(CompletableFuture.completedFuture(Collections.emptyList()));

            // when
            MvcResult mvcResult = mockMvc.perform(get(TRADE_CALCULATIONS_URL))
                    .andReturn();

            // then
            mockMvc.perform(asyncDispatch(mvcResult))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        @DisplayName("should return 500 Internal Server Error when the use case future completes exceptionally")
        void shouldReturnInternalServerError_WhenUseCaseFails() throws Exception {
            // given
            String errorMessage = "Internal service error";
            given(tradeCalculationUseCase.getTradeCalculations())
                    .willReturn(CompletableFuture.failedFuture(new RuntimeException(errorMessage)));

            // when
            MvcResult mvcResult = mockMvc.perform(get(TRADE_CALCULATIONS_URL))
                    .andReturn();

            // then
            mockMvc.perform(asyncDispatch(mvcResult))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.status", is(HttpStatus.INTERNAL_SERVER_ERROR.value())))
                    .andExpect(jsonPath("$.error", is(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())))
                    .andExpect(jsonPath("$.message", is(
                            "An unexpected internal server error occurred. Please contact the support team."))
                    )
                    .andExpect(jsonPath("$.path", is(TRADE_CALCULATIONS_URL)));
        }
    }

    private List<TradeCalculationDto> createTradeCalculationDtos() {
        TradeCalculationDto dto1 = TradeCalculationDto.builder()
                .tradeId(TRADE_1_ID)
                .grossProfit(TRADE_1_GP)
                .costOfGoods(TRADE_1_COG)
                .balance(TRADE_1_BALANCE)
                .build();
        TradeCalculationDto dto2 = TradeCalculationDto.builder()
                .tradeId(TRADE_2_ID)
                .grossProfit(TRADE_2_GP)
                .costOfGoods(TRADE_2_COG)
                .balance(TRADE_2_BALANCE)
                .build();
        return List.of(dto1, dto2);
    }
}
