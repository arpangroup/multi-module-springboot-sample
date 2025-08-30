package com.trustai.investment_service.repository;

import com.trustai.investment_service.entity.UserInvestment;
import com.trustai.investment_service.enums.InvestmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserInvestmentRepository extends JpaRepository<UserInvestment, Long> {
    List<UserInvestment> findByUserId(Long userId);
    Page<UserInvestment> findByUserId(Long userId, Pageable pageable);
    Page<UserInvestment> findByUserIdAndStatus(Long userId, InvestmentStatus status, Pageable pageable);
    Page<UserInvestment> findByStatus(InvestmentStatus status, Pageable pageable);

    @Query("""
    SELECT u FROM UserInvestment u
    WHERE u.status = 'ACTIVE'
      AND u.nextPayoutAt <= :now
    """)
    List<UserInvestment> findActiveInvestmentsDueForPayout(@Param("now") LocalDateTime now);


}
