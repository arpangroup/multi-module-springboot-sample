package com.trustai.income_service.referral.impl;


import com.trustai.common.dto.UserInfo;
import com.trustai.common.enums.TriggerType;
import com.trustai.income_service.referral.AbstractReferralBonusStrategy;

import java.math.BigDecimal;


public class MinimumRequirementBonusStrategy extends AbstractReferralBonusStrategy {
    @Override
    public boolean isEligible(UserInfo referee) {
//        return referee.getRank() >= RankLevel.LEVEL_1.getValue(); // or check invites etc.
        return false;
    }

    @Override
    protected BigDecimal getBonusAmount(UserInfo referrer, UserInfo referee) {
        return calculator.calculate(referrer, referee);
    }

    @Override
    protected TriggerType getTriggerType() {
        return TriggerType.MINIMUM_REQUIREMENT;
    }
}