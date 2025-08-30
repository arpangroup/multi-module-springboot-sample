package com.trustai.common.auth.service.otp;

import java.util.Optional;

public interface OtpService {
    OtpSession createSession(String username, String flow, int maxAttempts);
    void sendOtp(OtpSession session, String channel);
    boolean verifyOtp(String sessionId, String otp);
    void incrementAttempts(String sessionId, int maxAllowed);
    Optional<OtpSession> getSession(String sessionId);
    Optional<OtpSession> getSessionByUsername(String username);
    void invalidateSession(String sessionId);
}
