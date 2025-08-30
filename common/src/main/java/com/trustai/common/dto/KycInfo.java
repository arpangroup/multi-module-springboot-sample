package com.trustai.common.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KycInfo {
    private Long kycId;

    private String email;
    private String phone;
    private String address;

    private String documentType;
    private String emailVerifyStatus;
    private String phoneVerifyStatus;
    public String status;

    private String kycRejectionReason;
}
