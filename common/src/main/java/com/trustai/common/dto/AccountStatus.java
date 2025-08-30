package com.trustai.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountStatus {
    private boolean isAccountActive;
    private boolean isKycVerified;
    private boolean isEmailVerified;
    private boolean isPhoneVerified;

    private boolean isDepositEnabled;
    private boolean isWithdrawEnabled;
    private boolean isSendMoneyEnabled;

    private String accountStatus; // [ACTIVE, DISABLED, PENDING, SUSPENDED, DELETED, BANNED, LOCKED, INACTIVE]
    private String kycStatus;     // [PENDING, UNVERIFIED, VERIFIED, REJECTED]
    private String emailVerifyStatus;
    private String phoneVerifyStatus;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String kycRejectionReason = null; // Will be omitted if null
}
