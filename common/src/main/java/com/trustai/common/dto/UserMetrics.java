package com.trustai.common.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class UserMetrics {
    private int directReferrals;            // direct
    private BigDecimal totalDeposit;
    private BigDecimal totalInvestment;
    private BigDecimal walletBalance;
    private BigDecimal totalEarnings;
    private UserHierarchyStats userHierarchyStats;

    private BigDecimal totalReferralDeposit;
    private BigDecimal totalReferralInvestment;
}
