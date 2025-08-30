package com.trustai.income_service.referral.listener;

import com.trustai.common.event.FirstDepositEvent;
import com.trustai.common.event.ReferralTriggerEvent;
import com.trustai.income_service.referral.service.ReferralBonusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;

/**
 * [DEPRECATED] This class is now obsolete.
 *
 * Rank update responsibilities are now handled by centralized event dispatchers:
 * - {@link com.arpangroup.referral_service.listener.FirstDepositEventDispatcher}
 *
 * This class remains only for reference and backward compatibility (if needed).
 */
@Deprecated
//@Component
//@ConditionalOnProperty(name = "feature.deprecated-hierarchy-listener.enabled", havingValue = "true", matchIfMissing = false)
@RequiredArgsConstructor
@Slf4j
public class ReferralTriggerEventListener {
    private final ReferralBonusService referralBonusService;

    /**
     * Immediately evaluates and applies the referral bonus for the given referee.
     *
     * Unlike {@link #createPendingBonus}, this method does NOT create a pending record.
     * It **directly applies** the referral bonus to the referrer.
     * It assumes that the triggering condition (such as a successful deposit)
     * has already been satisfied and directly applies the bonus to the referrer.
     *
     * Useful when the triggering condition (e.g., successful first deposit)
     * is already fulfilled, and there is no need to wait for the scheduler
     * to evaluate and apply the bonus.
     *
     * @param event the FirstDepositEvent containing referrer and referee information
     */
    @EventListener
    public void handleReferralTrigger(FirstDepositEvent event) {
        //log.info("FirstDepositEvent: Evaluate Referral Bonus for - Referrer ID: {}, Referee ID: {}", event.getReferrerId(), event.getUserId());
        //referralBonusService.evaluateBonus(event.getUserId());
    }


    /**
     * Handles the ReferralTriggerEvent.
     *
     * This method **directly applies** the referral bonus to the referrer.
     * It does **not** create a pending bonus record.
     *
     * Useful when the triggering condition (e.g., successful first deposit)
     * is already fulfilled, and there is no need to wait for the scheduler
     * to evaluate and apply the bonus.
     *
     * @param event the event indicating a referral trigger condition has been met
     */
    @EventListener
    public void handleReferralTrigger(ReferralTriggerEvent event) {
        //log.info("Listening :: ReferralTriggerEvent for userId: {}.....", event.getUserId());
        //referralBonusService.evaluateBonus(event.getUserId());
    }
}
