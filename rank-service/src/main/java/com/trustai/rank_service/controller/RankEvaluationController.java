package com.trustai.rank_service.controller;

import com.trustai.rank_service.dto.RankEvaluationResultDTO;
import com.trustai.rank_service.service.RankEvaluatorServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rankings")
@RequiredArgsConstructor
@Slf4j
public class RankEvaluationController {
    private final RankEvaluatorServiceImpl rankEvaluationService;

    /**
     * Trigger rank evaluation for a specific user.
     */
    @PostMapping("/re-evaluate/{userId}")
    public ResponseEntity<RankEvaluationResultDTO> evaluateUserRank(@PathVariable Long userId) {
        RankEvaluationResultDTO result = rankEvaluationService.evaluateAndUpdateRank(userId);
        return ResponseEntity.ok(result);
    }

    /**
     * Optional: Evaluate ranks in batch (e.g., cron job or admin trigger).
     */
    @PostMapping("/re-evaluate/batch")
    public ResponseEntity<List<RankEvaluationResultDTO>> evaluateMultipleUsers(@RequestBody List<Long> userIds) {
        List<RankEvaluationResultDTO> results = rankEvaluationService.evaluateAndUpdateRanks(userIds);
        return ResponseEntity.ok(results);
    }
}
