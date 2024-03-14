package com.example.stockexchange.repository;

import com.example.stockexchange.entity.Company;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.Statement;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.List;

@Slf4j
//@Repository
@Getter
@RequiredArgsConstructor
@ConditionalOnProperty(value = "local.db.enabled", havingValue = "true")
public class CustomCompanyRepository{

    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    public final static String INSERT_COMPANY_QUERY = """
            insert into company (symbol, company_name, is_enabled)
            values($1, $2, $3) on conflict (company_name) do nothing 
            returning *
            """;

    public Flux<Company> save(List<Company> companies) {
        return r2dbcEntityTemplate.getDatabaseClient().inConnectionMany(connection ->
        {
            Statement statement = connection.createStatement(INSERT_COMPANY_QUERY);
            companies.forEach(company -> statement
                    .add()
                    .bind("$1", company.getSymbol())
                    .bind("$2", company.getName())
                    .bind("$3", company.isEnabled()));

            return Flux.from(statement.execute())
//                    .log("Postgres Level")
                    .flatMap(result -> result.map((row, metadata) -> toCompany(row)))
//                    .doOnNext(company -> log.info("Company added: {}", company))
                    .doOnComplete(() -> log.info("Companies: {} was successfully added/updated into repo", companies))
                    .doOnError(error ->  log.error("Requesting companies: {} caused an error: {}", companies, error.getMessage()));
        });
    }

    private Company toCompany(Row row) {
        return Company.builder()
                .id((Long) row.get("id"))
                .symbol((String) row.get("symbol"))
                .name((String) row.get("company_name"))
                .build();
    }
}
