package com.vladimirbabin.github.async_spring_practice.adapters.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeRepository extends JpaRepository<TradeEntity, Long> {
}
