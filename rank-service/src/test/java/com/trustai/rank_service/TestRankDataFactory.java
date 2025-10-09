/*
package com.trustai.rank_service;


import com.trustai.rank_service.entity.RankConfig;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class TestRankDataFactory {

    public static List<RankConfig> getAllRanks() {
        // RANK_0:  MinDeposit: 15; No level requirement; No Txn possible;
        RankConfig rank0 = createRank("RANK_0", 0,
                "TrustAI Member",
                new BigDecimal("15"), // minDepositAmount
                new BigDecimal("15"), // minInvestmentAmount
                new BigDecimal("0"), // commissionPercentage
                10, // rankBonus
                Map.of(1, 0, 2, 0, 3, 0) // requiredLevelCounts
        );

        // RANK_1: MinDeposit: 40; No level requirement; Only 1 txnPerDay; Commission: 1%;
        RankConfig rank1 = createRank("RANK_1", 1,
                "TrustAI Leader",
                new BigDecimal("40"),  // minDepositAmount
                new BigDecimal("0"),   // minInvestmentAmount
                new BigDecimal("1.0"), // commissionPercentage
                20, // rankBonus
                Map.of(1, 0, 2, 0, 3, 0) // requiredLevelCounts
        );

        // RANK_2: MinDeposit: 300; Commission: 1.70%; {LvA : 4, LvB : 5, LvC : 1}
        RankConfig rank2 = createRank("RANK_2", 2,
                "TrustAI Captain",
                new BigDecimal("300"), // minDepositAmount
                new BigDecimal("100"), // minInvestmentAmount
                new BigDecimal("1.7"), // commissionPercentage
                20, // rankBonus
                Map.of(1, 4, 2, 5, 3, 1) // requiredLevelCounts
        );

        // RANK_3: MinDeposit: 600; Commission: 2.30% ; {LvA : 6, LvB : 25, LvC : 5}
        RankConfig rank3 = createRank("RANK_3", 3,
                "TrustAI Victor",
                new BigDecimal("600"), // minDepositAmount
                new BigDecimal("200"), // minInvestmentAmount
                new BigDecimal("2.30"), // commissionPercentage
                20, // rankBonus
                Map.of(1, 6, 2, 25, 3, 5) // requiredLevelCounts
        );

        // RANK_4: MinDeposit: 1500; Commission: 2.80%; {LvA : 12, LvB : 35, LvC : 10}
        RankConfig rank4 = createRank("RANK_4", 4,
                "TrustAI Champion",
                new BigDecimal("1500"), // minDepositAmount
                new BigDecimal("300"), // minInvestmentAmount
                new BigDecimal("2.80"), // commissionPercentage
                20, // rankBonus
                Map.of(1, 12, 2, 35, 3, 10) // requiredLevelCounts
        );

        // RANK_5: MinDeposit: 3000; Commission: 3.30%; {LvA : 16, LvB : 70, LvC : 20}
        RankConfig rank5 = createRank("RANK_5", 5,
                "TrustAI Silver",
                new BigDecimal("3000"), // minDepositAmount
                new BigDecimal("400"), // minInvestmentAmount
                new BigDecimal("3.30"), // commissionPercentage
                20, // rankBonus
                Map.of(1, 16, 2, 70, 3, 20) // requiredLevelCounts
        );

        // RANK_6: MinDeposit: 6000; Commission: 3.80%; {LvA : 20, LvB : 160, LvC : 40}
        RankConfig rank6 = createRank("RANK_6", 6,
                "TrustAI Gold",
                new BigDecimal("6000"), // minDepositAmount
                new BigDecimal("500"), // minInvestmentAmount
                new BigDecimal("3.80"), // commissionPercentage
                20, // rankBonus
                Map.of(1, 20, 2, 160, 3, 40) // requiredLevelCounts
        );

        // RANK_7: MinDeposit: 15000; Commission: 3.80%; {LvA : 35, LvB : 350, LvC : 50}
        RankConfig rank7 = createRank("RANK_7", 7,
                "TrustAI Platinum",
                new BigDecimal("15000"), // minDepositAmount
                new BigDecimal("500"), // minInvestmentAmount
                new BigDecimal("4.5"), // commissionPercentage
                20, // rankBonus
                Map.of(1, 35, 2, 350, 3, 50) // requiredLevelCounts
        );

        return List.of(rank0, rank1, rank2, rank3, rank4, rank5, rank6, rank7);
    }


    public static RankConfig createRank(String code, int order, String displayName, BigDecimal minDeposit, BigDecimal minInvestmentAmount, BigDecimal commissionPercentage, int rankBonus, Map<Integer, Integer> levelCounts) {
        RankConfig config = new RankConfig();
        config.setCode(code);
        config.setDisplayName(displayName);
        config.setRankOrder(order);
        config.setActive(true);
        config.setMinDepositAmount(minDeposit);
        config.setMinInvestmentAmount(minInvestmentAmount);
        config.setCommissionPercentage(commissionPercentage);
        config.setRankBonus(rankBonus);
        config.setRequiredLevelCounts(levelCounts);
        config.setMinDirectReferrals(levelCounts.get(1));
        config.setTxnPerDay(order == 0 ? 0 : 1);
        return config;
    }
}
*/
