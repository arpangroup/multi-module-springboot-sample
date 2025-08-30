package com.trustai.investment_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class SchemaSummary {
    private Long schemaId;
    private String name;
    private BigDecimal returnRate;

    public SchemaSummary(Long schemaId, String name, BigDecimal returnRate) {
        this.schemaId = schemaId;
        this.name = name;
        this.returnRate = returnRate;
    }
}
