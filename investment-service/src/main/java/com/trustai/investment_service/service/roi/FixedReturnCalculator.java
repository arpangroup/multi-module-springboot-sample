//package com.trustai.investment_service.service.roi;
//
//import com.trustai.investment_service.entity.InvestmentSchema;
//import com.trustai.investment_service.enums.InterestCalculationType;
//import org.springframework.stereotype.Component;
//
//import java.math.BigDecimal;
//
//@Component
//public class FixedReturnCalculator implements ReturnCalculatorStrategy {
//    @Override
//    public BigDecimal calculate(InvestmentSchema schema, BigDecimal amount) {
//        return schema.getReturnRate().multiply(BigDecimal.valueOf(schema.getTotalReturnPeriods()));
//    }
//
//    @Override
//    public InterestCalculationType getType() {
//        return InterestCalculationType.FLAT;
//    }
//}
