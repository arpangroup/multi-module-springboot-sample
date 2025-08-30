//package com.trustai.income_service.referral.calculator;
//
//import com.trustai.common.client.UserClient;
//import com.trustai.common.dto.UserInfo;
//import com.trustai.common.enums.CalculationType;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.math.BigDecimal;
//
//// FORMULA: Use configurable or hardcoded formula
//@Deprecated
//@Component
//public class FormulaBonusCalculator implements BonusAmountCalculator {
//
//    @Autowired
//    private UserClient userClient;
//
//    @Override
//    public BigDecimal calculate(UserInfo referrer, UserInfo referee) {
//        /*BigDecimal deposit = userClient.getTotalDeposit(referee.getId());
//        int referrals = userClient.getTotalReferrals(referrer.getId());
//
//        // Formula: (deposit * 5%) + (referrals * 10)
//        BigDecimal percent = deposit.multiply(BigDecimal.valueOf(0.05));
//        BigDecimal referralBoost = BigDecimal.valueOf(referrals).multiply(BigDecimal.TEN);
//
//        return percent.add(referralBoost).setScale(2, BigDecimal.ROUND_HALF_UP);*/
//
//        return BigDecimal.ONE;
//    }
//
//    @Override
//    public CalculationType getType() {
//        return CalculationType.FORMULA;
//    }
//}