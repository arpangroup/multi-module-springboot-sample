package com.trustai.rank_service.listener;

import com.trustai.common.event.DepositActivityEvent;
import com.trustai.common.event.ReferralJoinedActivityEvent;
import com.trustai.common.event.UserActivatedActivityEvent;
import com.trustai.rank_service.service.RankCalculationOrchestrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

/*
📊 Rank Evaluation Trigger Points
- ✅ Daily batch job
- ✅ After investment/subscription
- ✅ After successful referral
- ✅ Admin manual trigger
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RankEventListener {
    private final RankCalculationOrchestrationService rankOrchestrator;

    @EventListener
    public void onDeposit(DepositActivityEvent event) {
        reevaluateWithCorrelation(event.getUserId(), "DepositEvent");
    }

    @EventListener
    public void handleUserActivated(UserActivatedActivityEvent event) {
        reevaluateWithCorrelation(event.getUserId(), "UserActivatedEvent");
    }

    @EventListener
    public void handleReferralJoined(ReferralJoinedActivityEvent event) {
        reevaluateWithCorrelation(event.getReferrerId(), "ReferralJoinedEvent");
    }

    private void reevaluateWithCorrelation(Long userId, String triggerSource) {
        String correlationId = UUID.randomUUID().toString();
        MDC.put("correlationId", correlationId);
        log.info("📥 Received event for userId={} [source={}]", userId, triggerSource);

        try {
            rankOrchestrator.reevaluateRank(userId, triggerSource, correlationId);
        } catch (Exception e) {
            log.error("❌ Error during rank reevaluation for userId={} [source={}]: {}", userId, triggerSource, e.getMessage(), e);
        } finally {
            MDC.clear();
        }
    }
}
