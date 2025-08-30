/*
package com.trustai.transaction_service;


import com.trustai.common.enums.PaymentGateway;
import com.trustai.common.enums.TransactionType;
import com.trustai.transaction_service.entity.Transaction;
import com.trustai.transaction_service.repository.TransactionRepository;
import com.trustai.transaction_service.service.WalletService;
import com.trustai.transaction_service.service.impl.WithdrawalServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WithdrawalServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private WalletService walletService;

    @InjectMocks
    private WithdrawalServiceImpl withdrawalService;

    private final Long userId = 101L;
    private final BigDecimal amount = new BigDecimal("300.00");
    private final String destination = "bank-acc-123";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void withdraw_shouldCreateTransactionSuccessfully() {
        // Arrange
        BigDecimal currentBalance = new BigDecimal("1000.00");
        BigDecimal newBalance = currentBalance.subtract(amount);

        when(walletService.getWalletBalance(userId)).thenReturn(currentBalance);
        doNothing().when(walletService).ensureSufficientBalance(userId, amount);
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Transaction result = withdrawalService.withdraw(userId, amount, destination, "Withdraw to bank");

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(amount, result.getAmount());
        assertEquals(newBalance, result.getBalance());
        assertEquals(TransactionType.WITHDRAWAL, result.getTxnType());
        assertEquals(PaymentGateway.SYSTEM, result.getGateway());
        assertEquals(Transaction.TransactionStatus.SUCCESS, result.getStatus());
        assertEquals("Withdraw to bank", result.getRemarks());
        assertEquals(destination, result.getMetaInfo());

        verify(walletService).ensureSufficientBalance(userId, amount);
        verify(walletService).getWalletBalance(userId);
        verify(walletService).updateBalanceFromTransaction(userId, amount.negate());
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void withdraw_shouldThrowIfAmountIsZeroOrNegative() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                withdrawalService.withdraw(userId, BigDecimal.ZERO, destination, null)
        );

        assertEquals("Withdrawal amount must be greater than zero.", ex.getMessage());
        verifyNoInteractions(walletService);
        verifyNoInteractions(transactionRepository);
    }

    @Test
    void withdraw_shouldFallbackToDefaultRemarksIfNull() {
        // Arrange
        BigDecimal currentBalance = new BigDecimal("500.00");

        when(walletService.getWalletBalance(userId)).thenReturn(currentBalance);
        doNothing().when(walletService).ensureSufficientBalance(userId, amount);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Transaction txn = withdrawalService.withdraw(userId, amount, destination, null);

        // Assert
        assertEquals("Withdrawal to: " + destination, txn.getRemarks());
    }
}*/
