package com.trustai.income_service.referral;


import com.trustai.common.dto.UserInfo;
import com.trustai.income_service.referral.entity.ReferralBonus;

/**
 * Referrer: The person who makes the referral.
 * Referee: The person who is being referred.
 */
public interface ReferralBonusStrategy {
    boolean isEligible(UserInfo referee);
    void applyBonus(UserInfo referrer, UserInfo referee);
    boolean processPendingBonus(ReferralBonus bonus);
}
