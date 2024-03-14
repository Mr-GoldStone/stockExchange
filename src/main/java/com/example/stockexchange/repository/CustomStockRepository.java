package com.example.stockexchange.repository;

import com.example.stockexchange.entity.Stock;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.Statement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CustomStockRepository {

    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    private final static String UPSERT_STOCK_QUERY = """
            insert into stock (latestprice, change, changepercent, company_name, symbol)
            values ($1, $2, $3, $4, $5) on conflict (company_name) do update
            set latestprice = excluded.latestprice,
                change = excluded.change,
                changepercent = excluded.changepercent
            """;
    private final static String SELECT_TOP_5_MAX_PRICE = """
            select * from stock order by  latestprice desc, company_name limit 5;
            """;
    private final static String SELECT_5_ABSOLUTE_MAX_CHANGE_PERCENT = """
            select * from stock order by abs(changepercent) desc limit 5;
            """;


    public Flux<Stock> save(List<Stock> stocks) {
        return r2dbcEntityTemplate.getDatabaseClient().inConnectionMany(connection -> {
            Statement statement = connection.createStatement(UPSERT_STOCK_QUERY);

            stocks.forEach(stock -> statement
                    .add()
                    .bind("$1", stock.getLatestPrice())
                    .bind("$2", stock.getChange())
                    .bind("$3", stock.getChangePercent())
                    .bind("$4", stock.getCompanyName())
                    .bind("$5", stock.getSymbol()));

            return Flux.from(statement.execute())
                    .flatMap(result -> result.map((row, metadata) -> this.toStock(row)))
                    .doOnComplete(() -> log.info("REPO: Stocks: {} was successfully added/updated into repo", stocks))
                    .doOnError(error ->  log.error("Requesting stocks: {} caused an error: {}", stocks, error.getMessage()));
        });
    }

    public Flux<Stock> getTop5MaxStockPrice() {
       return r2dbcEntityTemplate.getDatabaseClient().inConnectionMany(connection ->
        {
            Statement statement = connection.createStatement(SELECT_TOP_5_MAX_PRICE);
            return Flux.from(statement.execute())
                    .flatMap(result -> result.map((row, metadata) -> toStock(row)))
                    .doOnComplete(() -> log.debug("REPO: 5 max stock prices were got"))
                    .doOnError(error -> log.error("Requesting to repo caused an error {}", error.toString()));
        });
    }

    public Flux<Stock> get5MaxStockChangePercent() {
        return r2dbcEntityTemplate.getDatabaseClient().inConnectionMany(connection ->
        {
            Statement statement = connection.createStatement(SELECT_5_ABSOLUTE_MAX_CHANGE_PERCENT);
            return Flux.from(statement.execute())
                    .flatMap(result -> result.map((row, metadata) -> toStock(row)))
                    .doOnComplete(() -> log.debug("REPO: 5 max stock change in percent were got"))
                    .doOnError(error -> log.error("Requesting to repo caused an error {}", error.toString()));

        });
    }

    private Stock toStock(Row row) {
        return Stock.builder()
                .id((Long) row.get("id"))
                .companyName((String) row.get("company_name"))
                .changePercent((Float) row.get("changePercent"))
                .latestPrice((Float) row.get("latestPrice"))
                .build();
    }


}
