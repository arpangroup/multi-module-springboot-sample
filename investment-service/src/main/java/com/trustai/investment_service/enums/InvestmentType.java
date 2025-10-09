package com.trustai.investment_service.enums;

import java.util.Arrays;

public enum InvestmentType {
    STANDARD,
    STAKE,
    PROMO;

    public static InvestmentType fromString(String value) {
        return Arrays.stream(InvestmentType.values())
                .filter(e -> e.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid schema type: " + value));
    }
}
