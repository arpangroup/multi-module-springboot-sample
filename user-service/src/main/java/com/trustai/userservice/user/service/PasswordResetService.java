package com.trustai.userservice.user.service;

import com.trustai.common.auth.exception.BadCredentialsException;
import com.trustai.common.auth.exception.TooManyOtpAttemptsException;
import com.trustai.common.auth.service.otp.OtpService;
import com.trustai.common.auth.service.otp.OtpSession;
import com.trustai.common.domain.user.User;
import com.trustai.common.exceptions.NotFoundException;
import com.trustai.common.exceptions.RegistrationException;
import com.trustai.common.repository.user.UserRepository;
import com.trustai.userservice.user.entity.PasswordResetAttempt;
import com.trustai.userservice.user.repository.PasswordResetAttemptRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@Slf4j
@RequiredArgsConstructor
public class PasswordResetService {
    private final PasswordResetAttemptRepository attemptRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;

    private static final int MAX_ATTEMPTS_EMAIL = 3;
    private static final int MAX_ATTEMPTS_IP = 5;
    private static final Duration LOCK_DURATION = Duration.ofMinutes(15);

    public OtpSession verifyEmail(String email) {
        log.info("Initiating password reset OTP process for email: {}", email);

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Password reset requested for non-existing email: {}", email);
                    return new NotFoundException("User not found");
                });

        // Create OTP session
        OtpSession session = otpService.createSession(
                user.getEmail(),
                "FORGOT_PASSWORD",
                MAX_ATTEMPTS_EMAIL
        );
        log.debug("Created OTP session: {} for email: {}", session.sessionId(), email);

        // Send OTP (email channel)
        otpService.sendOtp(session, "email");
        log.info("OTP sent for password reset to email {}", email);
        return session;
    }

    public void verifyReset(String email, String sessionId, String otp, String newPassword, String ipAddress) {
        log.info("Verifying OTP for password reset - email: {}, sessionId: {}, IP: {}", email, sessionId, ipAddress);

        PasswordResetAttempt attempt = attemptRepository
                .findByEmailAndIpAddress(email, ipAddress)
                .orElse(new PasswordResetAttempt(email, ipAddress));

        if (attempt.isLocked()) {
            log.warn("Password reset attempt blocked due to too many failed attempts - email: {}, IP: {}", email, ipAddress);
            throw new TooManyOtpAttemptsException("Too many failed attempts. Try again later.");
        }

        boolean otpValid = validateOtp(email, sessionId, otp); // your OTP logic
        if (!otpValid) {
            attempt.incrementAttempts(MAX_ATTEMPTS_EMAIL, LOCK_DURATION);
            attemptRepository.save(attempt);
            log.warn("Invalid OTP attempt for email: {}, IP: {}", email, ipAddress);
            throw new BadCredentialsException("Invalid OTP");
        }

        // ✅ If OTP is valid → reset password
        updatePassword(email, newPassword);
        log.info("Password successfully updated for email: {}", email);

        // ✅ Reset attempts
        attempt.reset();
        attemptRepository.save(attempt);
        log.debug("Reset attempt counter for email: {}, IP: {}", email, ipAddress);
    }

    private boolean validateOtp(String email, String sessionId, String otp) {
        log.debug("Validating OTP for sessionId: {}", sessionId);
        // implement OTP validation (DB or cache lookup)
        if (!otpService.verifyOtp(sessionId, otp)) {
            log.warn("Invalid OTP provided for session: {} (email: {})", sessionId, email);
            throw new RegistrationException("Invalid OTP");
        }
        log.debug("OTP successfully verified for sessionId: {}", sessionId);
        return true;
    }

    private void updatePassword(String email, String newPassword) {
        log.debug("Updating password for email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found during password update - email: {}", email);
                    return new NotFoundException("User not found");
                });
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Password updated in database for email: {}", email);
    }
}
