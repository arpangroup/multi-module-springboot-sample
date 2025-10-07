package com.trustai.transaction_service.repository;

import com.trustai.transaction_service.entity.PendingDeposit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PendingDepositRepository extends JpaRepository<PendingDeposit, Long> {
    Page<PendingDeposit> findByStatus(PendingDeposit.DepositStatus status, Pageable pageable);
    boolean existsByLinkedTxnId(String linkedTxnId);
    boolean existsByLinkedTxnIdAndStatus(String linkedTxnId, PendingDeposit.DepositStatus status);
    long countByUserIdAndStatus(Long userId, PendingDeposit.DepositStatus status);

    // Ignore soft-deleted entries
    boolean existsByLinkedTxnIdAndStatusAndIsDeletedFalse(String linkedTxnId, PendingDeposit.DepositStatus status);


    @Query("""
        SELECT p FROM PendingDeposit p
        WHERE p.isDeleted = false
        AND (:userId IS NULL OR p.userId = :userId)
        AND (:status IS NULL OR p.status = :status)
        """)
    Page<PendingDeposit> findByUserIdAndStatus(
            @Param("userId") Long userId,
            @Param("status") PendingDeposit.DepositStatus status,
            Pageable pageable
    );
}
