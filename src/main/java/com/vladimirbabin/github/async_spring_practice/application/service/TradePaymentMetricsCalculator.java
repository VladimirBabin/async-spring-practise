package com.vladimirbabin.github.async_spring_practice.application.service;

import com.vladimirbabin.github.async_spring_practice.domain.model.TradePayment;
import com.vladimirbabin.github.async_spring_practice.domain.model.AccountType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class TradePaymentMetricsCalculator {

    public BigDecimal calculateTradePaymentsBalance(List<TradePayment> tradePayments) {
        if (tradePayments == null || tradePayments.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal buyerPayments = getPaymentsSumByAccountType(tradePayments, AccountType.BUYER);
        BigDecimal vendorPayments = getPaymentsSumByAccountType(tradePayments, AccountType.VENDOR);
        return buyerPayments.subtract(vendorPayments);
    }

    private BigDecimal getPaymentsSumByAccountType(List<TradePayment> tradeTradePayments, AccountType accountType) {
        return tradeTradePayments.stream()
                .filter(p -> accountType == p.getType())
                .map(TradePayment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
