package com.trustai.aggregator.dto;

public record AppConfigUpdateRequest(
        String key,
        String value
) {
}
