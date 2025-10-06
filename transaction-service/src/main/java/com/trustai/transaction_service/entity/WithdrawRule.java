package com.trustai.transaction_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Entity
@Table(name = "withdraw_rules")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class WithdrawRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, unique = true)
    private String rankCode; // e.g. RANK_0, RANK_1 ...

    // % based limits (0–100)
    private BigDecimal withdrawLimitFromWalletInPercentage;
    private BigDecimal withdrawLimitFromProfitWalletInPercentage;

    // Absolute maximums
    private BigDecimal maxWithdrawFromWallet;
    private BigDecimal maxWithdrawFromProfitWallet;

    // Fees & limits
//    private BigDecimal serviceCharge;
    private int dailyWithdrawLimit;
    private int totalWalletWithdrawLimit;
    private int totalProfitWithdrawLimit;
}
