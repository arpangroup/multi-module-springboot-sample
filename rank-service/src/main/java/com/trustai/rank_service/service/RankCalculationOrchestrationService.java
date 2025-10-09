package com.trustai.rank_service.service;

import com.trustai.common.api.UserApi;
import com.trustai.common.dto.UserInfo;
import com.trustai.rank_service.entity.RankConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/*
📊 Rank Evaluation Trigger Points
- ✅ Daily batch job
- ✅ After investment/subscription
- ✅ After successful referral
- ✅ Admin manual trigger
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RankCalculationOrchestrationService {
    private final RankEvaluatorService rankEvaluatorService;
    private final UserApi userApi; // REST or Feign client to user-service
    //private final UserActivityLogService activityLogService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void reevaluateRank(Long userId, String triggerSource, String correlationId) {
        MDC.put("correlationId", correlationId);
        log.info("🔍 Starting rank evaluation for userId={} [triggerSource={}]", userId, triggerSource);

        try {
            UserInfo userInfo = userApi.getUserById(userId);
            log.debug("📄 Retrieved user info for userId={}: currentRank={}", userId, userInfo.getRankCode());

            Optional<RankConfig> newRankOpt = rankEvaluatorService.evaluate(userInfo);

            if (newRankOpt.isPresent()) {
                RankConfig newRank = newRankOpt.get();
                String currentRankCode = userInfo.getRankCode();

                if (!newRank.getCode().equals(currentRankCode)) {
                    log.info("⬆️ Rank change detected for userId={}: {} → {}", userId, currentRankCode, newRank.getCode());

                    userApi.updateRank(userId, newRank.getCode());
                    log.info("✅ Rank updated successfully for userId={} to new rank={}", userId, newRank.getCode());

                    // activityLogService.save(UserActivityLog.rankChanged(userId, currentRankCode, newRank.getCode(), triggerSource));
                } else {
                    log.debug("⏸️ No rank change needed for userId={}. Already at correct rank={}", userId, currentRankCode);
                }

            } else {
                log.warn("⚠️ No new rank determined for userId={}. Skipping update.", userId);
            }

        } catch (Exception ex) {
            log.error("❌ Failed to reevaluate rank for userId={} [triggerSource={}]: {}", userId, triggerSource, ex.getMessage(), ex);
        } finally {
            MDC.clear(); // Clean up to avoid leaking into other threads
        }
    }
}
