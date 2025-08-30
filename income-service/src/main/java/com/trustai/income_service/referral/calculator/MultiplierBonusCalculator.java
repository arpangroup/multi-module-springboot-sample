/*
package com.trustai.income_service.referral.calculator;

import com.trustai.common.client.UserClient;
import com.trustai.common.dto.UserInfo;
import com.trustai.common.enums.CalculationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

// MULTIPLIER: Referrer’s rankCode/score multiplies the amount
@Deprecated
@Component
public class MultiplierBonusCalculator implements BonusAmountCalculator {

    @Autowired
    private UserClient userClient;

    @Override
    public BigDecimal calculate(UserInfo referrer, UserInfo referee) {
        // MULTIPLIER: Referrer’s rankCode/score multiplies the amount
        //int multiplier = userClient.getRankMultiplier(referrer.getId()); // e.g., Silver=1, Gold=2
        int multiplier = 1;
        BigDecimal base = BigDecimal.valueOf(100);
        return base.multiply(BigDecimal.valueOf(multiplier));
    }

    @Override
    public CalculationType getType() {
        return CalculationType.MULTIPLIER;
    }
}*/
