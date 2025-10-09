package com.trustai.income_service.income.strategy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trustai.common.api.UserApi;
import com.trustai.common.api.WalletApi;
import com.trustai.common.dto.UserInfo;
import com.trustai.common.dto.WalletUpdateRequest;
import com.trustai.common.enums.IncomeType;
import com.trustai.common.enums.TransactionType;
import com.trustai.income_service.constant.Remarks;
import com.trustai.income_service.income.dto.UplineIncomeLog;
import com.trustai.income_service.income.entity.IncomeHistory;
import com.trustai.income_service.income.repository.IncomeHistoryRepository;
import com.trustai.income_service.income.service.TeamIncomeConfigCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class DefaultTeamIncomeStrategy implements TeamIncomeStrategy {
    private final IncomeHistoryRepository incomeHistoryRepo;
    private final TeamIncomeConfigCache teamIncomeConfigCache;
    private final UserApi userApi;
    private final WalletApi walletApi;
    private final ObjectMapper objectMapper;

    @Override
    public List<UplineIncomeLog> distributeTeamIncome(Long sourceUserId, String sourceUserRank, BigDecimal baseIncome, List<UserInfo> uplines, Map<Long, Integer> uplineDepthMap) {
        log.info("..................................................");
        log.info("Inside distributeTeamIncome for sourceUserId: {}, sourceUserRank: {}, baseIncome: {}..................", sourceUserId, sourceUserRank, baseIncome);
        log.info("All uplines: {}", uplineDepthMap);
        List<UplineIncomeLog> incomeLogs = new ArrayList<>();

        for (UserInfo upline : uplines) {
            log.info("UPLINE UserID: {}, Rank: {} for Source UserID: {}", upline.getId(), upline.getRankCode(), sourceUserId);
            String uplineUserRank = upline.getRankCode();
            //RankConfig rankConfig = rankConfigRepository.findById(uplineUserRank).orElseThrow(() -> new IllegalStateException("Rank config not found: " + uplineUserRank));
            Integer depth = uplineDepthMap.get(upline.getId());
            if (depth == null) {
                log.warn("Skipping upline {} — depth not found", upline.getId());
                continue;
            }

            BigDecimal percentage = teamIncomeConfigCache.getPercentage(uplineUserRank, depth);
            log.info("TeamIncome Rate of {} for uplineUser: {} for rankCode: {} and depth: {}", percentage, upline.getId(), uplineUserRank, depth);
            if (percentage.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal teamIncome = baseIncome.multiply(percentage);
                //incomeHistoryRepository.save(new IncomeHistory(upline.getId(), teamIncome, IncomeHistory.IncomeType.TEAM));
                log.info("Saving team income of {} for user {}................", teamIncome, upline.getId());
                IncomeHistory incomeHistory = incomeHistoryRepo.save(IncomeHistory.builder()
                        .userId(upline.getId())
                        .amount(teamIncome)
                        .incomeType(IncomeType.TEAM)
                        .sourceUserId(sourceUserId)
                        .sourceUserRank(sourceUserRank)
                        .note("Team income")
                        .build());
                log.info("Saved team income of {} for user {}", teamIncome, upline.getId());
                incomeLogs.add(new UplineIncomeLog(upline.getId(), uplineUserRank, depth, percentage, teamIncome));

                log.info("Updating Wallet Balance for UserId: {} with TeamIncome: {} for SellerID: {} with his DailyIncome: {}", upline.getId(), teamIncome, sourceUserId, baseIncome);
                String metaInfo = getMetaInfo(incomeHistory);
                log.info("MetaInfo: {}", metaInfo);
                WalletUpdateRequest depositRequest = new WalletUpdateRequest(
                        teamIncome,
                        TransactionType.TEAM_INCOME,
                        true,
                        "team-income",
                        Remarks.TEAM_INCOME,
                        metaInfo
                );

                //userClient.deposit(upline.getId(), teamIncome, Remarks.TEAM_INCOME, getMetaInfo(incomeHistory));
                walletApi.updateWalletBalance(upline.getId(), depositRequest);
            }
        }
        return incomeLogs;
    }

    private String getMetaInfo(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }
}
