package com.trustai.income_service.referral.calculator;

import com.trustai.common.api.UserApi;
import com.trustai.common.dto.UserInfo;
import com.trustai.common.enums.CalculationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

// TIERED: Apply different percentages per tier
@Deprecated
@Component
public class TieredBonusCalculator implements BonusAmountCalculator {

    @Autowired
    private UserApi userApi;


    @Override
    public BigDecimal calculate(UserInfo referrer, UserInfo referee) {
        //BigDecimal deposit = userClient.getTotalDeposit(referee.getId());
        BigDecimal deposit = referee.getWalletBalance();

        if (deposit.compareTo(BigDecimal.valueOf(5000)) >= 0) {
            return deposit.multiply(BigDecimal.valueOf(0.15));
        } else if (deposit.compareTo(BigDecimal.valueOf(2000)) >= 0) {
            return deposit.multiply(BigDecimal.valueOf(0.10));
        }

        return deposit.multiply(BigDecimal.valueOf(0.05));
    }

    @Override
    public CalculationType getType() {
        return CalculationType.TIERED;
    }
}
