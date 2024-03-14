package com.example.stockexchange.job;

import com.example.stockexchange.service.AnalyticService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnalyticDataJob {

    private final AnalyticService analyticService;

    @Scheduled(initialDelay = 5000, fixedDelay = 3*1000)
    public void top5MaxStockPriceJob(){
        CompletableFuture.supplyAsync(analyticService::getTop5MaxStockPrice, Executors.newFixedThreadPool(10))
            .thenApply(stockFlux -> stockFlux.subscribe(stock -> log.info("JOB: Stock by company {} with price {}", stock.getCompanyName(), stock.getLatestPrice())));

//        analyticService.getTop5MaxStockPrice()
//            .doFirst(() -> log.info("JOB: Processing top 5 max stock price job is running"))
//            .doOnComplete(() -> log.info("JOB: Processing top 5 max stock price job is finished"))
//            .subscribe(stock -> log.info("JOB: Stock by company {} with price {}", stock.getCompanyName(), stock.getLatestPrice()));


    }



    @Scheduled(initialDelay = 5000, fixedDelay = 7 * 1000)
    public void top5MaxStockChangePercentJob() {
        CompletableFuture.supplyAsync(analyticService::get5MaxStockChangePercent)
                .thenApply(stockFlux -> stockFlux.subscribe(stock -> log.info("JOB: Stock by company {} with change in percent {}", stock.getCompanyName(), stock.getChangePercent())));
    }
        //        analyticService.get5MaxStockChangePercent()
//            .doFirst(() -> log.info("JOB: Processing top 5 max stock change in percent job is running"))
//            .doOnComplete(() -> log.info("JOB: Processing top 5 max stock change in percent job is finished"))
//            .subscribe(stock -> log.info("JOB: Stock by company {} with change in percent {}", stock.getCompanyName(), stock.getChangePercent()));    }
}
