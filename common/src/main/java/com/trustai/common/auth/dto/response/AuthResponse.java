package com.trustai.common.auth.dto.response;


public record AuthResponse(
        String accessToken,
        String refreshToken,
        long accessTokenExpiry,
        long refreshTokenExpiry
) {}
