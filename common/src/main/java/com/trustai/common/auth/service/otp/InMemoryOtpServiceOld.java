package com.trustai.common.auth.service.otp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InMemoryOtpServiceOld {
    record Entry(String otp, Instant expiresAt, String username) { }
    private final Map<String, Entry> store = new ConcurrentHashMap<>();
    private final OtpSender sender;
    private final int ttlSec;
    private final int length;

    public InMemoryOtpServiceOld(OtpSender sender, @Value("${otp.ttl-seconds}") int ttlSec, @Value("${otp.length}") int length) {
        this.sender = sender;
        this.ttlSec = ttlSec;
        this.length = length;
    }

    public String generateAndSend(String username, String destination) {
        String otp = generateNumericOtp(length);
        String sessionId = UUID.randomUUID().toString();
        store.put(sessionId, new Entry(otp, Instant.now().plusSeconds(ttlSec), username));
        sender.sendOtp(destination, otp);
        return sessionId;
    }

    public boolean verifyOtp(String sessionId, String otp) {
        Entry e = store.get(sessionId);
        if (e == null) return false;
        if (Instant.now().isAfter(e.expiresAt())) {
            store.remove(sessionId);
            return false;
        }
        boolean ok = e.otp().equals(otp);
        if (ok) store.remove(sessionId);
        return ok;
    }

    public String getUsernameForSession(String sessionId) {
        Entry e = store.get(sessionId);
        return e == null ? null : e.username();
    }

    private String generateNumericOtp(int len) {
        var sb = new StringBuilder();
        for (int i = 0; i < len; i++) sb.append((int)(Math.random()*10));
        return sb.toString();
    }

}
