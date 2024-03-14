package com.example.stockexchange.service;

import com.example.stockexchange.entity.Stock;
import com.example.stockexchange.repository.CustomStockRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.*;

@SpringBootTest
class AnalyticServiceImplTest {

    @Mock
    private CustomStockRepository stockRepository;

    @InjectMocks
    private AnalyticServiceImpl analyticService;

    @Test
    void getTop5MaxStockPrice() {
        when(stockRepository.getTop5MaxStockPrice()).thenReturn(Flux.fromIterable(generate5Stock()));

        StepVerifier.create(analyticService.getTop5MaxStockPrice().log())
            .expectNextCount(5)
            .verifyComplete();

        verify(stockRepository, times(1)).getTop5MaxStockPrice();
    }

    @Test
    void get5MaxStockChangePercent() {
        when(stockRepository.get5MaxStockChangePercent()).thenReturn(Flux.fromIterable(generate5Stock()));

        StepVerifier.create(analyticService.get5MaxStockChangePercent().log())
            .expectNextCount(5)
            .verifyComplete();

        verify(stockRepository, times(1)).get5MaxStockChangePercent();

    }

    private List<Stock> generate5Stock() {
        return List.of(new Stock(), new Stock(), new Stock(), new Stock(), new Stock());
    }

}