package com.trustai.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class AuthEntryPoint implements AuthenticationEntryPoint {
    @Autowired
    ObjectMapper objectMapper;


    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException ex) throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        String errorMessage = resolveErrorMessage(ex);

        Map<String, Object> body = Map.of(
                "status", 401,
                "error", "Unauthorized",
                "message", errorMessage,
                "path", request.getRequestURI()
        );
        //response.getWriter().write("{\"error\": \"" + errorMessage + "\"}");
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }

    private String resolveErrorMessage(AuthenticationException ex) {
        if (ex instanceof DisabledException) {
            return "Account is blocked";
        } else if (ex instanceof BadCredentialsException) {
            return "Invalid username or password";
        } else if (ex instanceof LockedException) {
            return "Your account is locked.";
        } else if (ex instanceof CredentialsExpiredException) {
            return "Your credentials have expired.";
        }
        return "Authentication failed";
    }
}
