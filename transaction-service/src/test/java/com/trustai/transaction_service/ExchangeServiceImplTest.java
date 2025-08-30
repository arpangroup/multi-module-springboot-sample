/*
package com.trustai.transaction_service;


import com.trustai.common.enums.TransactionType;
import com.trustai.transaction_service.entity.Transaction;
import com.trustai.transaction_service.repository.TransactionRepository;
import com.trustai.transaction_service.service.WalletService;
import com.trustai.transaction_service.service.impl.ExchangeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExchangeServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private WalletService walletService;

    @InjectMocks
    private ExchangeServiceImpl exchangeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void exchange_shouldCreateTransactionSuccessfully() {
        // Given
        Long userId = 1L;
        BigDecimal fromAmount = new BigDecimal("100.00");
        BigDecimal toAmount = new BigDecimal("120.00");
        String fromCurrency = "BTC";
        String toCurrency = "USDT";
        String metaInfo = "binance_rate";

        when(walletService.getWalletBalance(userId)).thenReturn(new BigDecimal("500.00"));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Transaction result = exchangeService.exchange(userId, fromAmount, fromCurrency, toAmount, toCurrency, metaInfo);

        // Then
        assertNotNull(result);
        assertEquals(TransactionType.EXCHANGE, result.getTxnType());
        assertEquals("Exchange: BTC to USDT", result.getRemarks());
        assertEquals("binance_rate", result.getMetaInfo());
        assertEquals(new BigDecimal("520.00"), result.getBalance()); // 500 - 100 + 120

        verify(walletService).ensureSufficientBalance(userId, fromAmount);
        verify(walletService).updateBalanceFromTransaction(userId, toAmount.subtract(fromAmount));
        verify(transactionRepository).save(result);
    }

    @Test
    void exchange_shouldThrowExceptionIfInsufficientBalance() {
        Long userId = 2L;
        BigDecimal fromAmount = new BigDecimal("100.00");
        BigDecimal toAmount = new BigDecimal("120.00");

        doThrow(new IllegalArgumentException("Insufficient balance"))
                .when(walletService).ensureSufficientBalance(userId, fromAmount);

        assertThrows(IllegalArgumentException.class, () ->
                exchangeService.exchange(userId, fromAmount, "BTC", toAmount, "USDT", "binance"));

        verify(walletService).ensureSufficientBalance(userId, fromAmount);
        verify(walletService, never()).updateBalanceFromTransaction(any(), any());
        verify(transactionRepository, never()).save(any());
    }
}*/
