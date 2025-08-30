package com.trustai.userservice.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class KycApproveRequest {
    private String action;
    private boolean isEmailVerified;
    private boolean isPhoneVerified;
    private boolean isAddressVerified;
    private String kycRejectionReason;
}
