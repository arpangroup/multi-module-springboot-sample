package com.trustai.common.auth.service;

import com.trustai.common.auth.dto.TokenPair;
import com.trustai.common.auth.dto.request.AuthRequest;
import com.trustai.common.auth.dto.response.AuthResponse;
import com.trustai.common.auth.exception.BadCredentialsException;
import com.trustai.common.auth.exception.UnsupportedAuthFlowException;
import com.trustai.common.auth.service.otp.OtpService;
import com.trustai.common.auth.service.otp.OtpSession;
import com.trustai.common.auth.strategy.AuthenticationStrategy;
import com.trustai.common.constants.SecurityConstants;
import com.trustai.common.security.jwt.JwtProvider;
import com.trustai.common.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final Map<String, AuthenticationStrategy> strategies; // injected by Spring: bean name -> impl
    private final OtpService otpService;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    /**
     * Step 1: Start authentication flow (password, passwordless, etc.)
     */
    public Object startAuth(AuthRequest req) {
        String flow = req.flow() == null ? "password" : req.flow();
        AuthenticationStrategy strategy  = strategies.get(flow);
        if (strategy  == null) throw new UnsupportedAuthFlowException(flow);

        // Strategy returns either AuthResponse (immediate login) or a DTO (multi-step flow)
        return strategy.start(req);
    }

    /**
     * Step 2: Verify OTP and issue JWT + refresh accessToken (for passwordless or OTP login)
     */
    public AuthResponse verifyOtpAndIssueToken(String sessionId, String otp) {
        if (StringUtils.isBlank(sessionId)) throw new BadCredentialsException("Invalid sessionId");

        OtpSession session = otpService.getSession(sessionId)
                .orElseThrow(() -> new BadCredentialsException("Invalid or expired OTP session"));

        if (!otpService.verifyOtp(sessionId, otp)) {
            otpService.incrementAttempts(sessionId, SecurityConstants.MAX_OTP_ATTEMPTS);
            throw new BadCredentialsException("Invalid OTP");
        }

        TokenPair tokens = jwtProvider.generateToken(session.username());
        refreshTokenService.storeToken(session.username(), tokens.refreshToken(), tokens.refreshTokenExpiry());
        otpService.invalidateSession(sessionId);

        return new AuthResponse(
                tokens.accessToken(),
                tokens.refreshToken(),
                tokens.accessTokenExpiry(),
                tokens.refreshTokenExpiry()
        );
    }

    /**
     * Issue accessToken without OTP (used in password login flow)
     */
    public AuthResponse issueTokenForUsername(String username) {
        TokenPair tokens = jwtProvider.generateToken(username);
        refreshTokenService.storeToken(username, tokens.refreshToken(), tokens.refreshTokenExpiry());
        return new AuthResponse(
                tokens.accessToken(),
                tokens.refreshToken(),
                tokens.accessTokenExpiry(),
                tokens.refreshTokenExpiry()
        );
    }

    /**
     * Refresh access token using a refresh token (rotating refresh token)
     */
    public AuthResponse refreshAccessToken__old(String refreshToken) {
        var record = refreshTokenService.validateAndGetRecord(refreshToken)
                .orElseThrow(() -> new BadCredentialsException("Invalid or expired refresh token"));

        String newAccessToken = jwtProvider.generateAccessToken(record.username());
        long newAccessExpiry = System.currentTimeMillis() + SecurityConstants.ACCESS_TOKEN_VALIDITY_MS;

        // Return both access + refresh + expiry times
        return new AuthResponse(
                newAccessToken,
                refreshToken, // existing refresh token
                newAccessExpiry,
                record.expiryMillis() // original refresh token expiry
        );
    }

    /**
     * Refresh access token using a refresh token (rotating refresh token)
     * The old refresh token is invalidated immediately.
     * A new refresh token (and expiry) is issued alongside the new access token.
     * Even if someone steals an old refresh token, it won’t work after it’s used once.
     */
    public AuthResponse refreshAccessToken(String refreshToken) {
        var record = refreshTokenService.validateAndGetRecord(refreshToken)
                .orElseThrow(() -> new BadCredentialsException("Invalid or expired refresh token"));

        // Invalidate old refresh token immediately
        refreshTokenService.invalidate(refreshToken);

        // Generate new token pair
        TokenPair tokens = jwtProvider.generateToken(record.username());

        // Store new refresh token
        refreshTokenService.storeToken(record.username(), tokens.refreshToken(), tokens.refreshTokenExpiry());

        // Return new access + refresh tokens with their expiry times
        return new AuthResponse(
                tokens.accessToken(),
                tokens.refreshToken(),
                tokens.accessTokenExpiry(),
                tokens.refreshTokenExpiry()
        );
    }
}
