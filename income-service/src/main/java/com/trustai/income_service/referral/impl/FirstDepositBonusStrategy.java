package com.trustai.income_service.referral.impl;

import com.trustai.common.dto.UserInfo;
import com.trustai.common.enums.TriggerType;
import com.trustai.income_service.referral.AbstractReferralBonusStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component("FIRST_DEPOSIT")
@Slf4j
public class FirstDepositBonusStrategy extends AbstractReferralBonusStrategy {

    @Override
    public boolean isEligible(UserInfo referee) {
        log.info("isEligible for firstDeposit for userId: {}", referee.getId());
        BigDecimal depositBalance = transactionClient.getDepositBalance(referee.getId());
        log.info("userId: {}, depositBalance: {}", referee.getId(), depositBalance);
        return depositBalance.compareTo(BigDecimal.ZERO) > 0;
    }

    @Override
    protected BigDecimal getBonusAmount(UserInfo referrer, UserInfo referee) {
        return calculator.calculate(referrer, referee);
    }

    @Override
    protected TriggerType getTriggerType() {
        return TriggerType.FIRST_DEPOSIT;
    }
}
