package com.trustai.income_service.scheduler;

import com.trustai.income_service.referral.service.ReferralBonusService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;

@RequiredArgsConstructor
public class BonusEvaluator {
    private final ReferralBonusService bonusService;

    @Scheduled(fixedRate = 86400000) // every 24 hours
    public void checkAndApplyBonuses() {
        bonusService.evaluateAllPendingBonuses();
    }
}
