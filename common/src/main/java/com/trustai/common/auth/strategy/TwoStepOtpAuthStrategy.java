package com.trustai.common.auth.strategy;

import com.trustai.common.auth.dto.request.AuthRequest;
import com.trustai.common.auth.dto.response.OtpChallengeResponse;
import com.trustai.common.auth.service.otp.OtpService;
import com.trustai.common.security.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

@Component("password+otp")
@RequiredArgsConstructor
public class TwoStepOtpAuthStrategy implements AuthenticationStrategy {
    private final AuthenticationManager authenticationManager;
    private final OtpService otpService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    public Object start(AuthRequest request) {
        // 1) validate username/password first (or you may skip password verification for passwordless OTP)
        var token = new UsernamePasswordAuthenticationToken(request.username(), request.password());
        var auth  = authenticationManager.authenticate(token);

        //  Step 2: Create OTP session (flow = "password+otp", maxAttempts = 3)
        var otpSession = otpService.createSession(auth.getName(), "password+otp", 3);

        /// Step 3: Send OTP (e.g., email/SMS)
        otpService.sendOtp(otpSession, "email"); // You could choose "sms" here too

        /*
        // 4) generate and send OTP to user's preferred destination (email or mobile)
        var user = (com.trustai.common.security.service.CustomUserDetails) userDetailsService.loadUserByUsername(request.getUsername());
        String destination = user.getEmail(); // or mobile if prefer mobile
        String sessionId = otpService.generateAndSend(request.getUsername(), destination);
        return sessionId;
         */

        // Step 4: Return session info to client
        return new OtpChallengeResponse(
                otpSession.sessionId(),
                "OTP sent to your registered contact."
        );


    }
}
