package com.trustai.investment_service.validation.annotations;

import com.trustai.investment_service.validation.StakeSchemaValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = StakeSchemaValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidStakeSchema {
    String message() default "For STAKE, schemaType must be FIXED and returnType must be PERIOD";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
