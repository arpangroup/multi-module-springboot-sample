package com.trustai.rank_service.entity;

import com.trustai.rank_service.repository.RankConfigRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


@Component
@RequiredArgsConstructor
public class RankDataInitializer {
    private final RankConfigRepository rankConfigRepository;

    @PostConstruct
    public void init() {
        List<RankConfig> configs = getAllRanks();
        rankConfigRepository.saveAll(configs);
    }

    public static List<RankConfig> getAllRanks() {
        // RANK_0:  MinDeposit: 15; No level requirement; No Txn possible;
        RankConfig rank0 = createRank("RANK_0", 0,
                "TrustAI Member",
                new BigDecimal("15"), // minDepositAmount
                new BigDecimal("100"), // minInvestmentAmount
                new BigDecimal("200"), // maxInvestmentAmount
                new BigDecimal("0"), // commissionPercentage
                10, // rankBonus
                Map.of(1, 0, 2, 0, 3, 0) // requiredLevelCounts
        );

        // RANK_1: MinDeposit: 40; No level requirement; Only 1 txnPerDay; Commission: 1%;
        RankConfig rank1 = createRank("RANK_1", 1,
                "TrustAI Leader",
                new BigDecimal("40"),  // minDepositAmount
                new BigDecimal("200"),   // minInvestmentAmount
                new BigDecimal("300"), // maxInvestmentAmount
                new BigDecimal("1.0"), // commissionPercentage
                20, // rankBonus
                Map.of(1, 0, 2, 0, 3, 0) // requiredLevelCounts
        );

        // RANK_2: MinDeposit: 300; Commission: 1.70%; {LvA : 4, LvB : 5, LvC : 1}
        RankConfig rank2 = createRank("RANK_2", 2,
                "TrustAI Captain",
                new BigDecimal("300"), // minDepositAmount
                new BigDecimal("300"), // minInvestmentAmount
                new BigDecimal("400"), // maxInvestmentAmount
                new BigDecimal("1.7"), // commissionPercentage
                20, // rankBonus
                Map.of(1, 4, 2, 5, 3, 1) // requiredLevelCounts
        );

        // RANK_3: MinDeposit: 600; Commission: 2.30% ; {LvA : 6, LvB : 25, LvC : 5}
        RankConfig rank3 = createRank("RANK_3", 3,
                "TrustAI Victor",
                new BigDecimal("600"), // minDepositAmount
                new BigDecimal("400"), // minInvestmentAmount
                new BigDecimal("500"), // maxInvestmentAmount
                new BigDecimal("2.30"), // commissionPercentage
                20, // rankBonus
                Map.of(1, 6, 2, 25, 3, 5) // requiredLevelCounts
        );

        // RANK_4: MinDeposit: 1500; Commission: 2.80%; {LvA : 12, LvB : 35, LvC : 10}
        RankConfig rank4 = createRank("RANK_4", 4,
                "TrustAI Champion",
                new BigDecimal("1500"), // minDepositAmount
                new BigDecimal("300"), // minInvestmentAmount
                new BigDecimal("400"), // maxInvestmentAmount
                new BigDecimal("2.80"), // commissionPercentage
                20, // rankBonus
                Map.of(1, 12, 2, 35, 3, 10) // requiredLevelCounts
        );

        // RANK_5: MinDeposit: 3000; Commission: 3.30%; {LvA : 16, LvB : 70, LvC : 20}
        RankConfig rank5 = createRank("RANK_5", 5,
                "TrustAI Silver",
                new BigDecimal("3000"), // minDepositAmount
                new BigDecimal("400"), // minInvestmentAmount
                new BigDecimal("500"), // maxInvestmentAmount
                new BigDecimal("3.30"), // commissionPercentage
                20, // rankBonus
                Map.of(1, 16, 2, 70, 3, 20) // requiredLevelCounts
        );

        // RANK_6: MinDeposit: 6000; Commission: 3.80%; {LvA : 20, LvB : 160, LvC : 40}
        RankConfig rank6 = createRank("RANK_6", 6,
                "TrustAI Gold",
                new BigDecimal("6000"), // minDepositAmount
                new BigDecimal("500"), // minInvestmentAmount
                new BigDecimal("600"), // maxInvestmentAmount
                new BigDecimal("3.80"), // commissionPercentage
                20, // rankBonus
                Map.of(1, 20, 2, 160, 3, 40) // requiredLevelCounts
        );

        // RANK_7: MinDeposit: 15000; Commission: 3.80%; {LvA : 35, LvB : 350, LvC : 50}
        RankConfig rank7 = createRank("RANK_7", 7,
                "TrustAI Platinum",
                new BigDecimal("15000"), // minDepositAmount
                new BigDecimal("500"), // minInvestmentAmount
                new BigDecimal("600"), // maxInvestmentAmount
                new BigDecimal("4.5"), // commissionPercentage
                20, // rankBonus
                Map.of(1, 35, 2, 350, 3, 50) // requiredLevelCounts
        );

        return List.of(rank0, rank1, rank2, rank3, rank4, rank5, rank6, rank7);
    }

