package com.trustai.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException ex) throws IOException {
        String traceId = MDC.get("traceId");
        String path = request.getRequestURI();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = (authentication != null && authentication.isAuthenticated()) ? authentication.getName() : "anonymous";
        List<String> roles = (authentication != null && authentication.getAuthorities() != null)
                ? authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList()
                : List.of();

        log.warn("Access Denied. traceId={}, user={}, path={}, roles={}, reason={}",
                traceId, username, path, roles, ex.getMessage());

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");

        String body = String.format("""
                {
                    "status": 403,
                    "error": "Access Denied",
                    "message": "You do not have permission to access this resource.",
                    "traceId": "%s",
                    "userId": "%s",
                    "roles": %s,
                    "path": "%s"
                }
                """,
                traceId != null ? traceId : "N/A",
                username,
                new ObjectMapper().writeValueAsString(roles), // serialize list to JSON array
                path);

        response.getWriter().write(body);
    }

}
