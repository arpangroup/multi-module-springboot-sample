package com.trustai.common.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
//@JsonInclude(JsonInclude.Include.NON_NULL) // Only this field will be excluded if null
public class UserInfo {
    private Long id;
    private String accountId;
    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private String mobile;
    private String rankCode;
    private int point;
    private String image;
    private String country;
    private String countryCode;
    private String walletAddress;
    // Balance
    private BigDecimal walletBalance;
    private BigDecimal profitWallet;
    // Referral
    private String referralCode;
    //Status:
    private boolean isActive;
    private String accountStatus;
    private String kycStatus;
    //Address:
    private String state;
    private String city;
    private String address;
    private String zipCode;
    //Date:
    private LocalDateTime createdAt;

    public UserInfo(Long id, String username) {
        this.id = id;
        this.username = username;
    }

}
