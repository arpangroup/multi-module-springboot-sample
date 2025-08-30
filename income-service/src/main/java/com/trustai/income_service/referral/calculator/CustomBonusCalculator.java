package com.trustai.income_service.referral.calculator;

import com.trustai.common.dto.UserInfo;
import com.trustai.common.enums.CalculationType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

// CUSTOM: Delegate to a configurable script or external rule
@Deprecated
@Component
public class CustomBonusCalculator implements BonusAmountCalculator {

//    @Autowired
//    private RuleEngine ruleEngine; // Hypothetical rule engine service

    @Override
    public BigDecimal calculate(UserInfo referrer, UserInfo referee) {
        //return ruleEngine.evaluateBonus(referrer, referee);

        return BigDecimal.ZERO;
    }

    @Override
    public CalculationType getType() {
        return CalculationType.CUSTOM;
    }
}
