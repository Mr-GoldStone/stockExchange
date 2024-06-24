package com.example.stockexchange.service;

import com.example.stockexchange.api.ExApiClient;
import com.example.stockexchange.entity.Company;
import com.example.stockexchange.entity.Stock;
import com.example.stockexchange.repository.CompanyRepository;
import com.example.stockexchange.repository.StockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class DataProcessServiceImplTest {

    @Value("${service.count_company}")
    private Integer countCompany;
    @Value("${service.buffer_size}")
    private Integer bufferSize;
    @Mock
    private ExApiClient mockClient;
    @Mock
    private StockRepository stockRepository;
    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private DataProcessServiceImpl.CompanySymbolHolder holder = new DataProcessServiceImpl.CompanySymbolHolder();

    @InjectMocks
    private DataProcessServiceImpl dataProcessService;

    @BeforeEach
    void setUp() {
        dataProcessService.setCountCompany(countCompany);
        dataProcessService.setBufferSize(bufferSize);
    }

    @Test
    void processingCompanyData_saveCompanies_ok() {
//        given
        Company company1 = Company.builder()
            .symbol("A")
            .name("A-Company")
            .isEnabled(true)
            .build();
        Company company3 = Company.builder()
            .symbol("C")
            .name("C-Company")
            .isEnabled(true)
            .build();
        Flux<Company> companies = generateCompanies();

//        when
        when(mockClient.getCompanies()).thenReturn(companies);
        when(companyRepository.save(anyList())).thenReturn(Flux.just(company1, company3));

//        then
        StepVerifier.create(dataProcessService.processingCompanyData())
            .expectNext(company1, company3)
            .verifyComplete();
        verify(mockClient, times(1)).getCompanies();
        verify(companyRepository, times(1)).save(any());
    }

    @Test
    void processingCompanyData_saveCompanies_fail() {
//        given
        Company company1 = Company.builder()
            .symbol("A")
            .name("A-Company")
            .isEnabled(true)
            .build();

//        when
        when(mockClient.getCompanies()).thenReturn(Flux.just(company1));
        when(companyRepository.save(anyList())).thenThrow(UnsupportedOperationException.class);

//        then
        StepVerifier.create(dataProcessService.processingCompanyData().log())
            .verifyError();
        verify(mockClient, times(1)).getCompanies();
        verify(companyRepository, times(1)).save(any());
    }

    @Test
    void processStockData_saveStocks_ok(){
//        given
        Stock stock1 = Stock.builder()
            .latestPrice(1.0f)
            .changePercent(50.0f)
            .symbol("A")
            .change(1.5f)
            .build();
        Stock stock2 = Stock.builder()
            .latestPrice(1.0f)
            .changePercent(-50.0f)
            .symbol("B")
            .change(0.5f)
            .build();

        ReflectionTestUtils.setField(dataProcessService, "companySymbolHolder", holder);

//        when
        when(holder.getSymbols()).thenReturn(List.of("A", "B"));
        when(mockClient.getStockBySymbol("A")).thenReturn(Mono.just(stock1));
        when(mockClient.getStockBySymbol("B")).thenReturn(Mono.just(stock2));
        when(stockRepository.save(List.of(stock1, stock2))).thenReturn(Flux.just(stock1, stock2));

//        then
        StepVerifier.create(dataProcessService.processingStockData().log())
            .expectNextCount(2)
            .verifyComplete();
        verify(mockClient, times(2)).getStockBySymbol(anyString());
        verify(stockRepository, times(1)).save(any());
    }

    @Test
    void processStockData_saveStocks_fail() {
//        given
        Stock stock1 = Stock.builder()
            .latestPrice(1.0f)
            .changePercent(50.0f)
            .symbol("A")
            .change(1.5f)
            .build();
        Stock stock2 = Stock.builder()
            .latestPrice(1.0f)
            .changePercent(-50.0f)
            .symbol("B")
            .change(0.5f)
            .build();

        ReflectionTestUtils.setField(dataProcessService, "companySymbolHolder", holder);

//        when
        when(holder.getSymbols()).thenReturn(List.of("A", "B"));
        when(mockClient.getStockBySymbol("A")).thenReturn(Mono.just(stock1));
        when(mockClient.getStockBySymbol("B")).thenReturn(Mono.just(stock2));
        when(stockRepository.save(List.of(stock1, stock2))).thenThrow(UnsupportedOperationException.class);

//        then
        StepVerifier.create(dataProcessService.processingStockData().log())
            .verifyError();
        verify(mockClient, times(2)).getStockBySymbol(anyString());
        verify(stockRepository, times(1)).save(any());
    }


    private Flux<Company> generateCompanies() {
        Company company1 = Company.builder()
            .symbol("A")
            .name("A-Company")
            .isEnabled(true)
            .build();
        Company company2 = Company.builder()
            .symbol("B")
            .name("B-Company")
            .isEnabled(false)
            .build();
        Company company3 = Company.builder()
            .symbol("C")
            .name("C-Company")
            .isEnabled(true)
            .build();
        return Flux.just(company2, company1, company3);
    }
}