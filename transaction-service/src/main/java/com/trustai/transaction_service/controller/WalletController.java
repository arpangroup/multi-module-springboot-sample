package com.trustai.transaction_service.controller;

import com.trustai.common.dto.WalletUpdateRequest;
import com.trustai.transaction_service.entity.Transaction;
import com.trustai.transaction_service.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/wallet")
@RequiredArgsConstructor
@Slf4j
public class WalletController {
    private final WalletService walletService;

    @GetMapping("/balance/{userId}")
    public ResponseEntity<BigDecimal> getWalletBalance(@PathVariable Long userId) {
        log.info("Fetching wallet balance for userId: {}", userId);
        return ResponseEntity.ok(walletService.getWalletBalance(userId));
    }

    @PostMapping("/update/{userId}")
    public ResponseEntity<Transaction> updateWalletBalance(@PathVariable Long userId, @RequestBody WalletUpdateRequest request) {
        log.info("Initiating wallet update [{}] for userId: {}, amount: {}, type: {}, remarks: {}, source: {}",
                request.isCredit() ? "CREDIT" : "DEBIT",  userId, request.amount(), request.transactionType(), request.remarks(), request.sourceModule());

        Transaction txn = walletService.updateWalletBalance(userId, request.amount(), request.transactionType(), request.sourceModule(), request.isCredit(), request.remarks(), request.metaInfo());

        log.info("Wallet transaction successful for userId: {}, txnId: {}, newBalance: {}", userId, txn.getId(), txn.getBalance());
        return ResponseEntity.ok(txn);
    }
}
