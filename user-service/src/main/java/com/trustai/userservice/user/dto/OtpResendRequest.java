package com.trustai.userservice.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OtpResendRequest {
    private String sessionId;
}
