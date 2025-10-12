package com.vladimirbabin.github.async_spring_practice.adapters.out.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "trades")
@Getter
@Setter
public class TradeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long buyingAccountId;
    private Long vendorAccountId;
    private String productName;
    private int productQuantity;
    private BigDecimal productBuyingPrice;
    private BigDecimal productSellingPrice;

}
