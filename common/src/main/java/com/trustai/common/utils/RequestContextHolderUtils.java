package com.trustai.common.utils;

import com.trustai.common.constants.CommonConstants;
import com.trustai.common.security.service.CustomUserDetails;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.stream.Collectors;

public class RequestContextHolderUtils {

    public static Long getCurrentUserId() {
        Authentication auth = requireAuthentication();

        // Handle internal-service user → prefer propagated header
        if (isInternalRequest(auth)) {
            return parseLongHeader(
                    CommonConstants.HEADER_ACTING_USER_ID,
                    "Missing acting user ID for internal call",
                    "Invalid acting user ID format"
            );
        }

        // if not internal call
        return getExternalUserId(auth);
    }

    public static String getCurrentUsername() {
        Authentication auth = requireAuthentication();

        if (isInternalRequest(auth)) {
            return requireHeader(CommonConstants.HEADER_X_USERNAME, "Acting username should not be null or empty");
        }

        return getExternalUsername(auth);
    }

    public static boolean isAdmin() {
        return hasRole("ADMIN"); // Check role from security context
    }

    public static List<String> getCurrentUserRoles() {
        Authentication auth = requireAuthentication();
        return auth.getAuthorities().stream()
                .map(granted -> granted.getAuthority())
                .collect(Collectors.toList());
    }



    /* #######################################################################################*/
    /* ################################ Private Utility Methods ##############################*/
    /* #######################################################################################*/

    private static Authentication requireAuthentication() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new SecurityException("No authenticated user found");
        }
        return auth;
    }

    private static boolean hasRole(String role) {
        Authentication auth = requireAuthentication();
        return auth.getAuthorities().stream()
                .anyMatch(granted -> granted.getAuthority().equals("ROLE_" + role));
    }

    private static boolean isInternalRequest(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_INTERNAL".equals(a.getAuthority()));
    }

    private static String requireHeader(String headerName, String missingMessage) {
        String value = getHeader(headerName);
        if (value == null || value.isEmpty()) {
            throw new AccessDeniedException(missingMessage);
        }
        return value;
    }

    private static Long parseLongHeader(String headerName, String missingMessage, String invalidMessage) {
        String value = requireHeader(headerName, missingMessage);
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(invalidMessage, ex);
        }
    }

    private static String getHeader(String headerName) {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return (attrs != null) ? attrs.getRequest().getHeader(headerName) : null;
    }

    private static Long getExternalUserId(Authentication auth) {
        Object details = auth.getDetails();
        if (!(details instanceof CustomUserDetails)) {
            throw new IllegalStateException("Authentication details are not CustomUserDetails");
        }
        return ((CustomUserDetails) details).getId();
    }

    private static String getExternalUsername(Authentication auth) {
        String username = auth.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails
                ? ((org.springframework.security.core.userdetails.UserDetails) auth.getPrincipal()).getUsername()
                : auth.getPrincipal().toString();

        return username;
    }
}
