package com.trustai.income_service.income.entity;

import com.trustai.income_service.income.repository.TeamIncomeConfigRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TeamIncomeConfigDataInitializer {
    private final TeamIncomeConfigRepository teamIncomeConfigRepository;


    /*@PostConstruct
    public void init() {
        TeamRebateConfig level2 = new TeamRebateConfig();
        level2.setRank(Rank.RANK_2);
        level2.setIncomePercentages(Map.of(
                1, new BigDecimal("12"),  // Lv.A
                2, new BigDecimal("5"),   // Lv.B
                3, new BigDecimal("2")    // Lv.C
        ));

        TeamRebateConfig level3 = new TeamRebateConfig();
        level3.setRank(Rank.RANK_3);
        level3.setIncomePercentages(Map.of(
                1, new BigDecimal("13"),
                2, new BigDecimal("6"),
                3, new BigDecimal("3")
        ));

        TeamRebateConfig level4 = new TeamRebateConfig();
        level4.setRank(Rank.RANK_4);
        level4.setIncomePercentages(Map.of(
                1, new BigDecimal("15"),
                2, new BigDecimal("7"),
                3, new BigDecimal("3")
        ));

        TeamRebateConfig level5 = new TeamRebateConfig();
        level5.setRank(Rank.RANK_5);
        level5.setIncomePercentages(Map.of(
                1, new BigDecimal("16"),
                2, new BigDecimal("8"),
                3, new BigDecimal("7")
        ));*/

    /*@PostConstruct
    public void init() {
        TeamIncomeConfig leve1 = new TeamIncomeConfig();
        leve1.setRankCode("RANK_1");
        leve1.setIncomePercentages(Map.of(
                1, new BigDecimal("0"),  // Lv.A ==> 5%
                2, new BigDecimal("0"),      // Lv.B ==> 2%
                3, new BigDecimal("0")       // Lv.C
        ));

        TeamIncomeConfig level2 = new TeamIncomeConfig();
        level2.setRankCode("RANK_2");
        level2.setIncomePercentages(Map.of(
                1, new BigDecimal("5"),  // Lv.A ==> 5%
                2, new BigDecimal("4"),      // Lv.B ==> 4%
                3, new BigDecimal("1")       // Lv.C ==>1
        ));

        TeamIncomeConfig level3 = new TeamIncomeConfig();
        level3.setRankCode("RANK_3");
        level3.setIncomePercentages(Map.of(
                1, new BigDecimal("6"), // 6%
                2, new BigDecimal("3"),     // 3%
                3, new BigDecimal("2")
        ));

        TeamIncomeConfig level4 = new TeamIncomeConfig();
        level4.setRankCode("RANK_4");
        level4.setIncomePercentages(Map.of(
                1, new BigDecimal("0"),
                2, new BigDecimal("0"),
                3, new BigDecimal("0")
        ));

        TeamIncomeConfig level5 = new TeamIncomeConfig();
        level5.setRankCode("RANK_5");
        level5.setIncomePercentages(Map.of(
                1, new BigDecimal("0"),
                2, new BigDecimal("0"),
                3, new BigDecimal("0")
        ));

        teamIncomeConfigRepository.saveAll(List.of(level2, level3, level4, level5));
    }*/

    @PostConstruct
    public void init() {
        List<TeamIncomeConfig> configs = List.of(
                // ===== RANK_1 =====
                createRank("RANK_1", 1, new BigDecimal("0")), // Lv.A ==> 0%
                createRank("RANK_1", 2, new BigDecimal("0")), // Lv.B ==> 0%
                createRank("RANK_1", 3, new BigDecimal("0")), // Lv.C ==> 0%

                // ===== RANK_2 =====
                createRank("RANK_2", 1, new BigDecimal("5")), // Lv.A ==> 5%
                createRank("RANK_2", 2, new BigDecimal("4")), // Lv.B ==> 4%
                createRank("RANK_2", 3, new BigDecimal("1")), // Lv.C ==> 1%

                // ===== RANK_3 =====
                createRank("RANK_3", 1, new BigDecimal("6")), // Lv.A ==> 6%
                createRank("RANK_3", 2, new BigDecimal("3")), // Lv.B ==> 3%
                createRank("RANK_3", 3, new BigDecimal("2")), // Lv.C ==> 2%

                // ===== RANK_4 =====
                createRank("RANK_4", 1, new BigDecimal("0")), // Lv.A ==> 0%
                createRank("RANK_4", 2, new BigDecimal("0")), // Lv.B ==> 0%
                createRank("RANK_4", 3, new BigDecimal("0")), // Lv.C ==> 0%

                // ===== RANK_5 =====
                createRank("RANK_5", 1, new BigDecimal("0")), // Lv.A ==> 0%
                createRank("RANK_5", 2, new BigDecimal("0")), // Lv.B ==> 0%
                createRank("RANK_5", 3, new BigDecimal("0"))  // Lv.C ==> 0%

                // ===== RANK_6 =====
//                createRank("RANK_6", 1, new BigDecimal("0")), // Lv.A ==> 0%
//                createRank("RANK_6", 2, new BigDecimal("0")), // Lv.B ==> 0%
//                createRank("RANK_6", 3, new BigDecimal("0")), // Lv.C ==> 0%

                // ===== RANK_7 =====
//                createRank("RANK_7", 1, new BigDecimal("0")), // Lv.A ==> 0%
//                createRank("RANK_7", 2, new BigDecimal("0")), // Lv.B ==> 0%
//                createRank("RANK_7", 3, new BigDecimal("0"))  // Lv.C ==> 0%
        );

        teamIncomeConfigRepository.saveAll(configs);
    }

    private TeamIncomeConfig createRank(String rank, int downlineDepth, BigDecimal payoutPercentage) {
        return new TeamIncomeConfig(new TeamIncomeKey(rank, downlineDepth), payoutPercentage);
    }
}
