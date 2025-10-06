package com.trustai.transaction_service.repository;

import com.trustai.transaction_service.entity.PendingWithdraw;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PendingWithdrawRepository extends JpaRepository<PendingWithdraw, Long> {
    Page<PendingWithdraw> findByUserId(Long userId, Pageable pageable);

    boolean existsByUserIdAndStatus(long userId, PendingWithdraw.WithdrawStatus withdrawStatus);

    Page<PendingWithdraw> findByUserIdAndStatus(Long userId, PendingWithdraw.WithdrawStatus status, Pageable pageable);

    Page<PendingWithdraw> findByStatus(PendingWithdraw.WithdrawStatus status, Pageable pageable);

    int countByUserIdAndRankCodeAndIsProfitWalletAndStatus(Long userId, String rankCode, boolean isProfitWallet, PendingWithdraw.WithdrawStatus status);

    int countByUserIdAndRankCodeAndStatusAndCreatedAtBetween(
            long userId,
            String rankCode,
            PendingWithdraw.WithdrawStatus withdrawStatus,
            LocalDateTime from,
            LocalDateTime to
    );
}
