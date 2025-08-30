package com.trustai.transaction_service.controller;

import com.trustai.common.dto.ApiResponse;
import com.trustai.transaction_service.dto.request.BalanceAdjustmentRequest;
import com.trustai.transaction_service.service.AdjustmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transactions/adjustments")
@RequiredArgsConstructor
@Slf4j
public class BalanceAdjustmentController {
    private final AdjustmentService adjustmentService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addBalance(@Valid @RequestBody BalanceAdjustmentRequest request) {
        log.info("Received add balance request for userId={}, amount={}, reason={}", request.getUserId(), request.getAmount(), request.getRemarks());
        adjustmentService.addBalance(request.getUserId(), request.getAmount(), request.getRemarks());
        log.info("Balance successfully added for userId={}", request.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Balance added successfully"));
    }

    @PostMapping("/subtract")
    public ResponseEntity<ApiResponse> subtractBalance(@Valid @RequestBody BalanceAdjustmentRequest request) {
        log.info("Received subtract balance request for userId={}, amount={}, reason={}", request.getUserId(), request.getAmount(), request.getRemarks());
        adjustmentService.subtractBalance(request.getUserId(), request.getAmount(), request.getRemarks());
        log.info("Balance successfully subtracted for userId={}", request.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Balance subtract successfully"));
    }
}
