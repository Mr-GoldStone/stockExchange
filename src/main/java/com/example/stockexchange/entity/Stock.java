package com.example.stockexchange.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder()
@Table("stocks")
public class Stock {

    @Id
    private Long id;
    @Column("latest_price")
    private Float latestPrice;
    @Column("change")
    private Float change;
    @Column("change_percent")
    private Float changePercent;
    @Column("company_name")
    private String companyName;
    @Column("symbol")
    private String symbol;
    @Transient
    private Company company;
}

