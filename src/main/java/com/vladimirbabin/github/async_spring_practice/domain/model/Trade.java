package com.vladimirbabin.github.async_spring_practice.domain.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class Trade {

    private Long id;

    @NotNull
    private Long buyingAccountId;

    @NotNull
    private Long sellingAccountId;

    @NotNull
    @NotEmpty
    private String productName;

    private int productQuantity;

    @NotNull
    private BigDecimal productBuyingPrice;

    @NotNull
    private BigDecimal productSellingPrice;

    private Integer createdBy;
    private Integer modifiedBy;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
}
