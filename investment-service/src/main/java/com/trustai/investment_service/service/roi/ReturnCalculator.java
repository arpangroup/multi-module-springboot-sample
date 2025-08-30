//package com.trustai.investment_service.service.roi;
//
//import com.trustai.investment_service.entity.InvestmentSchema;
//import com.trustai.investment_service.enums.InterestCalculationType;
//import org.springframework.stereotype.Component;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.Map;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//
//@Component
//public class ReturnCalculator {
//    private final Map<InterestCalculationType, ReturnCalculatorStrategy> strategies;
//
//    public ReturnCalculator(List<ReturnCalculatorStrategy> strategyList) {
//        this.strategies = strategyList.stream()
//                .collect(Collectors.toMap(ReturnCalculatorStrategy::getType, Function.identity()));
//    }
//
//    public BigDecimal calculate(InvestmentSchema schema, BigDecimal amount) {
//        ReturnCalculatorStrategy strategy = strategies.get(schema.getInterestCalculationMethod());
//        if (strategy == null) {
//            throw new UnsupportedOperationException("Unknown return strategy");
//        }
//        return strategy.calculate(schema, amount);
//    }
//}
