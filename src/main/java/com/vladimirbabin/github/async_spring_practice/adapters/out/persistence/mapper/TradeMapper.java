package com.vladimirbabin.github.async_spring_practice.adapters.out.persistence.mapper;


import com.vladimirbabin.github.async_spring_practice.adapters.out.persistence.TradeEntity;
import com.vladimirbabin.github.async_spring_practice.domain.model.Trade;
import org.mapstruct.Mapper;

@Mapper
public interface TradeMapper {

    Trade toDomain(TradeEntity entity);
}
