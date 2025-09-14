package com.trustai.common.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ApiResponse <T> {
    private String message;
    private boolean success;
    private T data;

    public ApiResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    public ApiResponse(T data, String message, boolean success) {
        this(message, success);
        this.data = data;
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(message, true);
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(data, message, true);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(message, false);
    }

}
