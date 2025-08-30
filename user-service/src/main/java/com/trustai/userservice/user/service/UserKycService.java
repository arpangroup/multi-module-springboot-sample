package com.trustai.userservice.user.service;

import com.trustai.userservice.user.dto.KycApproveRequest;
import com.trustai.userservice.user.dto.KycUpdateRequest;
import com.trustai.userservice.user.dto.SimpleKycInfo;
import com.trustai.userservice.user.entity.Kyc;
import org.springframework.data.domain.Page;

public interface UserKycService {
    Page<SimpleKycInfo> getAllKyc(Kyc.KycStatus status, Integer page, Integer size);
    Kyc getKycById(Long kycId);

    // Accessible only to User
    Kyc updateKyc(Long userId, KycUpdateRequest request);

    // Accessible only to Admin
    Kyc verifyKyc(Long userId, KycApproveRequest request);
}
