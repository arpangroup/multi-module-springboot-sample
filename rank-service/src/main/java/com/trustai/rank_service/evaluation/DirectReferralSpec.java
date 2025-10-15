package com.trustai.rank_service.evaluation;

import com.trustai.common.dto.UserInfo;
import com.trustai.common.dto.UserMetrics;
import com.trustai.rank_service.entity.RankConfig;
import org.springframework.stereotype.Component;

@Component
public class DirectReferralSpec implements RankSpecification {

    @Override
    public boolean isSatisfied(UserInfo user, UserMetrics metrics, RankConfig config) {
        int directReferrals = metrics.getDirectReferrals(); // from precomputed stats
//        int directReferrals = metrics.getUserHierarchyStats()
//                .getActiveDepthWiseCounts()
//                .getOrDefault(1, 0L) // <-- depth = 1
//                .intValue(); // safely convert to int
        return directReferrals >= config.getMinDirectReferrals();
    }
}
