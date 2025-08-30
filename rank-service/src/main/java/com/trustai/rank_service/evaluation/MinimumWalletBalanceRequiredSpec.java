package com.trustai.rank_service.evaluation;/*
package com.trustai.income_service.rank.evaluation.strategy;

import com.trustai.income_service.rank.entity.RankConfig;
import com.trustai.income_service.rank.evaluation.RankSpecification;
import com.trustai.user_service.hierarchy.dto.UserMetrics;
import com.trustai.user_service.user.entity.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class MinimumWalletBalanceRequiredSpec implements RankSpecification {

    @Override
    public boolean isSatisfied(User user, UserMetrics metrics, RankConfig config) {
        BigDecimal walletBalance = metrics.getWalletBalance();
        BigDecimal requiredBalance = config.getMinDepositAmount();

        // If the field is null or 0 in config, treat as no requirement
        if (requiredBalance == null || requiredBalance.compareTo(BigDecimal.ZERO) <= 0) {
            return true;
        }
        return walletBalance != null && walletBalance.compareTo(requiredBalance) >= 0;
    }
}
*/
