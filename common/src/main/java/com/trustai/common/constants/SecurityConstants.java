package com.trustai.common.constants;

import java.time.Duration;
import java.time.LocalDateTime;

public class SecurityConstants {

    private static final long VALIDITY_1_HOUR   = 3600_000; // 60 * 60 * 1000
    private static final long VALIDITY_1_MINUTE = 60_000;   // 1 * 60 * 1000
    private static final long VALIDITY_2_MINUTE = 120_000;   // 2 * 60 * 1000
    public static final Duration OTP_EXPIRY = Duration.ofMinutes(15);

    public static final int MAX_OTP_ATTEMPTS = 3;
    public static final int OTP_LENGTH = 6;
    public static final long OTP_TTL_MILLIS = 5 * 60 * 1000;  // 5 minutes
//    public static final long ACCESS_TOKEN_VALIDITY_MS  = 30 * 60 * 1000; // 30 minutes
    public static final long ACCESS_TOKEN_VALIDITY_MS  = 5 * 60 * 60 * 1000; // 5hr
    public static final long REFRESH_TOKEN_VALIDITY_MS = 60 * 60 * 1000; // 60 minutes
    public static final int LOCK_DURATION_MINUTES = 15;



    public static boolean isExpired(LocalDateTime createdAt) {
        //return createdAt.plus(SecurityConstants.OTP_EXPIRY).isBefore(LocalDateTime.now());

        LocalDateTime expiryTime = createdAt.plus(Duration.ofMillis(OTP_TTL_MILLIS));
        return expiryTime.isBefore(LocalDateTime.now());
    }
}
