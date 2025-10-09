package com.trustai.transaction_service.controller;

import com.trustai.common.controller.BaseController;
import com.trustai.common.dto.PagedResponse;
import com.trustai.transaction_service.dto.response.TransactionDTO;
import com.trustai.transaction_service.entity.Transaction;
import com.trustai.transaction_service.service.TransactionQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Slf4j
public class TransactionQueryController extends BaseController {
    private final TransactionQueryService transactionService;

    @GetMapping
    public ResponseEntity<PagedResponse<TransactionDTO>> getTransactions(
            @RequestParam(required = false) Transaction.TransactionStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size
    ) {
        String currentUserId = getCurrentUserId() + "";
        log.info("Received request to get transactions with status: {}, start: {}, end: {}, page: {}, size: {}", status, start, end, page, size);

        Page<Transaction> paginatedTransactions;
        if (!isAdmin()) {
            paginatedTransactions = transactionService.getTransactionsByUserId(currentUserId, status, start, end, page, size);
        } else {
            paginatedTransactions = transactionService.getTransactions(status, start, end, page, size);
        }

        // Convert to DTOs
        Page<TransactionDTO> dtoPage = paginatedTransactions.map(TransactionDTO::new);

        return ResponseEntity.ok(PagedResponse.from(dtoPage));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<PagedResponse<Transaction>> getTransactionsByUserId(
            @PathVariable String userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("Received request to get transactions for userId: {}, page: {}, size: {}", userId, page, size);
        Page<Transaction> paginatedTransactions = transactionService.getTransactionsByUserId(userId, null, start, end, page, size);
        log.info("Returning {} transactions for userId: {}", paginatedTransactions.getNumberOfElements(), userId);
        return ResponseEntity.ok(PagedResponse.from(paginatedTransactions));
    }

    @GetMapping("/{txnRefId}")
    public ResponseEntity<Transaction> getTransactionByTxnRefId(@PathVariable String txnRefId) {
        log.info("Received request to get transaction with txnRefId: {}", txnRefId);
        Transaction transaction = transactionService.findByTxnRefId(txnRefId);
        return ResponseEntity.ok(transaction);
    }
}
