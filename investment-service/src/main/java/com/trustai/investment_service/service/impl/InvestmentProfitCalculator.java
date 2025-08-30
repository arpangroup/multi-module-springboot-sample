package com.trustai.investment_service.service.impl;

import com.trustai.investment_service.entity.InvestmentSchema;
import com.trustai.investment_service.entity.UserInvestment;
import com.trustai.investment_service.enums.InterestCalculationType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *  This computes single period profit.
 *  returnRate is interpreted per return period (not per year/month unless you encode that into schedule).
 */
@Component
public class InvestmentProfitCalculator {

    public BigDecimal calculateProfit(UserInvestment investment) {
        InvestmentSchema schema = investment.getSchema();
        BigDecimal invested = investment.getInvestedAmount();

        if (schema.getInterestCalculationMethod() == InterestCalculationType.FLAT) {
            return schema.getReturnRate(); // e.g., ₹100 per period
        }

        if (schema.getInterestCalculationMethod() == InterestCalculationType.PERCENTAGE) {
            // returnRate is % per period — e.g., 5 = 5% per period
            return invested.multiply(schema.getReturnRate())
                    .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        }

        return BigDecimal.ZERO;
    }

    /*
    | ROI Type     | Formula                     | Example           |
    | ------------ | --------------------------- | ----------------- |
    | `FLAT`       | returnRate                  | ₹100 per period   |
    | `PERCENTAGE` | invested × returnRate ÷ 100 | 5% of ₹1000 = ₹50 |

     */
    public BigDecimal calculateTotalExpectedReturn(UserInvestment investment) {
        BigDecimal perPeriodProfit = calculateProfit(investment);
        int periods = investment.getSchema().getTotalReturnPeriods();

        BigDecimal totalReturn = perPeriodProfit.multiply(BigDecimal.valueOf(periods));

        if (investment.getSchema().isCapitalReturned()) {
            totalReturn = totalReturn.add(investment.getInvestedAmount());
        }

        return totalReturn.setScale(4, RoundingMode.HALF_UP);
    }
}
