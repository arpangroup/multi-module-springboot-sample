package com.trustai.transaction_service.controller;

import com.trustai.common.dto.ApiResponse;
import com.trustai.transaction_service.dto.request.RejectDepositRequest;
import com.trustai.transaction_service.dto.response.DepositHistoryItem;
import com.trustai.transaction_service.dto.request.DepositRequest;
import com.trustai.transaction_service.dto.request.ManualDepositRequest;
import com.trustai.transaction_service.entity.PendingDeposit;
import com.trustai.transaction_service.entity.Transaction;
import com.trustai.transaction_service.service.DepositService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/deposits")
@RequiredArgsConstructor
@Slf4j
public class DepositController {
    private final DepositService depositService;
    private String ADMIN_USER = "Admin";

    @GetMapping
    public ResponseEntity<Page<DepositHistoryItem>> depositHistory(
            @RequestParam(required = false) PendingDeposit.DepositStatus status,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size
    ) {
        log.info("Received request for deposit history. Status: {}, Page: {}, Size: {}", status, page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<DepositHistoryItem> transactions = depositService.getDepositHistory(status, pageable);
        log.info("Fetched {} deposit transactions.", transactions.getNumberOfElements());
        return ResponseEntity.ok(transactions);
    }

    @PostMapping
    public ResponseEntity<ApiResponse> depositNow(@RequestBody @Valid DepositRequest request) {
        //return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("Automatic Deposit is Currently Disabled in Backend"));
        log.info("Received deposit request: {}", request);
        PendingDeposit deposit = depositService.deposit(request);
        log.info("Standard deposit completed for userId: {}. Transaction ID: {}", request.getUserId(), deposit.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Deposit successfully completed."));
    }

    // Only by ADMIN
    @PostMapping("/manual")
    public ResponseEntity<ApiResponse> manualDeposit(@RequestBody @Valid ManualDepositRequest request) {
        log.info("Received manualDeposit request: {}", request);
        PendingDeposit pendingDeposit = depositService.depositManual(request, ADMIN_USER);
        log.info("Manual deposit completed for userId: {}. PendingDeposit ID: {}", request.getUserId(), pendingDeposit.getId());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(ApiResponse.success("Deposit Request Accepted!"));
    }

    @PostMapping("/approve/{id}")
    public ResponseEntity<ApiResponse> approve(@PathVariable Long id) {
        depositService.approvePendingDeposit(id, "ADMIN_USER");
        return ResponseEntity.ok(ApiResponse.success("Deposit approved successfully."));
    }

    @PostMapping("/reject/{id}")
    public ResponseEntity<ApiResponse> reject(@PathVariable Long id, @RequestBody @Valid RejectDepositRequest request) {
        depositService.rejectPendingDeposit(id, ADMIN_USER, request.rejectionReason());
        return ResponseEntity.ok(ApiResponse.error("Deposit rejected successfully."));
    }

}
