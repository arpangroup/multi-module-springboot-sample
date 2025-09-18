package com.trustai.rank_service.evaluation;

import com.trustai.common.dto.UserInfo;
import com.trustai.common.dto.UserMetrics;
import com.trustai.rank_service.entity.RankConfig;
import org.springframework.stereotype.Component;

@Component
public class MinimumDepositAmountSpec implements RankSpecification {

    @Override
    public boolean isSatisfied(UserInfo user, UserMetrics metrics, RankConfig config) {
        /*return metrics.getTotalDeposit()
                .compareTo(config.getMinDepositAmount()) >= 0;*/

        return metrics.getWalletBalance()
                .compareTo(config.getMinDepositAmount()) >= 0;
    }
}
