package com.trustai.income_service.referral.calculator;

import com.trustai.common.dto.UserInfo;
import com.trustai.common.enums.CalculationType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class FlatBonusCalculator implements BonusAmountCalculator {
    @Value("${bonus.referral.flat-amount}")
    private int flatAmount;

    @Override
    public BigDecimal calculate(UserInfo referrer, UserInfo referee) {
        return BigDecimal.valueOf(flatAmount);
    }

    @Override
    public CalculationType getType() {
        return CalculationType.FLAT;
    }
}
