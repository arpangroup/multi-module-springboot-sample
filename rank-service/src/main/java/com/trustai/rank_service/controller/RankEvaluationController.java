package com.trustai.rank_service.controller;

import com.trustai.common.dto.ApiResponse;
import com.trustai.rank_service.dto.RankEvaluationResultDTO;
import com.trustai.rank_service.scheduler.DailyRankEvaluationJob;
import com.trustai.rank_service.service.RankEvaluatorServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/rankings")
@RequiredArgsConstructor
@Slf4j
public class RankEvaluationController {
    private final RankEvaluatorServiceImpl rankEvaluationService;
    private final DailyRankEvaluationJob rankEvaluationJob;

    /**
     * Trigger rank evaluation for a specific user.
     */
    @PostMapping("/re-evaluate/{userId}")
    public ResponseEntity<ApiResponse<?>> evaluateUserRank(@PathVariable Long userId) {
        log.info("Received request to re-evaluate rank for userId={}", userId);
        RankEvaluationResultDTO result = rankEvaluationService.evaluateAndUpdateRank(userId);

        // Construct a meaningful message from the DTO
        String message = constructMessage(result);
        log.info("Rank evaluation result: {}", message);

        return ResponseEntity.ok(ApiResponse.success(message));
    }

    /**
     * Optional: Evaluate ranks in batch (e.g., cron job or admin trigger).
     */
    @PostMapping("/re-evaluate/batch")
    public ResponseEntity<ApiResponse<?>> evaluateMultipleUsers(@RequestBody List<Long> userIds) {
        log.info("Received batch request to re-evaluate ranks for userIds={}", userIds);
        rankEvaluationJob.evaluateRanks();
        return ResponseEntity.ok(ApiResponse.success("Ranks re-evaluated successfully"));
    }

    private String constructMessage(RankEvaluationResultDTO result) {
        if (result.upgraded()) {
            return String.format(
                    "User %d rank upgraded from %s to %s. Reason: %s",
                    result.userId(),
                    result.oldRankCode(),
                    result.newRankCode(),
                    result.reason()
            );
        } else if (result.oldRankCode().equals(result.newRankCode())) {
            return String.format(
                    "User %d rank remains the same (%s). No upgrade needed.",
                    result.userId(),
                    result.oldRankCode()
            );
        } else {
            return String.format(
                    "User %d rank evaluation completed. Current rank: %s. Reason: %s",
                    result.userId(),
                    result.newRankCode(),
                    result.reason()
            );
        }
    }
}
