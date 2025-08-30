package com.trustai.rank_service.evaluation;

import com.trustai.common.dto.UserInfo;
import com.trustai.common.dto.UserMetrics;
import com.trustai.rank_service.entity.RankConfig;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RequiredLevelCountsSpec implements RankSpecification {
    @Override
    public boolean isSatisfied(UserInfo user, UserMetrics metrics, RankConfig config) {
        Map<Integer, Integer> required = config.getRequiredLevelCounts();
        Map<Integer, Long> actual = metrics.getUserHierarchyStats().getDepthWiseCounts();

        for (Map.Entry<Integer, Integer> entry : required.entrySet()) {
            if (actual.getOrDefault(entry.getKey(), 0L) < entry.getValue()) {
                return false;
            }
        }
        return true;
    }
}
