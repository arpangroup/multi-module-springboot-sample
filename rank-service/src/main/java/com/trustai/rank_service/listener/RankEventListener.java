package com.trustai.rank_service.listener;

import com.trustai.common.event.DepositActivityEvent;
import com.trustai.common.event.ReferralJoinedActivityEvent;
import com.trustai.common.event.UserActivatedActivityEvent;
import com.trustai.rank_service.service.RankCalculationOrchestrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/*
📊 Rank Evaluation Trigger Points
- ✅ Daily batch job
- ✅ After investment/subscription
- ✅ After successful referral
- ✅ Admin manual trigger
 */
@Component
@RequiredArgsConstructor
public class RankEventListener {
    private final RankCalculationOrchestrationService rankOrchestrator;

    @EventListener
    public void onDeposit(DepositActivityEvent event) {
        rankOrchestrator.reevaluateRank(event.getUserId(), "DepositEvent");
    }

    @EventListener
    public void handleUserActivated(UserActivatedActivityEvent event) {
        rankOrchestrator.reevaluateRank(event.getUserId(), "UserActivatedEvent");
    }

    @EventListener
    public void handleReferralJoined(ReferralJoinedActivityEvent event) {
        rankOrchestrator.reevaluateRank(event.getReferrerId(), "ReferralJoinedEvent");
    }
}
