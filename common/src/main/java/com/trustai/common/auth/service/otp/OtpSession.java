package com.trustai.common.auth.service.otp;

public record OtpSession(String sessionId, String username, String flow) { }