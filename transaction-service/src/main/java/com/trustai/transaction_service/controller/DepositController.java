package com.trustai.transaction_service.controller;

import com.trustai.common.controller.BaseController;
import com.trustai.common.dto.ApiResponse;
import com.trustai.common.dto.PagedResponse;
import com.trustai.common.utils.RequestContextHolderUtils;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/deposits")
@RequiredArgsConstructor
@Slf4j
public class DepositController extends BaseController {
    private final DepositService depositService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PagedResponse<DepositHistoryItem>> depositHistory(
            @RequestParam(required = false) PendingDeposit.DepositStatus status,
            Pageable pageable
    ) {
        String currentUserId = getCurrentUserId() + "";
        log.info("Received request for deposit history. userId: {}, Status: {}, Page: {}, Size: {}", currentUserId, status, pageable.getPageNumber(), pageable.getPageSize());

        Page<DepositHistoryItem> transactions;
        if (isAdmin()) {
            transactions = depositService.getDepositHistory(status, pageable);
        } else {
            transactions = depositService.getDepositHistory(status, pageable);
        }
        log.info("Fetched {} deposit transactions.", transactions.getNumberOfElements());
        return ResponseEntity.ok(PagedResponse.from(transactions));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> depositNow(@RequestBody @Valid DepositRequest request) {
        //return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("Automatic Deposit is Currently Disabled in Backend"));
        log.info("Received deposit request: {}", request);
        Long currentUserId = getCurrentUserId();
        PendingDeposit deposit = depositService.deposit(currentUserId, request);
        log.info("Standard deposit completed for userId: {}. Transaction ID: {}", currentUserId, deposit.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Deposit successfully completed."));
    }

    @PostMapping(value = "/manual", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse> manualDeposit(
            @RequestParam("amount") BigDecimal amount,
            @RequestParam("paymentGateway") String paymentGateway,
            @RequestParam("txnId") String txnId,
            @RequestPart("screenshot") MultipartFile screenshot
    ) {
        //return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("Automatic Deposit is Currently Disabled in Backend"));
        Long currentUserId = getCurrentUserId();
        log.info("Received deposit request for userId: {}, amount: {}, txnId: {}", currentUserId, amount, txnId);
        PendingDeposit deposit = depositService.depositManual(currentUserId, amount, paymentGateway, txnId, screenshot);
        log.info("Standard deposit completed for userId: {}. Transaction ID: {}", currentUserId, deposit.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Deposit successfully completed."));
    }

    // Only by ADMIN
    /*@PostMapping("/manual")
    public ResponseEntity<ApiResponse> manualDeposit(@RequestBody @Valid ManualDepositRequest request) {
        log.info("Received manualDeposit request: {}", request);
        Long currentUserId = getCurrentUserId();
        PendingDeposit pendingDeposit = depositService.depositManual(currentUserId, request, ADMIN_USER);
        log.info("Manual deposit completed for userId: {}. PendingDeposit ID: {}", currentUserId, pendingDeposit.getId());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(ApiResponse.success("Deposit Request Accepted!"));
    }*/

    @PostMapping("/approve/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> approve(@PathVariable Long id) {
        log.info("API called to approve deposit ID {}", id);
        depositService.approvePendingDeposit(id, getCurrentUsername());
        return ResponseEntity.ok(ApiResponse.success("Deposit approved successfully."));
    }

    @PostMapping("/reject/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> reject(@PathVariable Long id, @RequestBody @Valid RejectDepositRequest request) {
        log.info("API called to reject deposit ID {} with reason: {}", id, request.rejectionReason());
        depositService.rejectPendingDeposit(id, getCurrentUsername(), request.rejectionReason());
        return ResponseEntity.ok(ApiResponse.error("Deposit rejected successfully."));
    }

}
