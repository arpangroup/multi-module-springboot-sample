package com.trustai.rank_service.listener;/*
package com.trustai.income_service.rank.listener;

import com.trustai.common.event.DepositActivityEvent;
import com.trustai.common.event.ReferralJoinedActivityEvent;
import com.trustai.common.event.UserActivityEvent;
import com.trustai.income_service.rank.service.RankEvaluatorService;
import com.trustai.user_service.user.entity.User;
import com.trustai.user_service.user.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserActivityEventListener {
    private final RankEvaluatorService rankEvaluator;
    private final UserProfileService userService;


    @EventListener
    public void handleUserActivity(UserActivityEvent event) {
        User user = userService.getUserById(event.getUserId());

        if (event instanceof DepositActivityEvent) {
            // Optional: log amount/deposit type
        } else if (event instanceof ReferralJoinedActivityEvent) {
            // Optional: log referral join
        }

        // Evaluate rank for all
        rankEvaluator.evaluate(user).ifPresent(newRank -> {
            user.setCurrentRank(newRank);
            userService.save(user);
        });
    }
}
*/
