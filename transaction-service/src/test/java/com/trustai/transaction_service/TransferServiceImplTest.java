/*
package com.trustai.transaction_service;


import com.trustai.common.enums.TransactionType;
import com.trustai.transaction_service.entity.Transaction;
import com.trustai.transaction_service.repository.TransactionRepository;
import com.trustai.transaction_service.service.WalletService;
import com.trustai.transaction_service.service.impl.TransferServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransferServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private WalletService walletService;
*/
/**//*

    @InjectMocks
    private TransferServiceImpl transferService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void transferMoney_shouldTransferSuccessfully() {
        // Given
        long senderId = 100L;
        long receiverId = 200L;
        BigDecimal amount = new BigDecimal("250.00");
        String message = "Gift";

        BigDecimal senderInitialBalance = new BigDecimal("1000.00");
        BigDecimal receiverInitialBalance = new BigDecimal("500.00");

        when(walletService.getWalletBalance(senderId)).thenReturn(senderInitialBalance);
        when(walletService.getWalletBalance(receiverId)).thenReturn(receiverInitialBalance);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        Transaction result = transferService.transferMoney(senderId, receiverId, amount, message);

        // Then
        assertNotNull(result);
        assertEquals(receiverId, result.getUserId());
        assertEquals(TransactionType.RECEIVE_MONEY, result.getTxnType());
        assertEquals(new BigDecimal("750.00"), result.getBalance());
        assertEquals("Gift", result.getMetaInfo());

        verify(walletService).updateBalanceFromTransaction(senderId, amount.negate());
        verify(walletService).updateBalanceFromTransaction(receiverId, amount);
        verify(transactionRepository, times(2)).save(any(Transaction.class)); // one for debit, one for credit
    }

    @Test
    void transferMoney_shouldThrowIfAmountZeroOrNegative() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                transferService.transferMoney(1L, 2L, BigDecimal.ZERO, "Invalid")
        );
        assertEquals("Transfer amount must be greater than zero.", exception.getMessage());

        verifyNoInteractions(walletService);
        verifyNoInteractions(transactionRepository);
    }
}*/
