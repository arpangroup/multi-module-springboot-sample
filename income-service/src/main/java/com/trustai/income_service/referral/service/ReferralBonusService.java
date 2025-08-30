package com.trustai.income_service.referral.service;


import com.trustai.common.enums.TriggerType;

public interface ReferralBonusService {
    /**
     * Creates a pending referral bonus for the specified referrer and referee.
     *
     * This method does **not** immediately apply the referral bonus to the referrer.
     * Instead, it logs a **PENDING** bonus record which will later be evaluated
     * and applied by a scheduled job ((e.g., {@code BonusEvaluator}), once certain conditions are met.
     *
     * This deferred bonus strategy helps prevent abuse For example, if referral bonuses
     * were given immediately upon registration, users could create fake/inactive accounts
     * just to earn bonuses. By waiting for a meaningful action (e.g., a deposit),
     * the system ensures the bonus is earned legitimately.
     *
     * @param referrerId  the ID of the user who made the referral
     * @param refereeId   the ID of the newly registered user (the referee)
     * @param triggerType the type of trigger that initiated this referral
     */
    void createPendingBonus(Long referrerId, Long refereeId, TriggerType triggerType);


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
     * @param refereeId the ID of the user who triggered the referral condition
     */
    void evaluateBonus(Long refereeId);


    /**
     * Evaluates and applies all pending referral bonuses that meet their required conditions.
     *
     * Typically invoked by a scheduled job to process accumulated pending bonuses
     * and apply those that are now eligible.
     */
    void evaluateAllPendingBonuses();


}
