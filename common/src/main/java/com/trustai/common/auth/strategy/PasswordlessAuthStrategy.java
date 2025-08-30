package com.trustai.common.auth.strategy;

import com.trustai.common.auth.dto.request.AuthRequest;
import com.trustai.common.auth.exception.BadCredentialsException;
import com.trustai.common.auth.dto.response.OtpChallengeResponse;
import com.trustai.common.auth.service.otp.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("passwordless")
@RequiredArgsConstructor
public class PasswordlessAuthStrategy implements AuthenticationStrategy {

    private final OtpService otpService;

    @Override
    public Object start(AuthRequest req) {
        if (req.username() == null || req.username().isBlank()) {
            throw new BadCredentialsException("Username (or email/phone) is required for passwordless login");
        }

        // Step 1: Create OTP session (max 3 attempts)
        var otpSession = otpService.createSession(req.username(), "passwordless", 3);

        // Step 2: Send OTP
        otpService.sendOtp(otpSession, "email"); // or "sms"


        /*
        String usernameOrEmail = req.getUsername();
        otpService.sendOtp(usernameOrEmail, sessionId);
        return Map.of("message", "OTP sent", "sessionId", sessionId);
         */

        // Step 3: Return challenge response
        return new OtpChallengeResponse(
                otpSession.sessionId(),
                "OTP sent to your registered contact."
        );

    }
}
