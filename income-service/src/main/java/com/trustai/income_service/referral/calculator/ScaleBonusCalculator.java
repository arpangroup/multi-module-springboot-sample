/*
package com.trustai.income_service.referral.calculator;

import com.trustai.common.client.UserClient;
import com.trustai.common.dto.UserInfo;
import com.trustai.common.enums.CalculationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

// SCALE: Bonus increases by deposit range
@Deprecated
@Component
public class ScaleBonusCalculator implements BonusAmountCalculator {

    @Autowired
    private UserClient userClient;

    @Override
    public BigDecimal calculate(UserInfo referrer, UserInfo referee) {
        //BigDecimal depositAmount = userClient.getTotalDeposit(referee.getId());
        BigDecimal depositAmount = referee.getWalletBalance();

        // SCALE: Bonus increases by deposit range
        if (depositAmount.compareTo(BigDecimal.valueOf(5000)) >= 0) {
            return BigDecimal.valueOf(500);
        } else if (depositAmount.compareTo(BigDecimal.valueOf(2000)) >= 0) {
            return BigDecimal.valueOf(200);
        } else if (depositAmount.compareTo(BigDecimal.valueOf(1000)) >= 0) {
            return BigDecimal.valueOf(100);
        }

        return BigDecimal.valueOf(50); // minimum
    }

    @Override
    public CalculationType getType() {
        return CalculationType.SCALE;
    }
}
*/
