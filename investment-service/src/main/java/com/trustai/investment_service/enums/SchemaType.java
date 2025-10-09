package com.trustai.investment_service.enums;

import java.util.Arrays;

public enum SchemaType {
    FIXED, // Fixed interest or terms
    RANGE; // Variable based on amount/period

    public static SchemaType fromString(String value) {
        return Arrays.stream(SchemaType.values())
                .filter(e -> e.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid schema type: " + value));
    }
}
