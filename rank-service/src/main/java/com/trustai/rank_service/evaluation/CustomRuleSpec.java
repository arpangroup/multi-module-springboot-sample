package com.trustai.rank_service.evaluation;//package com.trustai.income_service.rank.evaluation.strategy;
//
//import com.trustai.income_service.rank.entity.RankConfig;
//import com.trustai.income_service.rank.evaluation.RankSpecification;
//import com.trustai.user_service.hierarchy.dto.UserMetrics;
//import com.trustai.user_service.user.entity.User;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//public class CustomRuleSpec implements RankSpecification {
//    private final CustomRuleService customRuleService;
//
//    @Override
//    public boolean isSatisfied(User user, UserMetrics metrics, RankConfig config) {
//        return customRuleService.evaluateCustomLogic(user, config);
//    }
//}
