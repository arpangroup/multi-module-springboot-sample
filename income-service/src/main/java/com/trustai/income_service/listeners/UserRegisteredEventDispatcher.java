package com.trustai.income_service.listeners;

import com.trustai.common.event.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserRegisteredEventDispatcher {
    @EventListener
    public void dispatch(UserRegisteredEvent event) {
        // control order here
        Long refereeId = event.getRefereeId();
        Long referrerId = event.getReferrerId();

        // Step 1: Update the user hierarchy tree to reflect the new referral relationship
        log.info("UserRegisteredEvent: Updating hierarchy - Referrer ID: {}, Referee ID: {}", referrerId, refereeId);
        //userHierarchyService.updateHierarchy(event.getReferrerId(), event.getRefereeId());

        // Step 2: Create a pending bonus (not immediately granted)
        /*
         * This method does NOT immediately apply the referral bonus.
         * It creates a pending bonus record, which is later evaluated
         * by a scheduled job (BonusEvaluator) based on eligibility criteria
         * like first deposit or purchase.
         *
         * This deferred bonus model helps:
         * - Prevent abuse from fake/inactive sign-ups.
         * - Ensure bonuses are only rewarded for meaningful referrals.
         */
        log.info("UserRegisteredEvent:  Creating pending bonus - Referrer ID: {}, Referee ID: {}, Trigger: {}", referrerId, refereeId, event.getTriggerType());
        //referralBonusService.createPendingBonus(event.getReferrerId(), event.getRefereeId(), event.getTriggerType());

        // Step 3: Evaluate and update the rankCode for both referee and referrer
        /**
         * Handles updating the user's rankCode after a downline user registration event.
         * Triggers immediate rankCode evaluation based on downline structure.
         *
         * Note: This event is triggered when a new user registers under a referrer.
         *
         * Pros:
         * - Immediate recognition of new referrals.
         * - Can motivate referrers early by acknowledging downline growth quickly.
         *
         * Cons:
         * - May cause rankCode inflation due to inactive or fake registrations.
         * - Users who never make deposits or purchases still trigger rankCode updates,
         *   potentially leading to inaccurate rankCode assignments.
         */
        log.info("UserRegisteredEvent: Evaluating and updating ranks - Referrer ID: {}, Referee ID: {}", referrerId, refereeId);
        //rankEvaluationService.evaluateAndUpdateUserWithReferrerRank(event.getRefereeId(), BigDecimal.ZERO, event.getReferrerId());
    }
}
