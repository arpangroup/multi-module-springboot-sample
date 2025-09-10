package com.trustai.investment_service.validation;

import com.trustai.investment_service.validation.annotations.EnumValue;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EnumValueValidator implements ConstraintValidator<EnumValue, String> {

    private Class<? extends Enum<?>> enumClass;

    @Override
    public void initialize(EnumValue constraintAnnotation) {
        this.enumClass = constraintAnnotation.enumClass();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true; // Leave null check to @NotBlank if needed

        Object[] enumValues = this.enumClass.getEnumConstants();

        for (Object enumVal : enumValues) {
            if (value.equalsIgnoreCase(((Enum<?>) enumVal).name())) {
                return true;
            }
        }

        return false;
    }
}