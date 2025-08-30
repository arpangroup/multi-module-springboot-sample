package com.trustai.income_service.income.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
public class TeamIncomeConfigDto {
    private String rank;
    private int minWalletBalance;
    private int maxWalletBalance;
    private BigDecimal commissionRate = BigDecimal.ZERO;
    private Map<Integer, Integer> requiredLevelCounts;
}
