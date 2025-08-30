package com.trustai.rank_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RankConfigDto {
    private String code;
    private String displayName;
    private int rankOrder;

    private BigDecimal minDepositAmount;
    private BigDecimal minInvestmentAmount;
    private Integer minDirectReferrals;

    private BigDecimal minReferralTotalDeposit;
    private BigDecimal minReferralTotalInvestment;
    private BigDecimal minTotalEarnings;

    private Integer txnPerDay;

    private int minLevel1Count;
    private int minLevel2Count;
    private int minLevel3Count;

    private BigDecimal commissionPercentage;
    private int rankBonus;

    private String rewardType; // Enum type expected on the backend
    private String rankType;   // Enum type expected on the backend

    private String description;
    private boolean active;
}
