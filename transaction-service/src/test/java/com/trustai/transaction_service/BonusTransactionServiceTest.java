/*
package com.trustai.transaction_service;


import com.trustai.common.enums.PaymentGateway;
import com.trustai.common.enums.TransactionType;
import com.trustai.transaction_service.entity.Transaction;
import com.trustai.transaction_service.repository.TransactionRepository;
import com.trustai.transaction_service.service.WalletService;
import com.trustai.transaction_service.service.impl.BonusTransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BonusTransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private WalletService walletService;

    @InjectMocks
    private BonusTransactionServiceImpl bonusTransactionService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void applySignupBonus_shouldCreateTransactionSuccessfully() {
        Long userId = 1L;
        BigDecimal bonusAmount = new BigDecimal("100.00");

        when(walletService.getWalletBalance(userId)).thenReturn(new BigDecimal("200.00"));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        Transaction txn = bonusTransactionService.applySignupBonus(userId, bonusAmount);

        assertEquals(TransactionType.BONUS, txn.getTxnType());
        assertEquals(PaymentGateway.SYSTEM, txn.getGateway());
        assertEquals("Signup Bonus", txn.getRemarks());
        assertEquals("signup_bonus", txn.getMetaInfo());
        assertEquals(new BigDecimal("300.00"), txn.getBalance());

        verify(walletService).updateBalanceFromTransaction(userId, bonusAmount);
        verify(transactionRepository).save(txn);
    }

    @Test
    void applyReferralBonus_shouldCreateTransactionSuccessfully() {
        Long referrerUserId = 10L;
        Long referredUserId = 20L;
        BigDecimal bonusAmount = new BigDecimal("50.00");

        when(walletService.getWalletBalance(referrerUserId)).thenReturn(new BigDecimal("100.00"));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        Transaction txn = bonusTransactionService.applyReferralBonus(referrerUserId, referredUserId, bonusAmount);

        assertEquals(TransactionType.BONUS, txn.getTxnType());
        assertEquals("Referral Bonus for referring user: 20", txn.getRemarks());
        assertEquals("referral_bonus", txn.getMetaInfo());
        assertEquals(new BigDecimal("150.00"), txn.getBalance());

        verify(walletService).updateBalanceFromTransaction(referrerUserId, bonusAmount);
        verify(transactionRepository).save(txn);
    }

    @Test
    void applyBonus_shouldCreateGenericBonusSuccessfully() {
        Long userId = 1L;
        BigDecimal bonusAmount = new BigDecimal("25.00");
        String reason = "Leaderboard reward";

        when(walletService.getWalletBalance(userId)).thenReturn(new BigDecimal("75.00"));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        Transaction txn = bonusTransactionService.applyBonus(userId, bonusAmount, reason);

        assertEquals(TransactionType.BONUS, txn.getTxnType());
        assertEquals("Bonus: Leaderboard reward", txn.getRemarks());
        assertEquals("custom_bonus", txn.getMetaInfo());
        assertEquals(new BigDecimal("100.00"), txn.getBalance());

        verify(walletService).updateBalanceFromTransaction(userId, bonusAmount);
        verify(transactionRepository).save(txn);
    }

    @Test
    void applyInterest_shouldCreateInterestTransactionSuccessfully() {
        Long userId = 2L;
        BigDecimal interestAmount = new BigDecimal("15.50");
        String period = "July 2025";

        when(walletService.getWalletBalance(userId)).thenReturn(new BigDecimal("200.00"));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        Transaction txn = bonusTransactionService.applyInterest(userId, interestAmount, period);

        assertEquals(TransactionType.INTEREST, txn.getTxnType());
        assertEquals("Interest for period: July 2025", txn.getRemarks());
        assertEquals("interest_payment", txn.getMetaInfo());
        assertEquals(new BigDecimal("215.50"), txn.getBalance());

        verify(walletService).updateBalanceFromTransaction(userId, interestAmount);
        verify(transactionRepository).save(txn);
    }
}
*/
