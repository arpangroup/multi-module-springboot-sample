package com.trustai.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Data
@Getter
@NoArgsConstructor
public class ErrorResponse {
    private Instant timestamp;
    private int status;
    private String error;
    private String errorCode;
    private String message;
    private String path;

    @JsonInclude(JsonInclude.Include.NON_NULL) // Only this field will be excluded if null
    @Setter
    private List<ErrorField> errorFields = null;

    public ErrorResponse(int status, String error, String message, String path) {
        this.timestamp = Instant.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public ErrorResponse(int status, String error, String errorCode, String message, String path) {
        this(status, error, message, path);
        this.errorCode = errorCode;
    }
}
