package com.trustai.transaction_service.repository;

import com.trustai.transaction_service.entity.PendingWithdraw;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PendingWithdrawRepository extends JpaRepository<PendingWithdraw, Long> {
    Page<PendingWithdraw> findByUserId(Long userId, Pageable pageable);
}