    public static RankConfig createRank(
            String code,
            int order,
            String displayName,
            BigDecimal minDeposit,
            BigDecimal minInvestmentAmount,
            BigDecimal maxInvestmentAmount,
            BigDecimal commissionPercentage,
            int rankBonus, Map<Integer, Integer> levelCounts
    ) {
        RankConfig config = new RankConfig();
        config.setCode(code);
        config.setDisplayName(displayName);
        config.setRankOrder(order);
        config.setActive(true);
        config.setMinDepositAmount(minDeposit);
        config.setMinInvestmentAmount(minInvestmentAmount);
        config.setMaxInvestmentAmount(maxInvestmentAmount);
        config.setCommissionPercentage(commissionPercentage);
        config.setRankBonus(rankBonus);
        config.setRequiredLevelCounts(levelCounts);
        config.setMinDirectReferrals(levelCounts.get(1));
        config.setTxnPerDay(order == 0 ? 0 : 1);
        config.setActive(true);
        return config;
    }

    /*@PostConstruct
    public void init() {
        // Rank 0:  MinDeposit: 15; No level requirement; No Txn possible;
        RankConfig rank0 = new RankConfig(0, "RANK_0", "TrustAI Member", 15, 15, 0.0f);
        rank0.setTxnPerDay(0);
        rank0.getRequiredLevelCounts().put(1, 0); // Level A
        rank0.getRequiredLevelCounts().put(2, 0); // Level B
        rank0.getRequiredLevelCounts().put(3, 0); // Level C
        rank0.setActive(true);

        // Rank 1: MinDeposit: 40; No level requirement; Only 1 txnPerDay; 1% Commission;
        RankConfig rank1 = new RankConfig(1, "RANK_1", "TrustAI Leader", 40, 0, 1.0f); //1%
        rank1.setTxnPerDay(1);
        rank1.getRequiredLevelCounts().put(1, 0); // Level A
        rank1.getRequiredLevelCounts().put(2, 0); // Level B
        rank1.getRequiredLevelCounts().put(3, 0); // Level C
        rank1.setActive(true);

        // Rank 2: Level A = 4, Level B = 5, Level C = 1
        RankConfig rank2 = new RankConfig(2, "RANK_2", "TrustAI Captain", 300, 100, 1.7f);//10%
        rank2.setTxnPerDay(1);
        rank2.getRequiredLevelCounts().put(1, 4); // Level A
        rank2.getRequiredLevelCounts().put(2, 5); // Level B
        rank2.getRequiredLevelCounts().put(3, 1); // Level C
        rank2.setActive(true);

        // Rank 3: Level A = 6, Level B = 25, Level C = 5
        RankConfig rank3 = new RankConfig(3, "RANK_3", "trustAI Victor", 600, 200, 2.30f);//15%
        rank3.setTxnPerDay(1);
        rank3.getRequiredLevelCounts().put(1, 6);
        rank3.getRequiredLevelCounts().put(2, 25);
        rank3.getRequiredLevelCounts().put(3, 5);
        rank3.setActive(true);

        // Rank 4: Level A = 12, Level B = 35, Level C = 10
        RankConfig rank4 = new RankConfig(4, "RANK_4", "trustAI Champion", 1500, 300, 2.80f);
        rank4.setTxnPerDay(1);
        rank4.getRequiredLevelCounts().put(1, 12);
        rank4.getRequiredLevelCounts().put(2, 35);
        rank4.getRequiredLevelCounts().put(3, 10);
        rank4.setActive(true);


        // Rank 5: Level A = 16, Level B = 70, Level C = 20
        RankConfig rank5 = new RankConfig(5, "RANK_5", "trustAI Silver", 3000, 400, 3.30f);
        rank5.setTxnPerDay(1);
        rank5.getRequiredLevelCounts().put(1, 16);
        rank5.getRequiredLevelCounts().put(2, 70);
        rank5.getRequiredLevelCounts().put(3, 20);
        rank5.setActive(true);


        // Rank 6: Level A = 20, Level B = 160, Level C = 40
        RankConfig rank6 = new RankConfig(6, "RANK_6", "trustAI Gold", 6000, 500, 3.80f);
        rank6.setTxnPerDay(1);
        rank6.getRequiredLevelCounts().put(1, 20);
        rank6.getRequiredLevelCounts().put(2, 160);
        rank6.getRequiredLevelCounts().put(3, 40);
        rank6.setActive(true);


        // Rank 7: Level A = 35, Level B = 350, Level C = 50
        RankConfig rank7 = new RankConfig(7, "RANK_7", "trustAI Platinum", 15000, 500, 4.5f);
        rank7.setTxnPerDay(1);
        rank7.getRequiredLevelCounts().put(1, 35);
        rank7.getRequiredLevelCounts().put(2, 350);
        rank7.getRequiredLevelCounts().put(3, 50);
        rank7.setActive(true);

        rankConfigRepository.saveAll(List.of(rank0, rank1, rank2, rank3, rank4, rank5, rank6, rank7));
    }*/

}
