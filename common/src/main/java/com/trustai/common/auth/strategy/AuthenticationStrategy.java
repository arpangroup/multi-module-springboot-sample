package com.trustai.common.auth.strategy;

import com.trustai.common.auth.dto.request.AuthRequest;

public interface AuthenticationStrategy {/**
     * Start auth. Return some accessToken/response which caller uses to continue flow.
     * For simple username/password flow, this can directly return a username or accessToken
     * that can be exchanged for JWT. For 2-step, it may send OTP and return session id.
     */
    Object start(AuthRequest request);
}
