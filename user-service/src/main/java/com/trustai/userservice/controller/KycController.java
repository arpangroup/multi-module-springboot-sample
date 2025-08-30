package com.trustai.userservice.controller;

import com.trustai.userservice.user.dto.KycApproveRequest;
import com.trustai.userservice.user.dto.SimpleKycInfo;
import com.trustai.userservice.user.entity.Kyc;
import com.trustai.userservice.user.service.UserKycService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/kyc")
@RequiredArgsConstructor
@Slf4j
public class KycController {
    private final UserKycService userKycService;

    @GetMapping
    public ResponseEntity<Page<SimpleKycInfo>> allKyc(
            @RequestParam(required = false) Kyc.KycStatus status,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size
    ) {
        Page<SimpleKycInfo> paginatedKycList = userKycService.getAllKyc(status, page, size);
        return ResponseEntity.ok(paginatedKycList);
    }

    @GetMapping("/{kycId}")
    public ResponseEntity<Kyc> getKycById(@PathVariable Long kycId) {
        return ResponseEntity.ok(userKycService.getKycById(kycId));
    }

    @PostMapping("/{kycId}/approve")
    public ResponseEntity<Kyc> approveKyc(@PathVariable Long kycId, @RequestBody KycApproveRequest request) {
        return ResponseEntity.ok(null);
    }

    @PostMapping("/{kycId}/reject")
    public ResponseEntity<Kyc> rejectKyc(@PathVariable Long kycId, @RequestBody KycApproveRequest request) {
        return ResponseEntity.ok(userKycService.getKycById(kycId));
    }

}
