package com.example.stockexchange.service;

import com.example.stockexchange.entity.Stock;
import com.example.stockexchange.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnalyticServiceImpl implements AnalyticService {

  private final StockRepository stockRepository;

    @Override
    public Flux<Stock> getTop5MaxStockPrice() {
     return    stockRepository.getTop5MaxStockPrice()
                .doFirst(()-> log.info("SERVICE: Top 5 Max Stock Price /n"))
                .doOnComplete(() -> log.info("SERVICE: getTop5MaxStockPrice is done"));
    }

    @Override
    public Flux<Stock> get5MaxStockChangePercent() {
       return stockRepository.get5MaxStockChangePercent()
                .doFirst(()-> log.info("SERVICE: Top 5 Max Stock Change in Percent /n"))
                .doOnComplete(() -> log.info("SERVICE: get5MaxStockChangePercent is done"));
    }
}
