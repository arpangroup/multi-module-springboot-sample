package com.trustai.rank_service.evaluation;//package com.trustai.income_service.rank.evaluation.strategy;
//
//import com.trustai.income_service.rank.entity.RankConfig;
//import com.trustai.income_service.rank.evaluation.RankSpecification;
//import com.trustai.user_service.hierarchy.dto.UserMetrics;
//import com.trustai.user_service.user.entity.User;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDate;
//
//@Component
//public class TxnPerDaySpec implements RankSpecification {
//    private final TransactionRepository txnRepo;
//
//    @Override
//    public boolean isSatisfied(User user, UserMetrics metrics, RankConfig config) {
//        LocalDate today = LocalDate.now();
//        int txnCount = txnRepo.countByUserIdAndDate(user.getId(), today);
//        return txnCount >= config.getTxnPerDay();
//    }
//}
