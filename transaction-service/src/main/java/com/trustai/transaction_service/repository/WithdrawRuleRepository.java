package com.trustai.transaction_service.repository;

import com.trustai.transaction_service.entity.WithdrawRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WithdrawRuleRepository extends JpaRepository<WithdrawRule, Long> {
    Optional<WithdrawRule> findByRankCode(String rankCode);
    boolean existsByRankCode(String rankCode);
}