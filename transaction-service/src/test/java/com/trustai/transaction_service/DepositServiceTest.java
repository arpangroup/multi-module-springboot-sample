/*
package com.trustai.transaction_service;


import com.trustai.common.client.UserClient;
import com.trustai.common.enums.PaymentGateway;
import com.trustai.common.enums.TransactionType;
import com.trustai.transaction_service.dto.request.DepositRequest;
import com.trustai.transaction_service.entity.Transaction;
import com.trustai.transaction_service.repository.TransactionRepository;
import com.trustai.transaction_service.service.impl.DepositServiceImpl;
import com.trustai.transaction_service.service.impl.WalletServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

//@ExtendWith(MockitoExtension.class)
class DepositServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private WalletServiceImpl walletService;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private DepositServiceImpl depositService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deposit_shouldCreateTransactionSuccessfully() {
        // Arrange
        DepositRequest request = new DepositRequest(
                1L,
                new BigDecimal("100.00"),
                PaymentGateway.BINANCE,
                "TXN123",
                "bonus"
        );

        when(walletService.getWalletBalance(1L)).thenReturn(new BigDecimal("50.00"));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Transaction result = depositService.deposit(request);

        // Assert
        assertNotNull(result);
        assertEquals(TransactionType.DEPOSIT, result.getTxnType());
        assertEquals("TXN123", result.getTxnRefId());
        assertEquals(new BigDecimal("100.00"), result.getAmount());
        assertEquals("bonus", result.getMetaInfo());

        verify(walletService).updateBalanceFromTransaction(eq(1L), eq(new BigDecimal("90.00")), eq(TransactionType.DEPOSIT));
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void depositManual_shouldCreateManualTransactionSuccessfully() {
        // Arrange
        when(walletService.getWalletBalance(1L)).thenReturn(BigDecimal.ZERO);
        when(transactionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Transaction txn = depositService.depositManual(1L, 999L, new BigDecimal("150.00"), "Admin Topup");

        // Assert
        assertEquals(TransactionType.DEPOSIT_MANUAL, txn.getTxnType());
        assertEquals(PaymentGateway.SYSTEM, txn.getGateway());
        assertEquals("Admin Topup", txn.getRemarks());
        assertEquals(999L, txn.getSenderId());

        verify(transactionRepository).save(any(Transaction.class));
        verify(walletService).updateBalanceFromTransaction(1L, new BigDecimal("150.00"), TransactionType.DEPOSIT);
    }

    @Test
    void getTotalDeposit_shouldReturnSum() {
        when(transactionRepository.sumAmountByUserIdAndTxnType(1L, TransactionType.DEPOSIT)).thenReturn(new BigDecimal("300.00"));
        assertEquals(new BigDecimal("300.00"), depositService.getTotalDeposit(1L));
    }

    @Test
    void isDepositExistsByTxnRef_shouldReturnTrueIfExists() {
        when(transactionRepository.findByTxnRefId("TXN123")).thenReturn(Optional.of(mock(Transaction.class)));
        assertTrue(depositService.isDepositExistsByTxnRef("TXN123"));
    }

    @Test
    void getDepositHistory_shouldReturnPagedData() {
        Transaction txn = mock(Transaction.class);
        Page<Transaction> page = new PageImpl<>(List.of(txn));

        when(transactionRepository.findByUserIdAndTxnType(1L, TransactionType.DEPOSIT, PageRequest.of(0, 10))).thenReturn(page);

        Page<Transaction> result = depositService.getDepositHistory(1L, PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        verify(transactionRepository).findByUserIdAndTxnType(1L, TransactionType.DEPOSIT, PageRequest.of(0, 10));
    }

    @Test
    void confirmGatewayDeposit_shouldUpdateStatusAndMetaInfo() {
        Transaction txn = new Transaction();
        txn.setTxnRefId("TXN999");
        when(transactionRepository.findByTxnRefId("TXN999")).thenReturn(Optional.of(txn));
        when(transactionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Transaction result = depositService.confirmGatewayDeposit("TXN999", "success_json");

        assertEquals(Transaction.TransactionStatus.SUCCESS, result.getStatus());
        assertEquals("success_json", result.getMetaInfo());
    }

    @Test
    void confirmGatewayDeposit_shouldThrowIfNotFound() {
        when(transactionRepository.findByTxnRefId("INVALID")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> depositService.confirmGatewayDeposit("INVALID", "json"));
    }

    // ####################################
    @Test
    void deposit_shouldApplyTxnFeeAndUpdateWalletCorrectly_whenNonSystemGateway() {
        Long userId = 1L;
        BigDecimal currentBalance = new BigDecimal("1000.00");
        BigDecimal depositAmount = new BigDecimal("500.00");
        BigDecimal txnFee = new BigDecimal("20.00");
        BigDecimal newBalance = new BigDecimal("1480.00"); // currentBalance + (depositAmount - txnFee)


        when(walletService.getWalletBalance(userId)).thenReturn(currentBalance);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DepositRequest request = new DepositRequest(
                userId,
                depositAmount, // deposit
                PaymentGateway.BINANCE,   // non-system paymentGateway
                txnFee,  // fee
                "txn123",
                "deposit via binance"
        );

        Transaction result = depositService.deposit(request);

        ArgumentCaptor<Transaction> txnCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(txnCaptor.capture());
        Transaction savedTxn = txnCaptor.getValue();

        // Assertions
        assertEquals(depositAmount, savedTxn.getAmount());
        assertEquals(txnFee, savedTxn.getTxnFee());
        assertEquals(newBalance, savedTxn.getBalance()); // 1000 + (500 - 20)
        assertEquals("txn123", savedTxn.getTxnRefId());
        assertEquals(TransactionType.DEPOSIT, savedTxn.getTxnType());
        assertEquals(Transaction.TransactionStatus.SUCCESS, savedTxn.getStatus());
        assertEquals(PaymentGateway.BINANCE, savedTxn.getGateway());

        verify(walletService).updateBalanceFromTransaction(eq(userId), eq(new BigDecimal("480.00")), eq(TransactionType.DEPOSIT));
    }

    @Test
    void deposit_shouldUpdateWalletAndDepositBalancesCorrectly() {
        Long userId = 1L;
        BigDecimal initialWallet = new BigDecimal("1000.00");
        BigDecimal initialDeposit = new BigDecimal("500.00");
        BigDecimal amount = new BigDecimal("500.00");
        BigDecimal txnFee = new BigDecimal("20.00");
        BigDecimal netAmount = amount.subtract(txnFee); // 480
        BigDecimal expectedWallet = initialWallet.add(netAmount); // 1480
        BigDecimal expectedDeposit = initialDeposit.add(netAmount); // 980

        //  1. Set up mocks : Mock user balances before deposit
        when(userClient.findWalletBalanceById(userId)).thenReturn(Optional.of(initialWallet));
        when(transactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        // BEFORE deposit:
        when(walletService.getWalletBalance(userId)).thenReturn(initialWallet);

        DepositRequest request = new DepositRequest(
                userId,
                amount,
                PaymentGateway.BINANCE, // // Non-system paymentGateway to include fee
                txnFee,
                "txn123",
                "Deposit via BINANCE"
        );

        // 2. Act
        Transaction result = depositService.deposit(request);

        // AFTER deposit, simulate updated state:
        when(walletService.getWalletBalance(userId)).thenReturn(expectedWallet);

        // 3. Assert transaction state
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(amount, result.getAmount());
        assertEquals(txnFee, result.getTxnFee());
        assertEquals(expectedWallet, result.getBalance()); // new wallet balance in transaction

        // Verify transaction repository
        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(captor.capture());

        Transaction savedTxn = captor.getValue();
        assertEquals(amount, savedTxn.getAmount());
        assertEquals(txnFee, savedTxn.getTxnFee());
        assertEquals(expectedWallet, savedTxn.getBalance());

        // 4. Now simulate post-state to test updated wallet/deposit from service
        when(userClient.findWalletBalanceById(userId)).thenReturn(Optional.of(expectedWallet));

        BigDecimal actualWallet = walletService.getWalletBalance(userId);
        //BigDecimal actualDeposit = walletService.findDepositBalanceById(userId).orElse(BigDecimal.ZERO);

        // 5. Final assertions
        assertEquals(expectedWallet, actualWallet, "Wallet balance should be updated correctly");
        //assertEquals(expectedDeposit, actualDeposit, "Deposit balance should be updated correctly");
    }
}*/
