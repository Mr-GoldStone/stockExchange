package com.example.stockexchange.config;

import com.example.stockexchange.repository.CustomCompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@Configuration
@EnableR2dbcRepositories
public class AppConfig {

    @Bean
    CustomCompanyRepository customCompanyRepository(@Autowired R2dbcEntityTemplate r2dbcEntityTemplate) {
        return new CustomCompanyRepository(r2dbcEntityTemplate);
    }
}
