package com.trustai.investment_service.validation;

import com.trustai.investment_service.dto.SchemaRequest;
import com.trustai.investment_service.validation.annotations.ValidStakeSchema;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;

public class StakeSchemaValidator implements ConstraintValidator<ValidStakeSchema, SchemaRequest> {

    @Override
    public boolean isValid(SchemaRequest request, ConstraintValidatorContext context) {
        if (request == null) return true;

        boolean isStake = "STAKE".equalsIgnoreCase(request.getInvestmentType());

        if (isStake) {
            boolean isSchemaTypeFixed = "FIXED".equalsIgnoreCase(request.getSchemaType());
            boolean isReturnTypePeriod = "PERIOD".equalsIgnoreCase(request.getReturnType());

            boolean valid = true;
            context.disableDefaultConstraintViolation();

            if (!isSchemaTypeFixed) {
                context.buildConstraintViolationWithTemplate("For STAKE, schemaType must be FIXED")
                        .addPropertyNode("schemaType")
                        .addConstraintViolation();
                valid = false;
            }

            if (!isReturnTypePeriod) {
                context.buildConstraintViolationWithTemplate("For STAKE, returnType must be PERIOD")
                        .addPropertyNode("returnType")
                        .addConstraintViolation();
                valid = false;
            }

            // Check stakePrice not null and greater than zerow hen investmentType == STAKE
            if (request.getStakePrice() == null) {
                context.buildConstraintViolationWithTemplate("stakePrice is mandatory for STAKE investment type")
                        .addPropertyNode("stakePrice")
                        .addConstraintViolation();
                valid = false;
            } else if (request.getStakePrice().compareTo(BigDecimal.ZERO) <= 0) {
                context.buildConstraintViolationWithTemplate("stakePrice must be greater than zero")
                        .addPropertyNode("stakePrice")
                        .addConstraintViolation();
                valid = false;
            }


            return valid;
        }

        return true;
    }
}
