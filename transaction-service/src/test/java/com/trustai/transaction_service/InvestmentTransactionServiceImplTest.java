/*
package com.trustai.transaction_service;


import com.trustai.common.enums.TransactionType;
import com.trustai.transaction_service.entity.Transaction;
import com.trustai.transaction_service.repository.TransactionRepository;
import com.trustai.transaction_service.service.WalletService;
import com.trustai.transaction_service.service.impl.InvestmentTransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InvestmentTransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private WalletService walletService;

    @InjectMocks
    private InvestmentTransactionServiceImpl investmentTransactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void invest_shouldCreateInvestmentTransactionSuccessfully() {
        // Given
        Long userId = 1L;
        BigDecimal amount = new BigDecimal("250.00");
        BigDecimal currentBalance = new BigDecimal("1000.00");
        String investmentType = "staking";
        String metaInfo = "staking_lock_90days";

        when(walletService.getWalletBalance(userId)).thenReturn(currentBalance);
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Transaction result = investmentTransactionService.invest(userId, amount, investmentType, metaInfo);

        // Then
        assertNotNull(result);
        assertEquals(TransactionType.INVESTMENT, result.getTxnType());
        assertEquals("Investment in: staking", result.getRemarks());
        assertEquals("staking_lock_90days", result.getMetaInfo());
        assertEquals(currentBalance.subtract(amount), result.getBalance());
        assertEquals(Transaction.TransactionStatus.SUCCESS, result.getStatus());

        verify(walletService).ensureSufficientBalance(userId, amount);
        verify(walletService).updateBalanceFromTransaction(userId, amount.negate());
        verify(transactionRepository).save(result);
    }

    @Test
    void invest_shouldThrowIfInsufficientBalance() {
        Long userId = 2L;
        BigDecimal amount = new BigDecimal("999.99");

        doThrow(new IllegalArgumentException("Insufficient funds"))
                .when(walletService).ensureSufficientBalance(userId, amount);

        assertThrows(IllegalArgumentException.class,
                () -> investmentTransactionService.invest(userId, amount, "staking", "meta"));

        verify(walletService).ensureSufficientBalance(userId, amount);
        verify(walletService, never()).updateBalanceFromTransaction(any(), any());
        verify(transactionRepository, never()).save(any());
    }
}*/
