package com.trustai.rank_service.evaluation;/*
package com.trustai.income_service.rank.evaluation.strategy;

import com.trustai.income_service.rank.entity.RankConfig;
import com.trustai.income_service.rank.evaluation.RankSpecification;
import com.trustai.user_service.hierarchy.dto.UserMetrics;
import com.trustai.user_service.user.entity.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class MinTeamVolumeSpec implements RankSpecification {

    @Override
    public boolean isSatisfied(User user, UserMetrics metrics, RankConfig config) {
        BigDecimal teamVolume = metrics.getTotalReferralDeposit(); // or totalReferralInvestment
        return teamVolume.compareTo(BigDecimal.valueOf(config.getMinTeamVolume())) >= 0;
    }
}
*/
