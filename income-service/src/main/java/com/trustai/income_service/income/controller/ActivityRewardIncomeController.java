package com.trustai.income_service.income.controller;

import com.trustai.common.dto.ApiResponse;
import com.trustai.income_service.income.dto.ActivityRewardRequest;
import com.trustai.income_service.income.service.ActivityRewardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/income/activity-reward")
@RequiredArgsConstructor
@Slf4j
public class ActivityRewardIncomeController {
    private final ActivityRewardService activityRewardService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> applyActivityReward(@RequestBody @Valid ActivityRewardRequest request) {
        log.info("Received activity reward request: {}", request);
        activityRewardService.applyActivityReward(request.userId(), request.rewardAmount(), request.message());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Reward applied successfully."));
    }
}
