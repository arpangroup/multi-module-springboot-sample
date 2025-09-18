package com.trustai.income_service.income.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trustai.common.api.RankConfigApi;
import com.trustai.common.api.UserApi;
import com.trustai.common.api.WalletApi;
import com.trustai.common.dto.RankConfigDto;
import com.trustai.common.dto.UserHierarchyDto;
import com.trustai.common.dto.UserInfo;
import com.trustai.common.dto.WalletUpdateRequest;
import com.trustai.common.enums.IncomeType;
import com.trustai.common.enums.TransactionType;
import com.trustai.income_service.constant.Remarks;
import com.trustai.income_service.income.dto.UplineIncomeLog;
import com.trustai.income_service.income.entity.IncomeHistory;
import com.trustai.income_service.income.repository.IncomeHistoryRepository;
import com.trustai.income_service.income.repository.TeamIncomeConfigRepository;
import com.trustai.income_service.income.strategy.TeamIncomeStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class IncomeDistributionService {
    private final IncomeHistoryRepository incomeRepo;
    private final TeamIncomeConfigRepository teamIncomeRepo;
    private final TeamIncomeStrategy teamIncomeStrategy;
    private final ObjectMapper objectMapper;
    private final UserApi userApi;
    private final WalletApi walletApi;
    private final RankConfigApi rankConfigApi;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void distributeIncome(Long sellerId, BigDecimal saleAmount) {
        log.info("Starting income distribution: sellerId={}, saleAmount={}", sellerId, saleAmount);

        UserInfo seller = userApi.getUserById(sellerId);
        String sellerRank = seller.getRankCode();
        log.info("seller userId: {}, Rank: {}", sellerId, sellerRank);

        RankConfigDto config  = rankConfigApi.getRankConfigByRankCode(sellerRank);

        //BigDecimal profitRate = config.getCommissionRate().divide(BigDecimal.valueOf(100)vide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal profitRate = config.getCommissionPercentage()
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        log.info("Commission details: commissionPercentage={}%, profitRate={} for rank={}", config.getCommissionPercentage(), profitRate, sellerRank);

        if (profitRate.compareTo(BigDecimal.ZERO) == 0) {
            log.warn("Profit rate is zero for sellerId: {}, Rank: {}. No income will be distributed.", sellerId, sellerRank);
            return;
        }


        BigDecimal dailyIncome = saleAmount.multiply(profitRate).setScale(2, RoundingMode.HALF_UP);
        log.info("Calculated daily income: dailyIncome={} from saleAmount={} and profitRate={}", dailyIncome, saleAmount, profitRate);

        // 1. Save seller daily income
        //incomeRepo.save(new IncomeHistory(sellerId, dailyIncome, "DAILY", "SELF", LocalDate.now()));
        log.info("Saving direct income of {} for user: {}...........", dailyIncome, sellerId);
        IncomeHistory incomeHistory = IncomeHistory.builder()
                .userId(sellerId)
                .amount(dailyIncome)
                .incomeType(IncomeType.DAILY)
                .sourceUserId(sellerId)
                .sourceUserRank(sellerRank)
                .note(Remarks.DAILY_INCOME) // Self income
                .build();
        incomeRepo.save(incomeHistory);
        log.info("Saved direct income of {} for user {}", dailyIncome, seller.getId());

        // Update seller wallet
        log.info("Updating wallet balance for sellerId={} with DailyIncome={}", sellerId, dailyIncome);
        String metaInfo = getMetaInfo(incomeHistory);
        log.debug("Wallet update meta info: {}", metaInfo);

        WalletUpdateRequest depositRequest = new WalletUpdateRequest(
                dailyIncome,
                TransactionType.DAILY_INCOME,
                true,
                "daily-income",
                Remarks.DAILY_INCOME,
                metaInfo
        );
        walletApi.updateWalletBalance(sellerId, depositRequest);
        log.info("Wallet updated successfully for sellerId={}", sellerId);
        //userClient.deposit(sellerId, dailyIncome, Remarks.DAILY_INCOME, metaInfo);

        // 2. Fetch all uplines with rankCode info in a single query
        /*List<Object[]> uplineData = hierarchyRepo.findUplinesWithRank(sellerId, 3); // Depth ≤ 3

        for (Object[] row : uplineData) {
            Long uplineId = (Long) row[0];
            Integer depth = (Integer) row[1];
            Integer rankLevel = (Integer) row[2];

            Rank uplineRank = Rank.fromLevel(rankLevel);
            TeamIncomeConfig teamConfig = teamIncomeRepo.findById(uplineRank).orElse(null);
            if (teamConfig == null) continue;

            BigDecimal percentage = teamConfig.getIncomePercentages().getOrDefault(depth, BigDecimal.ZERO);
            BigDecimal teamIncome = dailyIncome.multiply(percentage).divide(BigDecimal.valueOf(100));

            if (teamIncome.compareTo(BigDecimal.ZERO) > 0) {
                //incomeRepo.save(new IncomeHistory(uplineId, teamIncome, "TEAM", "from: " + sellerId, LocalDate.now()));
                incomeRepo.save(IncomeHistory.builder()
                        .userId(uplineId)
                        .amount(teamIncome)
                        .type(IncomeType.TEAM)
                        .sourceUserId(sellerId)
                        .sourceUserRank(sellerRank)
                        .note("From user " + sellerId + ", depth " + depth)
                        .date(LocalDate.now())
                        .build());
            }
        }*/


        // 2. Load full hierarchy in one query
        // Propagate team income
        log.info("Fetching uplines for sellerId={} to propagate team income", sellerId);
        List<UserHierarchyDto> hierarchy = userApi.findByDescendant(sellerId);
        Map<Long, Integer> uplinesWithDepth = hierarchy.stream()
                .filter(UserHierarchyDto::isActive)
                .collect(Collectors.toMap(UserHierarchyDto::getAncestor, UserHierarchyDto::getDepth));
        log.info("Found {} active uplines for sellerId={}: {}", uplinesWithDepth.size(), sellerId, uplinesWithDepth.keySet());

        // Load all upline users in a single query
        List<UserInfo> uplines = userApi.getUsers(uplinesWithDepth.keySet().stream().toList());

        // Distribute team income
        log.info("Distribute team income for user: {}, uplinesCount={}.............", sellerId, uplines.size());
        List<UplineIncomeLog> logs = teamIncomeStrategy.distributeTeamIncome(sellerId, sellerRank, dailyIncome, uplines, uplinesWithDepth);
        printLog(logs, sellerId, saleAmount, sellerRank, config.getCommissionPercentage(), dailyIncome);
        log.info("Completed income distribution for sellerId={}", sellerId);
    }

    private String getMetaInfo(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }


    /*
    🧪 Example Output

    ====== INCOME DISTRIBUTION SUMMARY ======
    ▶️ Seller ID: 101, Rank: RANK_3, Daily Income: 30.0000
    ▶️ Upline Team Income:
      - Upline ID: 80  | Rank: RANK_5 | Level: 1 | %: 16.00 | Income: 4.8000
      - Upline ID: 55  | Rank: RANK_3 | Level: 2 | %: 6.00  | Income: 1.8000
    ▶️ Total Team Members Rewarded: 2
    ▶️ Total Team Income Distributed: 6.6000
    ========================================
     */

    private void printLog(List<UplineIncomeLog> logs, Long sellerId, BigDecimal sellAmount, String sellerRank, BigDecimal commissionPercentage, BigDecimal dailyIncome) {
        if (logs == null) {
            logs = List.of(); // prevent NPE
        }
        BigDecimal totalTeamIncome = logs.stream()
                .map(UplineIncomeLog::income)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        StringBuilder summary = new StringBuilder("\n============= INCOME DISTRIBUTION SUMMARY =============\n");

        summary.append("▶️ Seller ID: ").append(sellerId)
                .append(", Rank: ").append(sellerRank)
                .append(", Daily Income: ").append(dailyIncome)
                .append("\n");

        summary.append("▶️ Upline Team Income:\n");

        if (logs.isEmpty()) {
            summary.append("   No upline members qualified for team income.\n");
        } else {
            logs.forEach(log -> summary.append(String.format(
                    "  - Upline ID: %d | Rank: %-7s | Level: %d | %%: %5s | Income: %s\n",
                    log.uplineUserId(), log.rankCode(), log.depth(),
                    log.percentage().setScale(2, RoundingMode.HALF_UP), log.income().setScale(2, RoundingMode.HALF_UP)
            )));
            summary.append("▶️ Total Team Members Rewarded: ").append(logs.size()).append("\n");
            summary.append("▶️ Total Team Income Distributed: ").append(totalTeamIncome.setScale(2, RoundingMode.HALF_UP)).append("\n");
        }

        summary.append("==================================================\n");
        //System.out.println(summary);  // or log.info(summary.toString());

        // ✅ Use logger instead of System.out
        log.info(summary.toString());
    }
}
