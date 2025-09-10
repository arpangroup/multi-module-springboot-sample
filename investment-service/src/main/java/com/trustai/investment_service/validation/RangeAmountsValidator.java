package com.trustai.investment_service.validation;

import com.trustai.investment_service.dto.SchemaRequest;
import com.trustai.investment_service.validation.annotations.ValidRangeAmounts;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;

public class RangeAmountsValidator implements ConstraintValidator<ValidRangeAmounts, SchemaRequest> {

    @Override
    public boolean isValid(SchemaRequest request, ConstraintValidatorContext context) {
        if (request == null) return true;

        boolean valid = true;

        if ("RANGE".equalsIgnoreCase(request.getSchemaType())) {
            BigDecimal min = request.getMinimumInvestmentAmount();
            BigDecimal max = request.getMaximumInvestmentAmount();

            context.disableDefaultConstraintViolation();

            if (min == null) {
                context.buildConstraintViolationWithTemplate("Minimum investment amount must not be null for RANGE schema")
                        .addPropertyNode("minimumInvestmentAmount")
                        .addConstraintViolation();
                valid = false;
            }

            if (max == null) {
                context.buildConstraintViolationWithTemplate("Maximum investment amount must not be null for RANGE schema")
                        .addPropertyNode("maximumInvestmentAmount")
                        .addConstraintViolation();
                valid = false;
            }

            if (min != null && max != null) {
                if (min.compareTo(max) >= 0) {
                    context.buildConstraintViolationWithTemplate("Minimum investment amount must be less than maximum for RANGE schema")
                            .addPropertyNode("minimumInvestmentAmount")
                            .addConstraintViolation();
                    valid = false;
                }
            }
        }

        return valid;
    }
}