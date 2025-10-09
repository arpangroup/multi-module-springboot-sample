package com.trustai.investment_service.validation.annotations;

import com.trustai.investment_service.validation.RangeAmountsValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = RangeAmountsValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRangeAmounts {
    String message() default "Invalid investment amount range for schemaType RANGE";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}