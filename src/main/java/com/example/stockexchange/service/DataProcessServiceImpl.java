package com.example.stockexchange.service;

import com.example.stockexchange.api.ExApiClient;
import com.example.stockexchange.entity.Company;
import com.example.stockexchange.entity.Stock;
import com.example.stockexchange.repository.CustomCompanyRepository;
import com.example.stockexchange.repository.CustomStockRepository;
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
    private final CustomStockRepository customStockRepository;
    private final CustomCompanyRepository customCompanyRepository;

    @Override
    public Flux<Company> processingCompanyData() {
        companySymbolHolder.getSymbols().clear();
        return client.getCompanies()
                .filter(Company::isEnabled)
                .take(countCompany)
                .doOnNext(companySymbolHolder::add)
                .log()
                .buffer(bufferSize)
                .flatMap(customCompanyRepository::save)
                .doFirst(() -> log.info("Processing company data in service is running"))
                .doOnComplete(() -> log.info("Processing company data in service was finished "))
                .doOnError(err -> log.error("FIND Error {}", err.getMessage()));
//                .onErrorContinue((error, obj) -> log.error("error:[{}]", error.getMessage()));
    }

    @Override
    public Flux<Stock> processingStockData() {
        return Flux.fromIterable(companySymbolHolder.getSymbols())
                .flatMap(client::getStockBySymbol)
                .buffer(bufferSize)
                .flatMap(customStockRepository::save)
                .doOnComplete(() -> log.info("Processing stock data in service was finished "))
                .doOnError(err -> log.error("FIND Error {}", err.getMessage()));
//                .onErrorContinue((error, obj) -> log.error("error:[{}]", error.getMessage()));
    }

    @Getter
    public static class CompanySymbolHolder {
        private final List<String> symbols = new CopyOnWriteArrayList<>();
        private final AtomicInteger index = new AtomicInteger();
        private final static Integer ZERO_INDEX = 0;


        public void add(Company company) {
            symbols.add(company.getSymbol());
//            log.info("Add company: {} symbol into list", company.getName());
        }

        public String get() {
            String symbol = symbols.get(index.getAndIncrement());
            if (symbols.size() == index.get()) {
                index.set(ZERO_INDEX);
            }
            return symbol;
        }
    }
}
