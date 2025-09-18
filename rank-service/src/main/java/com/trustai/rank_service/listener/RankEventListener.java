package com.trustai.rank_service.listener;

import com.trustai.common.api.UserApi;
import com.trustai.common.dto.UserHierarchyDto;
import com.trustai.common.dto.UserHierarchyStats;
import com.trustai.common.dto.UserMetrics;
import com.trustai.common.event.DepositActivityEvent;
import com.trustai.common.event.ReferralJoinedActivityEvent;
import com.trustai.common.event.UserActivatedActivityEvent;
import com.trustai.rank_service.service.RankCalculationOrchestrationService;
import com.trustai.rank_service.service.RankEvaluatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
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
    private final UserApi userApi;

    private enum RankTriggerSource {
        DEPOSIT, USER_ACTIVATED, REFERRAL_JOINED
    }


    @EventListener
    public void onDeposit(DepositActivityEvent event) {
        Long userId = event.getUserId();
        String correlationId = UUID.randomUUID().toString();
        MDC.put("correlationId", correlationId);

        // reevaluate for depositor
        rankOrchestrator.reevaluateRank(userId, RankTriggerSource.DEPOSIT.name(), correlationId);

        if (event.isFirstDeposit()) { // update all upline users rank
            List<UserHierarchyDto> uplines = userApi.findByDescendant(userId);
            List<Long> uplineIds = uplines.stream()
                    .map(UserHierarchyDto::getAncestor)
                    .filter(ancestorId -> !ancestorId.equals(event.getUserId()))
                    .toList();

            // async reevaluation for uplines
            reevaluateUplinesAsync(uplineIds, MDC.get("correlationId"));
        }
    }

    @EventListener
    public void handleUserActivated(UserActivatedActivityEvent event) {
        reevaluateWithCorrelation(event.getUserId(), RankTriggerSource.USER_ACTIVATED.name());
    }

    @EventListener
    public void handleReferralJoined(ReferralJoinedActivityEvent event) {
        reevaluateWithCorrelation(event.getReferrerId(), RankTriggerSource.REFERRAL_JOINED.name());
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

    /**
     * 🔹 Async bulk reevaluation of uplines (resilient per upline)
     */
    @Async
    protected void reevaluateUplinesAsync(List<Long> uplineIds, String correlationId) {
        MDC.put("correlationId", correlationId);
        for (Long uplineId : uplineIds) {
            try {
                rankOrchestrator.reevaluateRank(uplineId, RankTriggerSource.DEPOSIT.name(), correlationId);
            } catch (Exception e) {
                log.error("⚠️ Failed to reevaluate uplineId={} during deposit event", uplineId, e);
            }
        }
        MDC.clear();
    }
}
