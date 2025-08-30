package com.trustai.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoOld {
    private Long id;
    private String username;
    private String referralCode;
    private BigDecimal walletBalance;

    private int level;

    private boolean active;
    private boolean referralApproved;
    private int rank;

    /*private String firstname;
    private String lastname;
    private String email;
    private String mobile;
    private int referBy;

    private int posId;
    private int position;
    private int planId;
    private float totalInvest;
    private float totalBinaryCom;
    private float totalRefCom;
    private int dailyAdLimit;

    private Address addressDetails;
    private String image;

    private int status;
    private Kyc kycData;

    private String KycStatus;
    private int profileComplete;
    private String banReason;

    private String rememberToken;
    private String provider;
    private int providerId;*/
}
