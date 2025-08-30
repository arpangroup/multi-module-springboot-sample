package com.trustai.transaction_service.repository;

import com.trustai.common.enums.PaymentGateway;
import com.trustai.common.enums.TransactionType;
import com.trustai.transaction_service.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction>
{
    Page<Transaction> findByTxnType(TransactionType txnType, Pageable pageable);
    Page<Transaction> findByTxnTypeIn(List<TransactionType> txnTypes, Pageable pageable);
    Page<Transaction> findByTxnTypeAndStatus(TransactionType txnType, Transaction.TransactionStatus status, Pageable pageable);

    // 1. Basic filters
    Page<Transaction> findByStatus(Transaction.TransactionStatus status, Pageable pageable);


    Page<Transaction> findByUserId(Long userId, Pageable pageable);
    Page<Transaction> findByUserIdAndTxnType(Long userId, TransactionType transactionType, Pageable pageable);

    boolean existsByUserIdAndTxnType(Long userId, TransactionType txnType);

    // 2. Date range filter
    Page<Transaction> findByUserIdAndCreatedAtBetween(Long userId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    // 3. Reference ID lookup
    Optional<Transaction> findByTxnRefId(String txnRefId);

    // 4. Transfer filters
    Page<Transaction> findBySenderId(Long senderId, Pageable pageable);

    // 5. Composite filter
    Page<Transaction> findByUserIdAndStatusAndGateway(Long userId, Transaction.TransactionStatus status, PaymentGateway gateway, Pageable pageable);

    // 6. Meta info search
    Page<Transaction> findByMetaInfoContainingIgnoreCase(String keyword, Pageable pageable);

    // 7. Keyword search (remarks + metaInfo)
    @Query("SELECT t FROM Transaction t " +
            "WHERE t.userId = :userId AND " +
            "(LOWER(t.remarks) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(t.metaInfo) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Transaction> searchByUserIdAndKeyword(@Param("userId") Long userId, @Param("keyword") String keyword, Pageable pageable);

    // 8. Aggregation
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.userId = :userId AND t.txnType = :txnType")
    BigDecimal sumAmountByUserIdAndTxnType(@Param("userId") Long userId, @Param("txnType") TransactionType txnType);

    @Query("""
    SELECT SUM(t.amount)
    FROM Transaction t
    WHERE t.userId = :userId
      AND t.txnType IN :txnTypes
      AND t.status IN :statuses
    """)
    BigDecimal sumAmountByUserIdAndTxnTypeAndStatusIn(
            @Param("userId") Long userId,
            @Param("txnTypes") List<TransactionType> txnTypes,
            @Param("statuses") List<Transaction.TransactionStatus> statuses
    );

    // 9. Recent N transactions
    List<Transaction> findTop10ByUserIdOrderByCreatedAtDesc(Long userId);

    // 10. Suspicious transactions
    List<Transaction> findByAmountGreaterThanEqual(BigDecimal threshold);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.userId = :userId AND t.txnType IN (:creditTypes)")
    BigDecimal sumCredits(@Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.userId = :userId AND t.txnType IN (:debitTypes)")
    BigDecimal sumDebits(@Param("userId") Long userId);

}
