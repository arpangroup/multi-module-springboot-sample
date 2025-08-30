package com.trustai.common.auth.dto;

public record TokenPair(
        String accessToken,
        String refreshToken,
        long accessTokenExpiry,
        long refreshTokenExpiry
) {}
