package com.trustai.income_service.referral.calculator;

import com.trustai.common.enums.CalculationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class BonusAmountCalculatorFactory {
    private final Map<CalculationType, BonusAmountCalculator> calculators;

    @Autowired
    public BonusAmountCalculatorFactory(List<BonusAmountCalculator> calculatorList) {
        this.calculators = calculatorList.stream().collect(Collectors.toMap(BonusAmountCalculator::getType, c -> c));
    }

    public BonusAmountCalculator getCalculator(CalculationType type) {
        return calculators.get(type);
    }
}
