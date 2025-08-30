/*
package com.trustai.transaction_service;


import com.trustai.common.enums.PaymentGateway;
import com.trustai.common.enums.TransactionType;
import com.trustai.transaction_service.entity.Transaction;
import com.trustai.transaction_service.repository.TransactionRepository;
import com.trustai.transaction_service.service.impl.TransactionQueryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static com.trustai.common.enums.TransactionType.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionQueryServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionQueryServiceImpl service;

    private final Long userId = 100L;
    private final Pageable pageable = PageRequest.of(0, 10);
    private Transaction sampleTxn;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sampleTxn = new Transaction(userId, BigDecimal.TEN, TransactionType.DEPOSIT, BigDecimal.ZERO, true);
    }

    @Test
    void getTransactions_withStatus_shouldCallFindByStatus() {
        Page<Transaction> page = new PageImpl<>(List.of(sampleTxn));
        when(transactionRepository.findByStatus(Transaction.TransactionStatus.SUCCESS, pageable)).thenReturn(page);

        Page<Transaction> result = service.getTransactions(Transaction.TransactionStatus.SUCCESS, 0, 10);

        assertEquals(1, result.getContent().size());
        verify(transactionRepository).findByStatus(Transaction.TransactionStatus.SUCCESS, pageable);
    }

    @Test
    void getTransactions_withoutStatus_shouldCallFindAll() {
        Page<Transaction> page = new PageImpl<>(List.of(sampleTxn));
        when(transactionRepository.findAll(pageable)).thenReturn(page);

        Page<Transaction> result = service.getTransactions(null, 0, 10);

        assertEquals(1, result.getContent().size());
        verify(transactionRepository).findAll(pageable);
    }

    @Test
    void getProfits_shouldCallFindByTxnTypeIn() {
        Page<Transaction> page = new PageImpl<>(List.of(sampleTxn));
        when(transactionRepository.findByTxnTypeIn(anyList(), eq(pageable))).thenReturn(page);

        Page<Transaction> result = service.getProfits(0, 10);

        assertEquals(1, result.getContent().size());
        verify(transactionRepository).findByTxnTypeIn(anyList(), eq(pageable));
    }

    @Test
    void getTransactionsByUserId_shouldReturnPage() {
        when(transactionRepository.findByUserId(userId, pageable)).thenReturn(Page.empty());

        Page<Transaction> result = service.getTransactionsByUserId(userId, 0, 10);

        assertNotNull(result);
        verify(transactionRepository).findByUserId(userId, pageable);
    }

    @Test
    void hasDepositTransaction_shouldReturnTrueIfExists() {
        when(transactionRepository.existsByUserIdAndTxnType(userId, DEPOSIT)).thenReturn(true);

        assertTrue(service.hasDepositTransaction(userId));
    }

    @Test
    void getTransactionsByUserIdAndDateRange_shouldReturnResults() {
        LocalDateTime from = LocalDateTime.now().minusDays(5);
        LocalDateTime to = LocalDateTime.now();
        when(transactionRepository.findByUserIdAndCreatedAtBetween(userId, from, to, pageable)).thenReturn(Page.empty());

        Page<Transaction> result = service.getTransactionsByUserIdAndDateRange(userId, from, to, pageable);

        assertNotNull(result);
        verify(transactionRepository).findByUserIdAndCreatedAtBetween(userId, from, to, pageable);
    }

    @Test
    void findByTxnRefId_shouldReturnTransaction() {
        when(transactionRepository.findByTxnRefId("abc")).thenReturn(Optional.of(sampleTxn));

        Transaction result = service.findByTxnRefId("abc");

        assertEquals(sampleTxn, result);
    }

    @Test
    void findTransfersBySenderId_shouldDelegateCall() {
        when(transactionRepository.findBySenderId(userId, pageable)).thenReturn(Page.empty());

        service.findTransfersBySenderId(userId, pageable);
        verify(transactionRepository).findBySenderId(userId, pageable);
    }

    @Test
    void findTransfersByReceiverId_shouldCallFindByUserId() {
        when(transactionRepository.findByUserId(userId, pageable)).thenReturn(Page.empty());

        service.findTransfersByReceiverId(userId, pageable);
        verify(transactionRepository).findByUserId(userId, pageable);
    }

    @Test
    void findByUserIdAndStatusAndGateway_shouldReturnPage() {
        when(transactionRepository.findByUserIdAndStatusAndGateway(eq(userId), any(), any(), eq(pageable)))
                .thenReturn(Page.empty());

        Page<Transaction> result = service.findByUserIdAndStatusAndGateway(userId, Transaction.TransactionStatus.SUCCESS, PaymentGateway.SYSTEM, pageable);
        assertNotNull(result);
    }

    @Test
    void searchTransactions_shouldReturnFilteredPage() {
        when(transactionRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(Page.empty());

        Page<Transaction> result = service.searchTransactions(
                userId,
                Transaction.TransactionStatus.SUCCESS,
                DEPOSIT,
                PaymentGateway.SYSTEM,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now(),
                pageable
        );

        assertNotNull(result);
        verify(transactionRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void searchByMetaInfo_shouldDelegateCall() {
        service.searchByMetaInfo("bonus", pageable);
        verify(transactionRepository).findByMetaInfoContainingIgnoreCase("bonus", pageable);
    }

    @Test
    void searchByKeyword_shouldDelegateCall() {
        service.searchByKeyword(userId, "bonus", pageable);
        verify(transactionRepository).searchByUserIdAndKeyword(userId, "bonus", pageable);
    }

    @Test
    void getTotalAmountByUserIdAndTxnType_shouldReturnTotal() {
        when(transactionRepository.sumAmountByUserIdAndTxnType(userId, DEPOSIT)).thenReturn(BigDecimal.valueOf(123.45));

        BigDecimal total = service.getTotalAmountByUserIdAndTxnType(userId, DEPOSIT);

        assertEquals(new BigDecimal("123.45"), total);
    }

    @Test
    void findTop10ByUserIdOrderByTxnDateDesc_shouldDelegateCall() {
        service.findTop10ByUserIdOrderByTxnDateDesc(userId);
        verify(transactionRepository).findTop10ByUserIdOrderByCreatedAtDesc(userId);
    }

    @Test
    void findSuspiciousTransactions_shouldDelegateCall() {
        service.findSuspiciousTransactions(BigDecimal.valueOf(1000));
        verify(transactionRepository).findByAmountGreaterThanEqual(BigDecimal.valueOf(1000));
    }
}*/
