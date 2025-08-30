package com.trustai.common.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {

        String errorMessage;
        if (exception instanceof DisabledException) {
            errorMessage = "Account is blocked";
        } else if (exception instanceof BadCredentialsException) {
            errorMessage = "Invalid username or password";
        }else if (exception instanceof LockedException) {
            errorMessage = "Your account is locked.";
        } else if (exception instanceof CredentialsExpiredException) {
            errorMessage = "Your credentials have expired.";
        } else {
            errorMessage = "Authentication failed";
        }

        // Check if it's an API request
        String requestUri = request.getRequestURI();
        boolean isApiRequest = requestUri.startsWith("/api/") ||
                "application/json".equalsIgnoreCase(request.getHeader("Accept"));

        if (isApiRequest) {
            // Return JSON for API calls
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"" + errorMessage + "\"}");
        } else {
            // Redirect for form login
            // For form login, store message in session so it survives redirect
            request.getSession(true).setAttribute("error_message", errorMessage);
            response.sendRedirect(request.getContextPath() + "/login");
        }

    }
}
