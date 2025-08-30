package com.trustai.common.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class RankConfigDto {
    private Long id;
    private String code;
    private String displayName;
    private BigDecimal minDepositAmount;
    private BigDecimal minInvestmentAmount;
    private BigDecimal maxInvestmentAmount;
    private BigDecimal commissionPercentage;
    private int rankOrder;
}
