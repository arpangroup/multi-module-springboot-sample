package com.trustai.common.security.filter;

import com.trustai.common.security.jwt.JwtProvider;
import com.trustai.common.security.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final CustomUserDetailsService userDetailsService;


    public JwtAuthenticationFilter(JwtProvider jwtProvider, CustomUserDetailsService userDetailsService) {
        this.jwtProvider = jwtProvider;
        this.userDetailsService = userDetailsService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            String token = extractToken(request);
            if (token != null && jwtProvider.validateToken(token)) {
                // load the userDetails from DB
                /*String username = jwtProvider.extractUsername(token);
                var userDetails = userDetailsService.loadUserByUsername(username);*/

                // no DB call required, directly get userDetails from JWT Token
                var userDetails   = jwtProvider.loadFromJwt(token);

                var auth = new UsernamePasswordAuthenticationToken(
                    userDetails.getUsername(),      // principal = username
                    null,                           // credentials
                    userDetails.getAuthorities()    // authorities
                );
                //auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                auth.setDetails(userDetails);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception ex) {
            log.warn("JWT authentication failed: {}", ex.getMessage());
            // skip setting security context — AuthEntryPoint will handle unauthorized responses
        }

        filterChain.doFilter(request, response);
    }

    /*@Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            String token = extractToken(request);
            if (token != null && jwtProvider.validateToken(token)) {

                // Use JWT claims to build CustomUserDetails
                CustomUserDetails userDetails = jwtProvider.loadFromJwt(token);

                // Set authentication
                var auth = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception ex) {
            // Skip setting security context; AuthEntryPoint will handle unauthorized responses
        }

        filterChain.doFilter(request, response);
    }*/

    private String extractToken(HttpServletRequest req) {
        String bearerHeader = req.getHeader("Authorization");
        if (StringUtils.hasText(bearerHeader) && bearerHeader.startsWith("Bearer ")) {
            return bearerHeader.substring(7);
        }
        return null;
    }

    /*@Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Only filter /api/** (stateless API)
        String path = request.getServletPath();
//        return !path.startsWith("/api/");
        return !path.startsWith("/api/") || path.startsWith("/api/auth/") || path.startsWith("/api/v1/register/");
//        return path.startsWith("/api/v1/auth/");
    }*/

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/api/auth/") || path.startsWith("/api/register/");
    }
}
