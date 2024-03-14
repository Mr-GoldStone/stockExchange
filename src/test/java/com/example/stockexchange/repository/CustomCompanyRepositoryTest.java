package com.example.stockexchange.repository;

import com.example.stockexchange.TestConfig;
import com.example.stockexchange.entity.Company;
import io.r2dbc.spi.Statement;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;


@DataR2dbcTest
@Testcontainers
@Import(TestConfig.class)
@ActiveProfiles("test")
class CustomCompanyRepositoryTest {
    @Container
    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer<>("postgres:12")
        .withCopyFileToContainer(MountableFile.forClasspathResource("schema-test.sql"), "/docker-entrypoint-initdb.d/init.sql");

    @DynamicPropertySource
    static void registerDynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () -> "r2dbc:postgresql://"
            + postgreSQLContainer.getHost() + ":" + postgreSQLContainer.getFirstMappedPort()
            + "/" + postgreSQLContainer.getDatabaseName());
        registry.add("spring.r2dbc.username", () -> postgreSQLContainer.getUsername());
        registry.add("spring.r2dbc.password", () -> postgreSQLContainer.getPassword());
    }

    @Autowired
    R2dbcEntityTemplate template;
    @Autowired
    CustomCompanyRepository companyRepository;

    public final static String SELECT_ALL = "select * from company";

    @Test
    public void testDatabaseClientExisted() {
        assertNotNull(template);
    }

    @Test
    public void testPostRepositoryExisted() {
        assertNotNull(companyRepository);
    }

    @Test
    void save() {
        Company company1 = Company.builder()
            .isEnabled(true)
            .name("A-company")
            .symbol("A")
            .build();

        companyRepository.save(List.of(company1))
            .thenMany(selectAll())
            .as(StepVerifier::create)
            .expectNextCount(1)
            .verifyComplete();
    }

    private Flux<Company> selectAll() {
        return template.getDatabaseClient().inConnectionMany(connection ->
        {
            Statement statement = connection.createStatement(SELECT_ALL);
            return Flux.from(statement.execute())
                .flatMap(result -> result.map((row, metadata) -> Company.builder().build()));
        });
    }
}