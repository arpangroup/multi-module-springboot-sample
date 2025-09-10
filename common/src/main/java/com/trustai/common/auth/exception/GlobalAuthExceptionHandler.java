package com.trustai.common.auth.exception;

import com.trustai.common.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
@Order(1)
//@RestControllerAdvice(basePackages = "com.trustai.common")
public class GlobalAuthExceptionHandler {

    @ExceptionHandler({BadCredentialsException.class, UnsupportedAuthFlowException.class})
    public ResponseEntity<Map<String, Object>> handleAuthExceptions(RuntimeException ex) {
        String errorType = (ex instanceof BadCredentialsException)
                ? "Bad credential"
                : "Unsupported authentication flow";

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", errorType);
        body.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(TooManyOtpAttemptsException.class)
    public ResponseEntity<ErrorResponse> handleTooManyOtpAttempts(TooManyOtpAttemptsException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(
                new ErrorResponse(
                        HttpStatus.TOO_MANY_REQUESTS.value(),
                        "Too many OTP attempts",
                        null,
                        ex.getMessage(),
                        request.getDescription(false)
                )
        );
    }

    @ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class})
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleAccessDeniedExceptions(Exception ex, WebRequest request) {
        String traceId = Optional.ofNullable(MDC.get("traceId")).orElse("N/A");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = "anonymous";
        List<String> roles = Collections.emptyList();

        if (authentication != null && authentication.isAuthenticated()) {
            userId = authentication.getName();
            roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
        }

        String path = ((ServletWebRequest) request).getRequest().getRequestURI();

        log.warn("Access denied. TraceId={}, UserId={}, Roles={}, Path={}, Message={}",
                traceId, userId, roles, path, ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "Access Denied",
                "You do not have permission to access this resource.",
                path
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }
}
