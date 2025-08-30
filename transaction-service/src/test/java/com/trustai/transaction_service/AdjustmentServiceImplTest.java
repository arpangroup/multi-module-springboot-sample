/*
package com.trustai.transaction_service;


import com.trustai.common.enums.PaymentGateway;
import com.trustai.common.enums.TransactionType;
import com.trustai.transaction_service.entity.Transaction;
import com.trustai.transaction_service.repository.TransactionRepository;
import com.trustai.transaction_service.service.WalletService;
import com.trustai.transaction_service.service.impl.AdjustmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdjustmentServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private WalletService walletService;

    @InjectMocks
    private AdjustmentServiceImpl adjustmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void subtract_shouldCreateAdjustmentTransactionSuccessfully() {
        Long userId = 1L;
        BigDecimal amountToSubtract = new BigDecimal("50.00");

        when(walletService.getWalletBalance(userId)).thenReturn(new BigDecimal("200.00"));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transaction result = adjustmentService.subtractBalance(userId, amountToSubtract, "penalty");

        assertNotNull(result);
        assertEquals(TransactionType.SUBTRACT, result.getTxnType());
        assertEquals(PaymentGateway.SYSTEM, result.getGateway());
        assertEquals("Adjustment: penalty", result.getRemarks());
        assertEquals("manual_adjustment", result.getMetaInfo());
        assertEquals(new BigDecimal("150.00"), result.getBalance());

        verify(walletService).ensureSufficientBalance(userId, amountToSubtract);
        verify(walletService).updateBalanceFromTransaction(userId, amountToSubtract.negate());
        verify(transactionRepository).save(result);
    }

    @Test
    void subtract_shouldThrowExceptionIfAmountZeroOrNegative() {
        Long userId = 1L;
        BigDecimal zeroAmount = BigDecimal.ZERO;

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> adjustmentService.subtractBalance(userId, zeroAmount, "invalid"));

        assertEquals("Amount to subtract must be greater than zero.", ex.getMessage());

        verify(walletService, never()).ensureSufficientBalance(any(), any());
        verify(transactionRepository, never()).save(any());
    }
}*/
