package com.vladimirbabin.github.async_spring_practice.adapters.out.persistence;

import com.vladimirbabin.github.async_spring_practice.adapters.out.persistence.mapper.TradeMapper;
import com.vladimirbabin.github.async_spring_practice.domain.model.Trade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoInteractions;


@ExtendWith(MockitoExtension.class)
@DisplayName("TradePersistenceAdapter")
class TradePersistenceAdapterTest {

    private static final Long TRADE_ID_1 = 1L;
    private static final Long TRADE_ID_2 = 2L;
    private static final String PRODUCT_NAME_1 = "Crude Oil";
    private static final String PRODUCT_NAME_2 = "Natural Gas";

    @Mock
    private TradeRepository tradeRepository;

    @Mock
    private TradeMapper tradeMapper;

    private TradePersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new TradePersistenceAdapter(tradeRepository, tradeMapper);
    }

    @Nested
    @DisplayName("getAllTrades")
    class GetAllTrades {

        @Test
        @DisplayName("should return a future with a list of domain trades when repository finds entities")
        void shouldReturnFutureWithDomainTrades_WhenRepositoryFindsEntities() {
            // given
            TradeEntity entity1 = createTradeEntity(TRADE_ID_1, PRODUCT_NAME_1);
            TradeEntity entity2 = createTradeEntity(TRADE_ID_2, PRODUCT_NAME_2);
            List<TradeEntity> entities = List.of(entity1, entity2);

            Trade domain1 = createDomainTrade(TRADE_ID_1, PRODUCT_NAME_1);
            Trade domain2 = createDomainTrade(TRADE_ID_2, PRODUCT_NAME_2);

            given(tradeRepository.findAll()).willReturn(entities);
            given(tradeMapper.toDomain(entity1)).willReturn(domain1);
            given(tradeMapper.toDomain(entity2)).willReturn(domain2);

            // when
            CompletableFuture<List<Trade>> resultFuture = adapter.getAllTrades();

            // then
            then(resultFuture)
                    .succeedsWithin(Duration.ofSeconds(1))
                    .satisfies(tradeList -> {
                        then(tradeList).hasSize(2);
                        then(tradeList).containsExactly(domain1, domain2);
                    });
        }

        @Test
        @DisplayName("should return a future with an empty list when repository finds no entities")
        void shouldReturnFutureWithEmptyList_WhenRepositoryFindsNoEntities() {
            // given
            given(tradeRepository.findAll()).willReturn(Collections.emptyList());

            // when
            CompletableFuture<List<Trade>> resultFuture = adapter.getAllTrades();

            // then
            then(resultFuture)
                    .succeedsWithin(Duration.ofSeconds(1))
                    .isEqualTo(Collections.emptyList());

            verifyNoInteractions(tradeMapper);
        }
    }

    private TradeEntity createTradeEntity(Long id, String productName) {
        TradeEntity tradeEntity = new TradeEntity();
        tradeEntity.setId(id);
        tradeEntity.setProductName(productName);
        tradeEntity.setBuyingAccountId(100L);
        tradeEntity.setSellingAccountId(200L);
        return tradeEntity;
    }

    private Trade createDomainTrade(Long id, String productName) {
        return Trade.builder()
                .id(id)
                .productName(productName)
                .buyingAccountId(100L)
                .sellingAccountId(200L)
                .productQuantity(50)
                .productBuyingPrice(new BigDecimal("75.50"))
                .productSellingPrice(new BigDecimal("80.00"))
                .build();
    }
}
