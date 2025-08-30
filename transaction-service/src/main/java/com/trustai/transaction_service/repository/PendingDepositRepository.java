package com.trustai.transaction_service.repository;

import com.trustai.transaction_service.entity.PendingDeposit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PendingDepositRepository extends JpaRepository<PendingDeposit, Long> {
    Page<PendingDeposit> findByStatus(PendingDeposit.DepositStatus status, Pageable pageable);
}
