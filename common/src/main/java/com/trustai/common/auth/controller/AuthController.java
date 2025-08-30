package com.trustai.common.auth.controller;

import com.trustai.common.auth.dto.*;
import com.trustai.common.auth.dto.request.AuthRequest;
import com.trustai.common.auth.dto.request.OtpVerifyRequest;
import com.trustai.common.auth.dto.response.AuthResponse;
import com.trustai.common.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
/*
POST /api/auth/accessToken     → starts auth (password or passwordless)
POST /api/auth/verify-otp      → verifies OTP
POST /api/auth/refresh         → refresh token

More REST-friendly & self-explanatory naming could be:

POST /api/auth/login           → starts authentication (password or passwordless)
POST /api/auth/otp/verify      → verifies OTP
POST /api/auth/token/refresh   → refresh token
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;

    /**
     * Start authentication. Returns either:
     * - For password flow: returns username (or directly accessToken in other design) — here we return accessToken for simplicity.
     * - For 2-step: returns sessionId which client must use with /verify-otp.
     */
    @PostMapping("/accessToken") // accessToken
    public ResponseEntity<?> token(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.startAuth(request));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<AuthResponse> verifyOtp(@RequestBody OtpVerifyRequest req) {
        return ResponseEntity.ok(authService.verifyOtpAndIssueToken(req.getSessionId(), req.getOtp()));
    }

    /**
     * Refresh token endpoint: given refreshToken, issue a new access token.
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(authService.refreshAccessToken(refreshToken));
    }






    //  Rate Limiting (Mandatory)
    // GET /api/auth/availability?username=arpan&email=arpan@example.com
    //@RateLimiter(name = "checkAvailability")
    @GetMapping("/availability")
    public ResponseEntity<AvailabilityResponse> availability(
            @RequestParam("username") String username,
            @RequestParam("email") String email) {
        return ResponseEntity.ok(new AvailabilityResponse());

    }
}
