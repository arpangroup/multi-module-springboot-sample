package com.trustai.common.auth.dto.request;

public record AuthRequest(String username, String password, String flow) {}