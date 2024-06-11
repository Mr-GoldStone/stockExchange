package com.example.stockexchange.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("companies")
public class Company {

    @Id
    @Column("id")
    private Long id;

    @Column("symbol")
    private String symbol;
    @Column("company_name")
    private String name;
    @Column("is_enabled")
    @Getter
    private Boolean isEnabled;
}
