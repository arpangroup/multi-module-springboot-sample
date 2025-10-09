package com.trustai.userservice.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(
        @NotBlank(message = "Session ID must not be blank")
        String sessionId,

        @NotBlank(message = "Email must not be blank")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "OTP must not be blank")
        String otp,

        @NotBlank(message = "Password must not be blank")
        String password
) {}