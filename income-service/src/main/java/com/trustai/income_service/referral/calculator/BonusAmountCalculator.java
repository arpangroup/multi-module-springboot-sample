package com.trustai.income_service.referral.calculator;


import com.trustai.common.dto.UserInfo;
import com.trustai.common.enums.CalculationType;

import java.math.BigDecimal;

public interface BonusAmountCalculator {
    BigDecimal calculate(UserInfo referrer, UserInfo referee);
    CalculationType getType();
}
