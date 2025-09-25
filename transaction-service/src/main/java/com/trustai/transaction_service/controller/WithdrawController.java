package com.trustai.transaction_service.controller;

import com.trustai.common.controller.BaseController;
import com.trustai.common.dto.ApiResponse;
import com.trustai.common.dto.PagedResponse;
import com.trustai.transaction_service.dto.request.RejectDepositRequest;
import com.trustai.transaction_service.dto.request.WithdrawRequest;
import com.trustai.transaction_service.dto.response.WithdrawHistoryItem;
import com.trustai.transaction_service.entity.PendingWithdraw;
import com.trustai.transaction_service.entity.Transaction;
import com.trustai.transaction_service.service.TransactionQueryService;
import com.trustai.transaction_service.service.WithdrawalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/withdraws")
@RequiredArgsConstructor
@Slf4j
public class WithdrawController extends BaseController {
    private final WithdrawalService withdrawalService;
    private final TransactionQueryService transactionQueryService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PagedResponse<WithdrawHistoryItem>> WithdrawHistory(
            @RequestParam(required = false) String status,
            Pageable pageable
    ) {
        Long userId = isAdmin() ? null : getCurrentUserId();
        log.info("Received request for withdraw history. Status: {}, Page: {}, Size: {}", status, pageable.getPageNumber(), pageable.getPageSize());

        PendingWithdraw.WithdrawStatus withdrawStatus = null;
        if ("PENDING".equalsIgnoreCase(status)) {
            withdrawStatus = PendingWithdraw.WithdrawStatus.PENDING;
        } else if("REJECTED".equalsIgnoreCase(status)) {
            withdrawStatus = PendingWithdraw.WithdrawStatus.REJECTED;
        } else if("APPROVED".equalsIgnoreCase(status)) {
            withdrawStatus = PendingWithdraw.WithdrawStatus.APPROVED;
        }

        Page<WithdrawHistoryItem> transactions = withdrawalService.getWithdrawHistory(userId, withdrawStatus, pageable);
        //log.info("Fetched {} deposit transactions.", transactions.getNumberOfElements());
        return ResponseEntity.ok(PagedResponse.from(transactions));
    }

    @PostMapping("/request")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<String>> requestWithdraw(@RequestBody @Valid WithdrawRequest request) {
        Long currentUserId = getCurrentUserId();
        log.info("Received withdraw request for userId: {}, amount: {}, walletAddress: {}", currentUserId, request.amount(), request.walletAddress());
        PendingWithdraw transaction = withdrawalService.requestWithdraw(currentUserId, request.amount(), null);
        log.info("Withdraw request completed for userId: {}. Transaction ID: {}", currentUserId, transaction.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Withdraw request successfully completed."));
    }

    @PostMapping("/approve/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> approve(@PathVariable Long id) {
        withdrawalService.approveWithdraw(id, getCurrentUsername());
        return ResponseEntity.ok(ApiResponse.success("Withdraw request approved successfully."));
    }

    @PostMapping("/reject/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> reject(@PathVariable Long id, @RequestBody @Valid RejectDepositRequest request) {
        withdrawalService.rejectWithdraw(id, getCurrentUsername(), request.rejectionReason());
        return ResponseEntity.ok(ApiResponse.error("Withdraw request rejected successfully."));
    }


}
