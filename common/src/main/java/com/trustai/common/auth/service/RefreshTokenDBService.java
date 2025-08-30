package com.trustai.common.auth.service;/*
package com.trustai.common.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenDBService {

    private final RefreshTokenRepository repository; // DB or cache store

    */
/**
     * Stores a refresh token along with its expiry timestamp.
     *//*

    public void storeToken(String username, String refreshToken, long expiryMillis) {
        RefreshTokenEntity entity = new RefreshTokenEntity();
        entity.setUsername(username);
        entity.setToken(refreshToken);
        entity.setExpiry(expiryMillis);
        repository.save(entity);
    }

    */
/**
     * Validate refresh token and return record (username + expiry).
     *//*

    public Optional<RefreshTokenRecord> validateAndGetRecord(String refreshToken) {
        return repository.findByToken(refreshToken)
                .filter(token -> token.getExpiry() > System.currentTimeMillis())
                .map(token -> new RefreshTokenRecord(token.getUsername(), token.getExpiry()));
    }

    */
/**
     * Simple DTO record for returning both username and expiry.
     *//*

    public record RefreshTokenRecord(String username, long expiryMillis) {}
}
*/
