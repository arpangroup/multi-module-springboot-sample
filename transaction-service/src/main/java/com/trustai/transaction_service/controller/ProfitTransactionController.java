package com.trustai.transaction_service.controller;

import com.trustai.transaction_service.entity.Transaction;
import com.trustai.transaction_service.service.TransactionQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/profits")
@RequiredArgsConstructor
@Slf4j
public class ProfitTransactionController {
    private final TransactionQueryService transactionService;

    @GetMapping
    public ResponseEntity<Page<Transaction>> getAllProfits(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size
    ) {
        Page<Transaction> paginatedUsers = transactionService.getProfits(page, size);
        return ResponseEntity.ok(paginatedUsers);
    }
}
