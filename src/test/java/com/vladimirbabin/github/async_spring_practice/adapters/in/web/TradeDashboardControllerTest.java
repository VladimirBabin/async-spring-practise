package com.vladimirbabin.github.async_spring_practice.adapters.in.web;

import com.vladimirbabin.github.async_spring_practice.application.ports.in.TradeCalculationUseCase;
import com.vladimirbabin.github.async_spring_practice.domain.dto.TradeCalculationDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TradeDashboardController.class)
class TradeDashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TradeCalculationUseCase tradeCalculationUseCase;

    @Test
    void getTradeCalculations() throws Exception {
        // Given
        TradeCalculationDto dto1 = TradeCalculationDto.builder().tradeId(1L).grossProfit(BigDecimal.valueOf(1000)).costOfGoods(BigDecimal.valueOf(1500)).balance(BigDecimal.valueOf(500)).build();
        TradeCalculationDto dto2 = TradeCalculationDto.builder().tradeId(2L).grossProfit(BigDecimal.valueOf(1000)).costOfGoods(BigDecimal.valueOf(1250)).balance(BigDecimal.valueOf(-250)).build();
        List<TradeCalculationDto> dtos = List.of(dto1, dto2);

        when(tradeCalculationUseCase.getTradeCalculations()).thenReturn(CompletableFuture.completedFuture(dtos));

        // When
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/dashboards/trade-calculations"))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [
                            {"tradeId":1,"grossProfit":1000,"costOfGoods":1500,"balance":500},
                            {"tradeId":2,"grossProfit":1000,"costOfGoods":1250,"balance":-250}
                        ]
                        """));
    }
}
