package com.trustai.investment_service.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class EligibleInvestmentSummary {
    private String rankCode;
    private String rankDisplayName;
    private String incomePercentageRange; // From InvestmentSchema.returnRate range
    private BigDecimal minInvestmentAmount; // From RankConfig
    private BigDecimal maxInvestmentAmount; // From RankConfig
    private boolean enabled;
    private List<SchemaSummary> schemas;
}
