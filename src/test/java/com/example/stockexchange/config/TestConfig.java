package com.example.stockexchange.config;

import com.example.stockexchange.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;

@TestConfiguration
public class TestConfig {

    @Bean
    CompanyRepository customCompanyRepository(@Autowired R2dbcEntityTemplate r2dbcEntityTemplate) {
        return new CompanyRepository(r2dbcEntityTemplate);
    }
}