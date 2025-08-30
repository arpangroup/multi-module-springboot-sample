package com.trustai.common.auth.service.otp;/*
package com.trustai.common.auth.service.otp;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RedisOtpService implements OtpService {

    private final StringRedisTemplate redisTemplate;
    private final SecureRandom random = new SecureRandom();

    // Configurable
    private static final int OTP_LENGTH = 6;
    private static final Duration OTP_TTL = Duration.ofMinutes(5);

    private static final String SESSION_KEY_PREFIX = "otp:session:";
    private static final String ATTEMPTS_KEY_PREFIX = "otp:attempts:";

    @Override
    public OtpSession createSession(String username, String flow, int maxAttempts) {
        String sessionId = UUID.randomUUID().toString();
        String otp = generateOtp();

        // Store OTP in Redis
        String key = SESSION_KEY_PREFIX + sessionId;
        String value = username + ":" + otp + ":" + flow;
        redisTemplate.opsForValue().set(key, value, OTP_TTL);

        // Init attempts counter
        redisTemplate.opsForValue().set(ATTEMPTS_KEY_PREFIX + sessionId, "0", OTP_TTL);

        return new OtpSession(sessionId, username, flow);
    }

    @Override
    public void sendOtp(OtpSession session, String channel) {
        // For real-world: integrate with Email/SMS service here
        System.out.printf("Sending OTP to %s via %s%n", session.username(), channel);
        String otp = getOtpForSession(session.sessionId())
                .orElseThrow(() -> new IllegalStateException("OTP not found"));
        System.out.printf("OTP is: %s%n", otp); // For debugging only — remove in prod
    }

    @Override
    public boolean verifyOtp(String sessionId, String otp) {
        Optional<String> storedOtp = getOtpForSession(sessionId);
        return storedOtp.isPresent() && storedOtp.get().equals(otp);
    }

    @Override
    public void incrementAttempts(String sessionId, int maxAllowed) {
        String key = ATTEMPTS_KEY_PREFIX + sessionId;
        Long attempts = redisTemplate.opsForValue().increment(key);

        if (attempts != null && attempts > maxAllowed) {
            invalidateSession(sessionId);
            throw new RuntimeException("Too many OTP attempts");
        }
    }

    @Override
    public Optional<OtpSession> getSession(String sessionId) {
        String key = SESSION_KEY_PREFIX + sessionId;
        String value = redisTemplate.opsForValue().get(key);

        if (value == null) return Optional.empty();

        String[] parts = value.split(":");
        if (parts.length < 3) return Optional.empty();

        return Optional.of(new OtpSession(sessionId, parts[0], parts[2]));
    }

    @Override
    public void invalidateSession(String sessionId) {
        redisTemplate.delete(SESSION_KEY_PREFIX + sessionId);
        redisTemplate.delete(ATTEMPTS_KEY_PREFIX + sessionId);
    }

    private Optional<String> getOtpForSession(String sessionId) {
        String key = SESSION_KEY_PREFIX + sessionId;
        String value = redisTemplate.opsForValue().get(key);
        if (value == null) return Optional.empty();
        String[] parts = value.split(":");
        return parts.length >= 2 ? Optional.of(parts[1]) : Optional.empty();
    }

    private String generateOtp() {
        int bound = (int) Math.pow(10, OTP_LENGTH);
        int code = random.nextInt(bound);
        return String.format("%0" + OTP_LENGTH + "d", code);
    }
}
*/
