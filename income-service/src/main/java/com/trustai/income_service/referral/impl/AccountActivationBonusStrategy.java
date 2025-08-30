package com.trustai.income_service.referral.impl;

import com.trustai.common.dto.UserInfo;
import com.trustai.common.enums.TriggerType;
import com.trustai.income_service.referral.AbstractReferralBonusStrategy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component("ACCOUNT_ACTIVATION")
public class AccountActivationBonusStrategy extends AbstractReferralBonusStrategy {

    @Override
    public boolean isEligible(UserInfo referee) {
        return referee.isActive();
    }

    @Override
    protected BigDecimal getBonusAmount(UserInfo referrer, UserInfo referee) {
        return calculator.calculate(referrer, referee);
    }

    @Override
    protected TriggerType getTriggerType() {
        return TriggerType.ACCOUNT_ACTIVATION;
    }
}
