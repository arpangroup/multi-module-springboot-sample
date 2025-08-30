package com.trustai.common.auth.service;

import com.trustai.common.security.jwt.JwtProvider;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RefreshTokenService {
    private final Map<String, RefreshTokenRecord> refreshTokenStore = new ConcurrentHashMap<>();
    private final JwtProvider jwtProvider;

    public RefreshTokenService(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    public void storeToken(String username, String refreshToken, long expiryMillis) {
        refreshTokenStore.put(refreshToken, new RefreshTokenRecord(username, expiryMillis));
    }

    /**
     * Old behavior — still available if you just need username.
     */
    public Optional<String> validateAndGetUsername(String refreshToken) {
        RefreshTokenRecord record = refreshTokenStore.get(refreshToken);

        if (record == null || System.currentTimeMillis() > record.expiryMillis()) {
            return Optional.empty();
        }
        return Optional.of(record.username());

        /*if (!jwtProvider.validateToken(refreshToken)) {
            return Optional.empty();
        }
        String username = jwtProvider.extractUsername(refreshToken);
        if (!refreshTokenStore.containsKey(refreshToken)) {
            return Optional.empty();
        }
        return Optional.of(username);*/
    }

    /**
     * New method — returns both username and expiry without resetting the expiry.
     */
    public Optional<RefreshTokenRecord> validateAndGetRecord(String refreshToken) {
        RefreshTokenRecord record = refreshTokenStore.get(refreshToken);

        if (record == null || System.currentTimeMillis() > record.expiryMillis()) {
            return Optional.empty();
        }
        return Optional.of(record);
    }

    public void invalidate(String refreshToken) {
        refreshTokenStore.remove(refreshToken);
    }
    public record RefreshTokenRecord(String username, long expiryMillis) {}
}
