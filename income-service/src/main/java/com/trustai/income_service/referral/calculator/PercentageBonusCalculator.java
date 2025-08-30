package com.trustai.income_service.referral.calculator;

import com.trustai.common.api.UserApi;
import com.trustai.common.dto.UserInfo;
import com.trustai.common.enums.CalculationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component("PERCENTAGE")
public class PercentageBonusCalculator implements BonusAmountCalculator {
    @Value("${bonus.referral.percentage-rate}")
    private double percentage; // e.g., 10.0 for 10%

    @Autowired
    private UserApi userApi;

    @Override
    public BigDecimal calculate(UserInfo referrer, UserInfo referee) {
        //BigDecimal depositAmount = userClient.getTotalDeposit(referee.getId());
        BigDecimal depositAmount = referee.getWalletBalance();

        if (depositAmount == null || depositAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal bonus = depositAmount
                .multiply(BigDecimal.valueOf(percentage))
                .divide(BigDecimal.valueOf(100));

        return bonus.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    @Override
    public CalculationType getType() {
        return CalculationType.PERCENTAGE;
    }
}
