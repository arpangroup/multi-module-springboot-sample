package com.trustai.investment_service.repository;

import com.trustai.investment_service.entity.InvestmentSchema;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SchemaRepository extends JpaRepository<InvestmentSchema, Long> {
    List<InvestmentSchema> findByLinkedRank(String linkedRank);
    Page<InvestmentSchema> findByLinkedRank(String linkedRank, Pageable pageable);
    Page<InvestmentSchema> findByLinkedRankAndInvestmentSubType(String rankCode, InvestmentSchema.InvestmentSubType investmentSubType, Pageable pageable);
    Page<InvestmentSchema> findByInvestmentSubType(InvestmentSchema.InvestmentSubType investmentSubType, Pageable pageable);

    List<InvestmentSchema> findByIsActiveTrueAndInvestmentSubType(InvestmentSchema.InvestmentSubType investmentSubType);

    //List<InvestmentSchema> findByLinkedRankAndIsActiveTrue(String linkedRank);

    Optional<InvestmentSchema> findTopByInvestmentSubTypeAndIsActiveTrueOrderByPriceDesc(InvestmentSchema.InvestmentSubType subType);
}
