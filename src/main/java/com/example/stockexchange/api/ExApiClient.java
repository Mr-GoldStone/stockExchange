package com.example.stockexchange.api;

import com.example.stockexchange.entity.Company;
import com.example.stockexchange.entity.Stock;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
@Data
public class ExApiClient {

    private final WebClient webClient;

    @Value("${api.external.ref-data-url}")
    private String refDataUrl;
    @Value("${api.external.stock-data-url}")
    private String stockDataUrl;
    @Value("${api.external.token}")
    private String token;

    public Flux<Company> getCompanies() {
        return webClient.get()
            .uri(refDataUrl, token)
            .retrieve()
            .bodyToFlux(Company.class);
    }

    public Mono<Stock> getStockBySymbol(String symbol) {
        return webClient.get()
            .uri(stockDataUrl, symbol, token)
            .retrieve()
            .bodyToMono(Stock.class)
            .onErrorResume(WebClientResponseException.class,
                ex -> ex.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS ? this.getStockBySymbol(symbol) : Mono.error(ex));
    }
}
