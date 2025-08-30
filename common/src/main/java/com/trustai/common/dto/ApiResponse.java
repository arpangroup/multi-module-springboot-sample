package com.trustai.common.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ApiResponse {
    private String message;
    private boolean success;

    public ApiResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    public static ApiResponse success(String message) {
        return new ApiResponse(message, true);
    }

    public static ApiResponse error(String message) {
        return new ApiResponse(message, false);
    }

}
