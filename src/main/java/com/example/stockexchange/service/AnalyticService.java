package com.example.stockexchange.service;

import com.example.stockexchange.entity.Stock;
import reactor.core.publisher.Flux;

public interface AnalyticService {
    Flux<Stock> getTop5MaxStockPrice();

    Flux<Stock> get5MaxStockChangePercent();
}
