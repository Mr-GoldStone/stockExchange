package com.example.stockexchange.service;

import com.example.stockexchange.api.ExApiClient;
import com.example.stockexchange.entity.Company;
import com.example.stockexchange.entity.Stock;
import com.example.stockexchange.repository.CompanyRepository;
import com.example.stockexchange.repository.StockRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@RequiredArgsConstructor
@Setter
public class DataProcessServiceImpl implements DataProcessService {

    @Value("${service.count_company}")
    private Integer countCompany;
    @Value("${service.buffer_size}")
    private Integer bufferSize;
    private final ExApiClient client;
    private final CompanySymbolHolder companySymbolHolder = new CompanySymbolHolder();
    private final StockRepository stockRepository;
    private final CompanyRepository companyRepository;

    @Override
    public Flux<Company> processingCompanyData() {
        companySymbolHolder.getSymbols().clear();
        return client.getCompanies()
                .filter(Company::getIsEnabled)
                .take(countCompany)
                .doOnNext(companySymbolHolder::add)
                .buffer(bufferSize)
                .flatMap(companyRepository::save)
                .doFirst(() -> log.info("Processing company data in service is running"))
                .doOnComplete(() -> log.info("Processing company data in service was finished "))
                .doOnError(err -> log.error("FIND Error {}", err.getMessage()));
    }

    @Override
    public Flux<Stock> processingStockData() {
        return Flux.fromIterable(companySymbolHolder.getSymbols())
                .flatMap(client::getStockBySymbol)
                .buffer(bufferSize)
                .flatMap(stockRepository::save)
                .doOnComplete(() -> log.info("Processing stock data in service was finished "))
                .doOnError(err -> log.error("FIND Error {}", err.getMessage()));
    }

    @Getter
    public static class CompanySymbolHolder {
        private final List<String> symbols = new CopyOnWriteArrayList<>();
        private final AtomicInteger index = new AtomicInteger();
        private final static Integer ZERO_INDEX = 0;


        public void add(Company company) {
            symbols.add(company.getSymbol());
        }
    }
}
