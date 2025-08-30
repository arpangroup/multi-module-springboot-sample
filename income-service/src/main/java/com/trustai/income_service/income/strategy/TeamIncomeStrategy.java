package com.trustai.income_service.income.strategy;

import com.trustai.common.dto.UserInfo;
import com.trustai.income_service.income.dto.UplineIncomeLog;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface TeamIncomeStrategy {
    List<UplineIncomeLog> distributeTeamIncome(Long sourceUserId, String sourceUserRank, BigDecimal baseIncome, List<UserInfo> uplines, Map<Long, Integer> uplineDepthMap);
}
