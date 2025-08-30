package com.trustai.common.auth.registration;

import com.trustai.common.auth.dto.request.OtpVerifyRequest;
import com.trustai.common.auth.dto.response.AuthResponse;
import com.trustai.common.auth.service.otp.OtpSession;
import com.trustai.common.domain.user.User;

public interface RegistrationService {
    OtpSession createPendingRegistration(RegistrationRequest request);
    AuthResponse completeRegistration(String sessionId, String otp);
    User directRegister(User user, String referralCode);
}
