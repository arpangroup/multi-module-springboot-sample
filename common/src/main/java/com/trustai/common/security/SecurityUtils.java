package com.trustai.common.security;

import com.trustai.common.security.service.CustomUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUtils {
    private SecurityUtils() {}

    public static Authentication createAuthentication(UserDetails userDetails) {
        return new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
    }

    public static Long getCurrentUserId() {
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails)) return null;
        return ((CustomUserDetails) auth.getPrincipal()).getId();
    }

    public static String getCurrentUsername() {
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserDetails)) return null;
        return ((UserDetails) auth.getPrincipal()).getUsername();
    }
}
