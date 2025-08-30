package com.trustai.rank_service.scheduler;

import com.trustai.common.api.UserApi;
import com.trustai.common.dto.UserInfo;
import com.trustai.rank_service.service.RankCalculationOrchestrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
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
        log.info("🕑 Starting scheduled daily rank evaluation...");

        try {
            List<UserInfo> users = userApi.getUsers(); // Make sure this method exists and is paginated if large
            int count = 0;

            for (UserInfo user : users) {
                orchestrationService.reevaluateRank(user.getId(), "DAILY_SCHEDULED_JOB");
                count++;
            }
            log.info("✅ Daily rank evaluation completed");
        } catch (Exception ex) {
            log.error("❌ Rank evaluation failed: {}", ex.getMessage(), ex);
        }
    }
}
