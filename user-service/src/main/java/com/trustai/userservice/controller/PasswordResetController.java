package com.trustai.userservice.controller;

import com.trustai.common.auth.service.otp.OtpSession;
import com.trustai.common.dto.ApiResponse;
import com.trustai.common.utils.IpUtils;
import com.trustai.userservice.user.dto.ForgotPasswordRequest;
import com.trustai.userservice.user.dto.ResetPasswordRequest;
import com.trustai.userservice.user.service.PasswordResetService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/password")
@RequiredArgsConstructor
@Slf4j
public class PasswordResetController {
    private final PasswordResetService passwordResetService;

    @PostMapping("/forgot")
    public ResponseEntity<ApiResponse<?>> sendVerificationCode(@RequestBody @Valid ForgotPasswordRequest req) {
        log.info("Received password reset request for email: {}", req.email());
        try {
            OtpSession otpSession = passwordResetService.verifyEmail(req.email());
            log.info("Verification code sent successfully to email: {}", req.email());
            return ResponseEntity.ok(ApiResponse.success(otpSession, "Verification code sent to your email"));
        } catch (Exception e) {
            log.error("Error while sending verification code to email: {}", req.email(), e);
            throw e; // Let global exception handler manage the response
        }
    }

    @PostMapping("/reset")
    public ResponseEntity<ApiResponse<?>> resetPassword(@RequestBody @Valid ResetPasswordRequest request, HttpServletRequest httpRequest) {
        log.info("Received password reset attempt for email: {} ", request.email());
        String clientIp = IpUtils.getClientIp(httpRequest);

        try {
            passwordResetService.verifyReset(
                    request.email(),
                    request.sessionId(),
                    request.otp(),
                    request.password(),
                    clientIp
            );
            log.info("Password reset successful for email: {}", request.email());
            return ResponseEntity.ok(ApiResponse.success("Password reset successfully"));
        } catch (Exception e) {
            log.error("Password reset failed for email: {} from IP: {}", request.email(), clientIp, e);
            throw e; // Let global exception handler manage the response
        }
    }
}
