package com.trustai.userservice.user.mapper;

import com.trustai.userservice.user.dto.SimpleKycInfo;
import com.trustai.userservice.user.entity.Kyc;
import org.springframework.stereotype.Component;

@Component
public class KycMapper {
    public SimpleKycInfo mapTo(Kyc kyc) {
        return SimpleKycInfo.builder()
                .kycId(kyc.getId())
                .fullname(kyc.getFirstname() + " " + kyc.getLastname())
                .documentType(kyc.getDocumentType().name())
                .createdAt(kyc.getCreatedAt().toString())
                .status(kyc.getStatus().name())
//                .email(kyc.getEmail())
//                .phone(kyc.getPhone())
//                .address(kyc.getAddress())
//                .address(kyc.getAddress())
//                .emailVerifyStatus(kyc.getEmailVerifyStatus().name())
//                .phoneVerifyStatus(kyc.getPhoneVerifyStatus().name())
//                .status(kyc.status.name())
//                .kycRejectionReason(kyc.getKycRejectionReason())
                .build();
    }
}
