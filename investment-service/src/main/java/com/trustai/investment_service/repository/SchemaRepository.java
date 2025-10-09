package com.trustai.investment_service.repository;

import com.trustai.investment_service.entity.InvestmentSchema;
import com.trustai.investment_service.enums.InvestmentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SchemaRepository extends JpaRepository<InvestmentSchema, Long> {
    boolean existsByName(String name);
    List<InvestmentSchema> findByLinkedRank(String linkedRank);
    Page<InvestmentSchema> findByLinkedRank(String linkedRank, Pageable pageable);
    Page<InvestmentSchema> findByLinkedRankAndInvestmentType(String rankCode, InvestmentType investmentSubType, Pageable pageable);
    Page<InvestmentSchema> findByInvestmentType(InvestmentType investmentType, Pageable pageable);

    List<InvestmentSchema> findByIsActiveTrueAndInvestmentType(InvestmentType investmentSubType);

    //List<InvestmentSchema> findByLinkedRankAndIsActiveTrue(String linkedRank);

    Optional<InvestmentSchema> findTopByInvestmentTypeAndIsActiveTrueOrderByMinimumInvestmentAmountDesc(InvestmentType investmentType);

    @Query(value = "SELECT * FROM investment_schemas WHERE is_active = true ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<InvestmentSchema> findRandomActiveSchema();
}
