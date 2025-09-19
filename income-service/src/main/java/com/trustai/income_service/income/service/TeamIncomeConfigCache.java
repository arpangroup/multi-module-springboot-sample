package com.trustai.income_service.income.service;

import com.trustai.common.lifecycle.Reloadable;
import com.trustai.income_service.income.entity.TeamIncomeConfig;
import com.trustai.income_service.income.entity.TeamIncomeKey;
import com.trustai.income_service.income.repository.TeamIncomeConfigRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamIncomeConfigCache implements Reloadable {
    private final TeamIncomeConfigRepository repository;
    private Map<TeamIncomeKey, BigDecimal> payoutCache;

    @Override
    public void reload() {
        preload();
    }

    @PostConstruct
    public void preload() {
        /*payoutCache = repository.findAll().stream()
                .collect(Collectors.toMap(
                        TeamIncomeConfig::getId,
                        cfg -> cfg.getPayoutPercentage()
                                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                ));*/
        payoutCache = repository.findAll().stream()
                .collect(Collectors.toMap(
                        TeamIncomeConfig::getId,
                        TeamIncomeConfig::getPayoutPercentage // No division here
                ));
        log.info("✅ Loaded {} TeamIncomeConfig entries into cache", payoutCache.size());
    }

    public BigDecimal getPercentage(String uplineRank, int downlineDepth) {
        TeamIncomeKey key = new TeamIncomeKey(uplineRank, downlineDepth);
        BigDecimal rate = payoutCache.getOrDefault(key, BigDecimal.ZERO);
        log.debug("Cache lookup for rank={}, depth={} => {}", uplineRank, downlineDepth, rate);
        return rate;
    }


    /*public BigDecimal getTeamCommissionPercentage(String rank, int depth) {
        log.info("getTeamCommissionPercentage for Rank: {}, depth: {}", rank,depth);

        // Replace with DB or config-based logic
        if (rankCode == Rank.RANK_2 && depth == 1) return BigDecimal.valueOf(0.12); // 12% Lv.A
        //if (rankCode == Rank.RANK_2 && depth == 2) return BigDecimal.valueOf(0.05); // 5% Lv.B
        //if (rankCode == Rank.RANK_2 && depth == 3) return BigDecimal.valueOf(0.02); // 2% Lv.C
        //return BigDecimal.ZERO;


        TeamIncomeConfig config = teamIncomeConfigRepository.findById(rank)
                .orElseThrow(() -> new IllegalStateException("No team config for rankCode: " + rank));

        BigDecimal teamIncomePercentage = config.getIncomePercentages().getOrDefault(depth, BigDecimal.ZERO);
        BigDecimal teamIncomeRate = teamIncomePercentage.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        log.info("Team IncomePercentage: {}% ===> Team IncomeRate: {} for Rank: {}, Depth: {}", teamIncomePercentage, teamIncomeRate, rank, depth);
        return teamIncomeRate;
    }*/

    /*public BigDecimal getPercentage(String uplineRank, int downlineDepth) {
        log.debug("Fetching team income percentage for UplineRank: {}, DownlineDepth: {}", uplineRank, downlineDepth);
//        return repository.findById_UplineRankAndId_DownlineDepth(uplineRank, downlineDepth)
//                .map(cfg -> cfg.getPayoutPercentage().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP))
//                .orElse(BigDecimal.ZERO);

        TeamIncomeConfig config = repository
                .findByIdUplineRankAndIdDownlineDepth(uplineRank, downlineDepth)
                .orElseThrow(() -> {
                    log.warn("Missing TeamIncomeConfig for UplineRank: {}, DownlineDepth: {}", uplineRank, downlineDepth);
                    return new IllegalStateException(
                            String.format("No team config found for UplineRank: %s and DownlineDepth: %d", uplineRank, downlineDepth)
                    );
                });


        BigDecimal payoutPercentage = config.getPayoutPercentage();
        BigDecimal payoutRate = payoutPercentage.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        log.info(
                "Calculated Team Income Payout - UplineRank: {}, DownlineDepth: {}, PayoutPercentage: {}%, PayoutRate: {}",
                uplineRank, downlineDepth, payoutPercentage, payoutRate
        );

        return payoutRate;
    }*/
}
