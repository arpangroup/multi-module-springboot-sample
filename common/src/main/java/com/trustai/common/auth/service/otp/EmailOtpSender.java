package com.trustai.common.auth.service.otp;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class EmailOtpSender implements OtpSender {
    // inject MailService

    @Override
    public void sendOtp(String to, String otp) {
        // integrate with mail service
        System.out.println("============================================");
        System.out.println("Sending OTP " + otp + " to email: " + to);
        System.out.println("============================================");
    }
}
