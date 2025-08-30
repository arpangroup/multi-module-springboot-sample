package com.trustai.common.auth.service.otp;

public interface OtpSender {
    void sendOtp(String to, String otp);
}
