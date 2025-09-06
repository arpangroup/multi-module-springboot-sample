package com.trustai.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDetailsInfo {
    private Long id;
    private String username;
    private String firstname;
    private String lastname;
    private String fullName;
    private String email;
    private String phone;

    // Balance Related
    private BigDecimal walletBalance;
    private BigDecimal profitBalance;

    // Referral & User Hierarchy Related:
    private String referralCode;
    private UserInfo referrer;
    private String rankCode;

    // Kyc
    private KycInfo kyc;

    // Status
    private AccountStatus accountStatus;

    private String createdAt;
}
