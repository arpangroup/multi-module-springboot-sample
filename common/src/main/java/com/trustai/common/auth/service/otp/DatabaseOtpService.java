package com.trustai.common.auth.service.otp;

import com.trustai.common.auth.entity.OtpSessionEntity;
import com.trustai.common.auth.exception.TooManyOtpAttemptsException;
import com.trustai.common.auth.repository.OtpSessionRepository;
import com.trustai.common.constants.SecurityConstants;
import com.trustai.common.dto.NotificationRequest;
import com.trustai.common.event.NotificationEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DatabaseOtpService implements OtpService {
    private final OtpSessionRepository repository;
    private final SecureRandom random = new SecureRandom();
    private final ApplicationEventPublisher eventPublisher;


    @Override
    public OtpSession createSession(String username, String flow, int maxAttempts) {
        String sessionId = UUID.randomUUID().toString();
        String otp = generateOtp();

        OtpSessionEntity entity = new OtpSessionEntity(
                sessionId,
                username,
                flow,
                otp,
                Instant.now().toEpochMilli()
        );

        repository.save(entity);
        log.info("Created OTP session {} for user {}", sessionId, username);
        return new OtpSession(sessionId, username, flow);
    }

    @Override
    public void sendOtp(OtpSession session, String channel) {
        OtpSessionEntity entity = repository.findById(session.sessionId())
                .orElseThrow(() -> new IllegalStateException("OTP session not found"));

        System.out.printf("Sending OTP %s to %s via %s%n", entity.getOtp(), entity.getUsername(), channel);

        NotificationRequest notificationRequest = NotificationRequest.forEmail(
                String.valueOf(entity.getUsername()),
                "Verification Code from TrustAI",
                "Please enter the verification code to verify your account.\n" +
                        "<b>" + entity.getOtp() + "</b>"
        );

        publishOTP(notificationRequest);
    }

    @Override
    @Transactional
    public boolean verifyOtp(String sessionId, String otp) {
        OtpSessionEntity entity = repository.findById(sessionId)
                .orElseThrow(() -> new IllegalStateException("OTP session not found"));

        if (!entity.isValid() || isExpired(entity)) {
            invalidateSession(sessionId);
            log.warn("OTP verification failed: session {} expired or invalid", sessionId);
            return false;
        }

        boolean matched = entity.getOtp().equals(otp);

        if (matched) {
            log.info("OTP verified successfully for session {}", sessionId);
            // Optionally invalidate session or clear OTP after successful verification here
             invalidateSession(sessionId);
        } else {
            log.warn("OTP verification failed: incorrect OTP for session {}", sessionId);
        }

        return matched;
    }

    @Override
    @Transactional
    public void incrementAttempts(String sessionId, int maxAllowed) {
        OtpSessionEntity entity = repository.findById(sessionId)
                .orElseThrow(() -> new IllegalStateException("OTP session not found"));

        int current = entity.getAttempts() + 1;
        entity.setAttempts(current);

        if (current > maxAllowed) {
            int lockDurationMinutes = SecurityConstants.LOCK_DURATION_MINUTES;
            long lockUntilMillis = Instant.now()
                    .plusSeconds(lockDurationMinutes * 60L)
                    .toEpochMilli();

            entity.setValid(false);
            entity.setLockedUntil(lockUntilMillis);
            repository.save(entity);

            log.warn("OTP session {} locked for {} minutes", sessionId, lockDurationMinutes);
            throw new TooManyOtpAttemptsException(lockDurationMinutes);
        }

        repository.save(entity);
    }

    @Override
    public Optional<OtpSession> getSession(String sessionId) {
        return repository.findById(sessionId)
                .filter(entity -> !isSessionLockedOrInvalid(entity))
                .map(entity -> new OtpSession(sessionId, entity.getUsername(), entity.getFlow()));
    }

    @Override
    public Optional<OtpSession> getSessionByUsername(String username) {
        return repository.findAllByUsernameOrderByCreatedAtDesc(username).stream()
                .filter(entity -> !isSessionLockedOrInvalid(entity))
                .findFirst()
                .map(entity -> new OtpSession(entity.getSessionId(), entity.getUsername(), entity.getFlow()));
    }

    @Override
    @Transactional
    public void invalidateSession(String sessionId) {
        repository.findById(sessionId).ifPresent(entity -> {
            entity.setValid(false);
            repository.save(entity);
            log.info("Invalidated OTP session {}", sessionId);
        });
    }

    private boolean isSessionLockedOrInvalid(OtpSessionEntity entity) {
        long now = Instant.now().toEpochMilli();

        if (entity.getLockedUntil() != null && entity.getLockedUntil() > now) {
            log.warn("OTP session {} locked until {}", entity.getSessionId(), entity.getLockedUntil());
            return true;
        }

        if (!entity.isValid() || isExpired(entity)) {
            invalidateSession(entity.getSessionId());
            return true;
        }

        return false;
    }

    private boolean isExpired(OtpSessionEntity entity) {
        Instant created = Instant.ofEpochMilli(entity.getCreatedAt());
        Instant expiryTime = created.plusMillis(SecurityConstants.OTP_TTL_MILLIS);
        return Instant.now().isAfter(expiryTime);
    }

    private String generateOtp() {
        int bound = (int) Math.pow(10, SecurityConstants.OTP_LENGTH);
        int code = random.nextInt(bound);
        return String.format("%0" + SecurityConstants.OTP_LENGTH + "d", code);
    }

    @Async
    private void publishOTP(NotificationRequest request) {
        log.info("🔔 [Async OTP Publish] Triggered for user: {}, channel: {}, destination: {}",
                request.getRecipient(),
                request.getChannels(),
                request.getRecipient());
        eventPublisher.publishEvent(new NotificationEvent(this, request));
    }
}
