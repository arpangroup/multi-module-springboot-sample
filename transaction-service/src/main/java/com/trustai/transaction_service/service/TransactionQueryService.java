package com.trustai.transaction_service.service;

import com.trustai.common.enums.PaymentGateway;
import com.trustai.common.enums.TransactionType;
import com.trustai.transaction_service.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionQueryService {
    // ------------------------------------------------------------------------
    // 1. Basic Transaction Queries
    // ------------------------------------------------------------------------
    Page<Transaction> getTransactions(Transaction.TransactionStatus status, Integer page, Integer size);
    Page<Transaction> getProfits(Integer page, Integer size);
    Page<Transaction> getTransactionsByUserId(Long userId, Integer page, Integer size);
    Boolean hasDepositTransaction(Long userId);

    // ------------------------------------------------------------------------
    // 2. Time-based and Range Filters
    // ------------------------------------------------------------------------
    Page<Transaction> getTransactionsByUserIdAndDateRange(Long userId, LocalDateTime start, LocalDateTime end, Pageable pageable);


    // ------------------------------------------------------------------------
    // 3. Lookup by Unique Reference ID
    // ------------------------------------------------------------------------
    Transaction findByTxnRefId(String txnRefId);


    // ------------------------------------------------------------------------
    // 4. Transfer-Specific Queries: Search by Sender & Receiver (for transfers)
    // ------------------------------------------------------------------------
    Page<Transaction> findTransfersBySenderId(Long senderId, Pageable pageable);
    Page<Transaction> findTransfersByReceiverId(Long receiverId, Pageable pageable);

    // ------------------------------------------------------------------------
    // 5. Filtering by Combined Criteria
    // ------------------------------------------------------------------------
    Page<Transaction> findByUserIdAndStatusAndGateway(Long userId, Transaction.TransactionStatus status, PaymentGateway gateway, Pageable pageable);
    Page<Transaction> searchTransactions(Long userId,
                                         Transaction.TransactionStatus status,
                                         TransactionType type,
                                         PaymentGateway gateway,
                                         LocalDateTime fromDate,
                                         LocalDateTime toDate,
                                         Pageable pageable);


    // ------------------------------------------------------------------------
    // 6. Free Text Search (Meta Info / Remarks) : Admin Use: Search by Meta Info (like investmentType, bonusReason)
    // ------------------------------------------------------------------------
    Page<Transaction> searchByMetaInfo(String keyword, Pageable pageable); // e.g., LIKE '%staking%'
    Page<Transaction> searchByKeyword(Long userId, String keyword, Pageable pageable);

    // ------------------------------------------------------------------------
    // 7. Aggregation and Reporting
    // ------------------------------------------------------------------------
    BigDecimal getTotalAmountByUserIdAndTxnType(Long userId, TransactionType txnType);


    // ------------------------------------------------------------------------
    // 8. Recent Activity : Recent N Transactions
    // ------------------------------------------------------------------------
    List<Transaction> findTop10ByUserIdOrderByTxnDateDesc(Long userId);


    // ------------------------------------------------------------------------
    // 9. Security / Anomaly Detection
    // ------------------------------------------------------------------------
    List<Transaction> findSuspiciousTransactions(BigDecimal threshold); // unusually large txns

}
