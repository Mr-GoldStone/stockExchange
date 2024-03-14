package com.example.stockexchange.job;

import com.example.stockexchange.service.DataProcessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProcessDataJob {

    private final DataProcessService dataProcessService;
    private final ExecutorService executor = Executors.newCachedThreadPool();

    @Scheduled(initialDelay = 2000, fixedDelay = 3600*1000)
    public void processingCompanyDataJob(){
//        CompletableFuture.supplyAsync(dataProcessService::processingCompanyData, executor)
//                        .thenApply(Flux::subscribe);
        dataProcessService.processingCompanyData()
                .doFirst(()->log.info("Processing company data job is running"))
                .doOnComplete(() -> log.info("Processing company data job is finished"))
                .subscribe();
    }

    @Scheduled(initialDelay = 5000, fixedDelay = 10*1000)
    public void processingStockDataJob(){
//        log.info("Processing stock data job is running");
//        CompletableFuture.supplyAsync(dataProcessService::processStockData, executor);
//        log.info("Processing stock data job is finished");

        dataProcessService.processingStockData()
                .doFirst(()->log.info("Processing stock data job is running"))
                .doOnComplete(() -> log.info("Processing stock data job is finished"))
                .subscribe();
    }
}
