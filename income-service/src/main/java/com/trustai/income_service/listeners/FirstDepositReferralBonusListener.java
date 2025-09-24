package com.trustai.income_service.listeners;


import com.trustai.common.event.FirstDepositEvent;
import com.trustai.income_service.referral.service.ReferralBonusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class FirstDepositReferralBonusListener {
    private final ReferralBonusService referralBonusService;

//    @EventListener
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleFirstDepositEvent(FirstDepositEvent event) {
        Long userId = event.getUserId();
        BigDecimal amount = event.getAmount();

        log.info("📥 FirstDepositEvent received | userId={}, amount={}", userId, amount);

        try {
            log.info("🔄 Evaluating referral bonus | userId={}", userId);
            referralBonusService.approvePendingBonus(userId, amount);
            log.info("✅ Referral bonus evaluation completed | userId={}", userId);
        } catch (Exception e) {
            log.error("❌ Failed to evaluate referral bonus | userId={}, amount={}", userId, amount, e);
        }
    }
}
