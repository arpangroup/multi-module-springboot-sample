package com.trustai.userservice.user.registration;

import com.trustai.common.auth.dto.response.AuthResponse;
import com.trustai.common.auth.service.otp.OtpSession;
import com.trustai.common.domain.user.User;

public interface RegistrationService {
    OtpSession createPendingRegistration(RegistrationRequest request);
    AuthResponse completeRegistration(String sessionId, String otp);
    User directRegister(User user, String referralCode);

    void resendOtp(String sessionId);
}
