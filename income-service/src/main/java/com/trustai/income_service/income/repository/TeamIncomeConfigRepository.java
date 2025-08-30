package com.trustai.income_service.income.repository;

import com.trustai.income_service.income.entity.TeamIncomeConfig;
import com.trustai.income_service.income.entity.TeamIncomeKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamIncomeConfigRepository extends JpaRepository<TeamIncomeConfig, TeamIncomeKey> {
    //Optional<TeamIncomeConfig> findByRankCode(String rankCode);
    Optional<TeamIncomeConfig> findById_UplineRankAndId_DownlineDepth(String uplineRank, int downlineDepth);
}
