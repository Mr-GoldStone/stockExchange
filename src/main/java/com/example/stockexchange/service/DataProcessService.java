package com.example.stockexchange.service;

import com.example.stockexchange.entity.Company;
import com.example.stockexchange.entity.Stock;
import reactor.core.publisher.Flux;

public interface DataProcessService {
    Flux<Company> processingCompanyData();

    Flux<Stock> processingStockData();
}
