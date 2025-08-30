/*
package com.trustai.transaction_service;


import com.trustai.common.enums.TransactionType;
import com.trustai.transaction_service.entity.Transaction;
import com.trustai.transaction_service.repository.TransactionRepository;
import com.trustai.transaction_service.service.WalletService;
import com.trustai.transaction_service.service.impl.RefundServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RefundServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private WalletService walletService;

    @InjectMocks
    private RefundServiceImpl refundService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void refund_shouldCreateTransactionSuccessfully() {
        // Given
        Long userId = 1L;
        BigDecimal amount = new BigDecimal("150.00");
        String originalTxnRef = "TXN123ABC";
        String reason = "duplicate_payment";
        BigDecimal currentBalance = new BigDecimal("500.00");

        when(walletService.getWalletBalance(userId)).thenReturn(currentBalance);
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Transaction result = refundService.refund(userId, amount, originalTxnRef, reason);

        // Then
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(amount, result.getAmount());
        assertEquals(TransactionType.REFUND, result.getTxnType());
        assertEquals("Refund for txnRef: " + originalTxnRef, result.getRemarks());
        assertEquals(reason, result.getMetaInfo());
        assertEquals(Transaction.TransactionStatus.SUCCESS, result.getStatus());
        assertEquals(currentBalance.add(amount), result.getBalance());

        verify(walletService).getWalletBalance(userId);
        verify(walletService).updateBalanceFromTransaction(userId, amount);
        verify(transactionRepository).save(result);
    }
}*/
