package com.trustai.investment_service.enums;

import java.util.Arrays;

public enum ReturnType {
    PERIOD,
    LIFETIME;

    public static ReturnType fromString(String value) {
        return Arrays.stream(ReturnType.values())
                .filter(e -> e.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid schema type: " + value));
    }
}
