package com.trustai.common.exceptions;

import com.trustai.common.dto.ErrorField;
import com.trustai.common.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class CommonGlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Value("${spring.profiles.active:local}")
    private String activeProfile;

    // =====================================================================================
    // OVERRIDDEN SPRING HANDLERS
    // =====================================================================================

    @Override
    protected ResponseEntity<Object> handleNoResourceFoundException(
            NoResourceFoundException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        maybePrintStackTrace(ex);
        String traceId = MDC.get("traceId");
        String userId = "1"; // Replace with actual logic
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();

        log.warn("NoResourceFoundException: traceId={}, userId={}, path={}, message={}",
                traceId, userId, path, ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                "Resource not found",
                path
        );

        return new ResponseEntity<>(errorResponse, headers, HttpStatus.NOT_FOUND);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            @NonNull MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {

        maybePrintStackTrace(ex);
        String traceId = MDC.get("traceId");
        String userId = "1";
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        List<ErrorField> errorFields = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ErrorField(error.getField(), error.getDefaultMessage()))
                .toList();

        log.warn("MethodArgumentNotValidException: traceId={}, userId={}, path={}, errors={}",
                traceId, userId, path, errors, ex);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                String.join("; ", errors),
                path
        );
        errorResponse.setErrorFields(errorFields);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        maybePrintStackTrace(ex);
        String traceId = MDC.get("traceId");
        String userId = "1";
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();

        log.warn("HttpMessageNotReadableException: traceId={}, userId={}, path={}, message={}",
                traceId, userId, path, ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "Request body is missing or malformed",
                path
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // =====================================================================================
    // CUSTOM EXCEPTION HANDLERS
    // =====================================================================================

    /*@ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleMalformedJson(HttpMessageNotReadableException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Malformed JSON request");
        body.put("message", ex.getMostSpecificCause().getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }*/

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException ex, HttpServletRequest request) {

        maybePrintStackTrace(ex);
        String traceId = MDC.get("traceId");
        String userId = "1";
        String path = request.getRequestURI();

        Throwable rootCause = ex.getRootCause();
        String message = "Data integrity violation";

        if (rootCause != null && rootCause.getMessage() != null) {
            String causeMessage = rootCause.getMessage();
            if (causeMessage.contains("Duplicate entry")) {
                if (causeMessage.contains("username")) {
                    message = "Username already exists.";
                } else if (causeMessage.contains("referral_code")) {
                    message = "Referral code already exists.";
                } else {
                    message = "Duplicate entry exists.";
                }
            } else if (causeMessage.contains("cannot be null")) {
                message = "Required field is missing.";
            }
        }

        log.warn("DataIntegrityViolationException: traceId={}, userId={}, path={}, rootCauseMessage={}, resolvedMessage={}",
                traceId, userId, path, rootCause != null ? rootCause.getMessage() : "N/A", message, ex);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                "Database Constraint Violation",
                message,
                path
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, HttpServletRequest request) {

        maybePrintStackTrace(ex);
        String traceId = MDC.get("traceId");
        String userId = "1";
        String path = request.getRequestURI();

        log.warn("IllegalArgumentException: traceId={}, userId={}, path={}, message={}",
                traceId, userId, path, ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "IllegalArgument Exception",
                ex.getMessage(),
                path
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            ValidationException ex, WebRequest request) {

        maybePrintStackTrace(ex);
        String traceId = MDC.get("traceId");
        String userId = "1";
        String path = request.getDescription(false);

        log.warn("ValidationException occurred: traceId={}, userId={}, errorCode={}, message={}, path={}",
                traceId, userId, ex.getErrorCode(), ex.getMessage(), path, ex);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                "ValidationException",
                ex.getErrorCode(),
                ex.getMessage(),
                path
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(RestCallException.class)
    public ResponseEntity<ErrorResponse> handleRestCallException(
            RestCallException ex, WebRequest request) {

        maybePrintStackTrace(ex);
        String traceId = MDC.get("traceId");
        String userId = "1";
        String path = request.getDescription(false);

        log.warn("RestCallException: traceId={}, userId={}, message={}, path={}",
                traceId, userId, ex.getMessage(), path, ex);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                "RestCallException",
                ErrorCode.REST_CALL_EXCEPTION,
                ex.getMessage(),
                path
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    // =====================================================================================
    // RUNTIME & GENERIC EXCEPTION HANDLERS
    // =====================================================================================

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(
            RuntimeException ex, WebRequest request) {

        maybePrintStackTrace(ex);
        String traceId = MDC.get("traceId");
        String userId = "1";
        String path = request.getDescription(false);

        log.error("RuntimeException occurred: traceId={}, userId={}, path={}, message={}",
                traceId, userId, path, ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                path
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, WebRequest request) {

        maybePrintStackTrace(ex);
        String traceId = MDC.get("traceId");
        String userId = "1";
        String path = request.getDescription(false);

        log.error("Unhandled Exception occurred: traceId={}, userId={}, path={}, message={}",
                traceId, userId, path, ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Exception",
                ex.getMessage(),
                path
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // =====================================================================================
    // UTILITIES
    // =====================================================================================

    private void maybePrintStackTrace(Exception ex) {
        if ("local".equalsIgnoreCase(activeProfile)) {
            //ex.printStackTrace();
        }
    }

    private ResponseEntity<?> buildResponse(HttpStatus status, String message, String path) {
        Map<String, Object> body = Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "message", message,
                "path", path
        );
        return new ResponseEntity<>(body, new HttpHeaders(), status);
    }
}
