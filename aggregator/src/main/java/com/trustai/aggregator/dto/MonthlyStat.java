package com.trustai.aggregator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyStat {
    private String month;              // e.g. "2025-01"
    private BigDecimal totalDeposit;
    private BigDecimal totalInvestment;
    private BigDecimal totalWithdraw;
    private BigDecimal totalProfit;
}
