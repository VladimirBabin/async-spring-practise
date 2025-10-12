package com.vladimirbabin.github.async_spring_practice.domain.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TradeCalculationDto {
    private Long tradeId;
    private BigDecimal grossProfit;
    private BigDecimal costOfGoods;
    private BigDecimal balance;
}
