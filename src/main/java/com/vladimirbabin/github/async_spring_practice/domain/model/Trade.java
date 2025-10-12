package com.vladimirbabin.github.async_spring_practice.domain.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class Trade {
    private Long id;
    private Long buyingAccountId;
    private Long vendorAccountId;
    private String productName;
    private int productQuantity;
    private BigDecimal productBuyingPrice;
    private BigDecimal productSellingPrice;
}
