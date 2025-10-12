package com.vladimirbabin.github.async_spring_practice.domain.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class Payment {
    private Long id;
    private Long tradeId;
    private PaymentType type;
    private BigDecimal amount;
    private String currency;
    private LocalDate paymentDate;
}
