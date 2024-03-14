package com.example.stockexchange.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("company")
public class Company {

    @Id
    @Column("id")
    private Long id;

    @Column("symbol")
    private String symbol;
    @Column("company_name")
    private String name;
    @Column("is_enabled")
    private boolean isEnabled;
}
