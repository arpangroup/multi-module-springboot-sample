package com.trustai.rank_service.evaluation;


import com.trustai.common.dto.UserInfo;
import com.trustai.common.dto.UserMetrics;
import com.trustai.rank_service.dto.SpecificationResult;
import com.trustai.rank_service.entity.RankConfig;

public interface RankSpecification {
    boolean isSatisfied(UserInfo user, UserMetrics metrics, RankConfig config);

    default SpecificationResult evaluate(UserInfo user, UserMetrics metrics, RankConfig config) {
        boolean result = isSatisfied(user, metrics, config);
        return new SpecificationResult(result, getClass().getSimpleName(), result ? "Satisfied" : "Not satisfied");
    }
}
