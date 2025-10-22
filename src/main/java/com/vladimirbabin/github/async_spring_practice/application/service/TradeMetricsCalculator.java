package com.vladimirbabin.github.async_spring_practice.application.service;

import com.vladimirbabin.github.async_spring_practice.domain.model.Trade;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TradeMetricsCalculator {

    public BigDecimal calculateGrossProfit(Trade trade) {
        return trade.getProductBuyingPrice()
                .multiply(
                        BigDecimal
                                .valueOf(
                                        trade.getProductQuantity()));
    }

    public BigDecimal calculateCostOfGoods(Trade trade) {
        return trade.getProductSellingPrice()
                .multiply(
                        BigDecimal
                                .valueOf(
                                        trade.getProductQuantity()));
    }

}
