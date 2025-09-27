package com.trustai.transaction_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Entity
@Table(name = "withdraw_rules")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class WithdrawRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, unique = true)
    private String rankCode; // e.g. RANK_0, RANK_1 ...

    private int requiredTotalMembers;
    private int requiredDirectReferrals;
    private int withdrawLimit;
    private BigDecimal maxWithdrawAmount;
}
