package com.trustai.common.config;

import com.trustai.common.constants.CommonConstants;
import com.trustai.common.security.CustomAccessDeniedHandler;
import com.trustai.common.security.filter.InternalTokenAuthFilter;
import com.trustai.common.security.filter.JwtAuthenticationFilter;
import com.trustai.common.security.jwt.JwtProvider;
import com.trustai.common.security.CustomAuthenticationFailureHandler;
import com.trustai.common.security.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
//@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomUserDetailsService userDetailsService;
    private final JwtProvider jwtProvider;
    private final com.trustai.common.security.AuthEntryPoint unauthorizedHandler;
    private final CustomAuthenticationFailureHandler failureHandler ; // centralize error handle for form base login
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Value("${security.auth.internal-token}")
    private String internalToken;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 1) API chain - stateless, uses CustomUserDetailsService and JwtAuthenticationFilter
    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtProvider, userDetailsService);
        InternalTokenAuthFilter internalTokenAuthFilter = new InternalTokenAuthFilter(internalToken);

        http
                .securityMatcher("/api/**")
                .cors(cors -> {})   // 👈 enable CORS handling here <---Without .cors(cors -> {}), Spring Security ignores the WebMvcConfigurer CORS settings.
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(e -> e.
                        authenticationEntryPoint(unauthorizedHandler)   // 401 handler
                        .accessDeniedHandler(customAccessDeniedHandler) // <-- 403 handler
                )
                //.authenticationProvider(daoProvider) // keep this if not globally registered
                .authorizeHttpRequests(auth -> auth
                        //.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // 👈 allow preflight
                        .requestMatchers("/api/auth/**", "/api/register/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Order is important: check internal token before JWT
                // The order of these two lines matters — whichever you call last will run first. So to run internalTokenAuthFilter before JWT, make sure it’s added after JWT:
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(internalTokenAuthFilter, JwtAuthenticationFilter.class);

        return http.build();
    }

    // 2) Form-login chain - stateful, uses an *in-memory* user store created locally
    @Bean
    @Order(2)
    public SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
        // If you want form login to authenticate against this in-memory store,
        // set it explicitly on the HttpSecurity:
        http
                .securityMatcher("/**")
                //.csrf(csrf -> csrf.enable()) // CSRF protection enabled for forms
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**", "/h2-console/**"))
                //.userDetailsService(inMemory)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/login",
                                "/css/**",
                                "/js/**",
                                "/public/**",
                                "/favicon.ico",
                                "/h2-console",
//                                "/actuator/**",
                                "/actuator/health",
                                CommonConstants.IMAGE_PATH + "/**" // "/images/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")                    // GET -> your Thymeleaf form
                        .loginProcessingUrl("/perform_login")   // POST -> processed by Spring Security
                        .defaultSuccessUrl("/", true)
                        .failureHandler(failureHandler)
                        .permitAll()
                )
                .logout(l -> l.logoutUrl("/logout").permitAll());

        return http.build();
    }

}
