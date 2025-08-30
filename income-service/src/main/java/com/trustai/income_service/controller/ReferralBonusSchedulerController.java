package com.trustai.income_service.controller;

import com.trustai.income_service.referral.service.ReferralBonusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/bonus/referral")
@RequiredArgsConstructor
public class ReferralBonusSchedulerController {
    private final ReferralBonusService referralBonusService;

    @GetMapping("/schedule")
    public ResponseEntity<?> schedule() {
        referralBonusService.evaluateAllPendingBonuses();
        return ResponseEntity.ok(Map.of("result", "schedule success!!"));
    }
}
