/*
package com.trustai.transaction_service;


import com.trustai.common.client.UserClient;
import com.trustai.common.enums.TransactionType;
import com.trustai.transaction_service.repository.TransactionRepository;
import com.trustai.transaction_service.service.impl.WalletServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WalletServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private WalletServiceImpl walletService;

    private final Long userId = 1L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // --- getUserBalance() ---

    @Test
    void getUserBalance_shouldReturnBalance_whenWalletExists() {
        when(userClient.findWalletBalanceById(userId)).thenReturn(Optional.of(new BigDecimal("1000.00")));

        BigDecimal balance = walletService.getWalletBalance(userId);

        assertEquals(new BigDecimal("1000.00"), balance);
        verify(userClient).findWalletBalanceById(userId);
    }

    @Test
    void getUserBalance_shouldThrow_whenWalletNotFound() {
        when(userClient.findWalletBalanceById(userId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> walletService.getWalletBalance(userId));
        verify(userClient).findWalletBalanceById(userId);
    }

    // --- updateBalanceFromTransaction(userId, delta) ---

    @Test
    void updateBalanceFromTransaction_shouldUpdateWalletBalance() {
        when(userClient.findWalletBalanceById(userId)).thenReturn(Optional.of(new BigDecimal("100.00")));

        walletService.updateBalanceFromTransaction(userId, new BigDecimal("50.00"));

        verify(userClient).updateWalletBalance(userId, new BigDecimal("150.00"));
    }

    // --- updateBalanceFromTransaction(userId, delta, type) ---

    @Test
    void updateBalanceFromTransaction_shouldAlsoUpdateDepositBalanceForDepositTxn() {
        when(userClient.findWalletBalanceById(userId)).thenReturn(Optional.of(new BigDecimal("200.00")));
        //when(userClient.findDepositBalanceById(userId)).thenReturn(Optional.of(new BigDecimal("50.00")));

        walletService.updateBalanceFromTransaction(userId, new BigDecimal("100.00"));

        verify(userClient).updateWalletBalance(userId, new BigDecimal("300.00"));
        //verify(userClient).updateDepositBalance(userId, new BigDecimal("150.00"));
    }

    @Test
    void updateBalanceFromTransaction_shouldSkipDepositUpdateForNonDepositTxn() {
        when(userClient.findWalletBalanceById(userId)).thenReturn(Optional.of(new BigDecimal("500.00")));

        walletService.updateBalanceFromTransaction(userId, new BigDecimal("50.00"));

        verify(userClient).updateWalletBalance(userId, new BigDecimal("550.00"));
        //verify(userClient, never()).updateDepositBalance(anyLong(), any());
    }

    // --- ensureSufficientBalance() ---

    @Test
    void hasSufficientBalance_shouldPass_whenSufficient() {
        when(userClient.findWalletBalanceById(userId)).thenReturn(Optional.of(new BigDecimal("100.00")));

        assertDoesNotThrow(() -> walletService.ensureSufficientBalance(userId, new BigDecimal("90.00")));
    }

    @Test
    void hasSufficientBalance_shouldFail_whenInsufficient() {
        when(userClient.findWalletBalanceById(userId)).thenReturn(Optional.of(new BigDecimal("30.00")));

        Exception ex = assertThrows(IllegalStateException.class,
                () -> walletService.ensureSufficientBalance(userId, new BigDecimal("50.00")));

        assertEquals("Insufficient wallet balance", ex.getMessage());
    }
}*/
