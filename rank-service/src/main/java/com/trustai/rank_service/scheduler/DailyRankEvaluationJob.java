package com.trustai.rank_service.scheduler;

import com.trustai.common.api.UserApi;
import com.trustai.common.dto.UserInfo;
import com.trustai.rank_service.service.RankCalculationOrchestrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/*
List<UserInfo> users = userApi.getAllUsers();
with paginated iteration:
int page = 0, size = 100;
List<UserInfo> batch;

do {
    batch = userApi.getUsersByPage(page, size);
    batch.forEach(user -> orchestrationService.reevaluateRank(user.getId(), "DAILY_SCHEDULED_JOB"));
    page++;
} while (!batch.isEmpty());
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DailyRankEvaluationJob {
    private final RankCalculationOrchestrationService orchestrationService;
    private final UserApi userApi;

    // Run daily at 2:00 AM IST
    @Scheduled(cron = "0 0 2 * * *", zone = "Asia/Kolkata") // Use correct zone if needed
    public void evaluateRanks() {
        String correlationId = UUID.randomUUID().toString();
        MDC.put("correlationId", correlationId);
        log.info("🕑 Starting scheduled daily rank evaluation... [correlationId={}]", correlationId);

        try {
            List<UserInfo> users = userApi.getUsers(); // Make sure this method exists and is paginated if large
            log.info("📋 Retrieved {} users for rank evaluation.", users.size());

            int successCount = 0;
            int failureCount = 0;

            for (UserInfo user : users) {
                try {
                    log.debug("🔄 Re-evaluating rank for userId: {}", user.getId());
                    orchestrationService.reevaluateRank(user.getId(), "DAILY_SCHEDULED_JOB", correlationId);
                    successCount++;
                } catch (Exception e) {
                    failureCount++;
                    log.warn("⚠️ Failed to evaluate rank for userId {}: {}", user.getId(), e.getMessage(), e);
                }
            }
            log.info("✅ Daily rank evaluation completed. Success: {}, Failures: {}", successCount, failureCount);
        } catch (Exception ex) {
            log.error("❌ Rank evaluation job failed entirely: {}", ex.getMessage(), ex);
        }
    }
}
