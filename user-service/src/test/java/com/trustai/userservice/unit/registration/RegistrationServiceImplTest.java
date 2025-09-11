package com.trustai.userservice.unit.registration;

import com.trustai.common.auth.dto.response.AuthResponse;
import com.trustai.common.auth.exception.BadCredentialsException;
import com.trustai.common.auth.exception.TooManyOtpAttemptsException;
import com.trustai.common.auth.repository.RoleRepository;
import com.trustai.common.auth.service.AuthService;
import com.trustai.common.auth.service.otp.OtpService;
import com.trustai.common.auth.service.otp.OtpSession;
import com.trustai.common.constants.CommonConstants;
import com.trustai.common.domain.user.Role;
import com.trustai.common.domain.user.User;
import com.trustai.common.event.UserRegisteredEvent;
import com.trustai.common.exceptions.RegistrationException;
import com.trustai.common.repository.user.UserRepository;
import com.trustai.userservice.hierarchy.service.UserHierarchyService;
import com.trustai.userservice.user.registration.PendingUser;
import com.trustai.userservice.user.registration.PendingUserRepository;
import com.trustai.userservice.user.registration.RegistrationRequest;
import com.trustai.userservice.user.registration.RegistrationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegistrationServiceImplTest {

    @Mock private PendingUserRepository pendingRepo;
    @Mock private UserRepository userRepo;
    @Mock private RoleRepository roleRepository;
    @Mock private OtpService otpService;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthService authService;
    @Mock private UserHierarchyService userHierarchyService;
    @Mock private ApplicationEventPublisher publisher;

    @InjectMocks private RegistrationServiceImpl registrationService;

    private RegistrationRequest request;
    private PendingUser pendingUser;
    private Role userRole;

    @BeforeEach
    void setUp() {
        request = new RegistrationRequest();
        request.setUsername("testuser");
        request.setPassword("pass123");
        request.setEmail("test@example.com");
        request.setReferralCode("REF123");
        request.setMobile("1234567890");

        pendingUser = PendingUser.builder()
                .username("u1")
                .email("u1@example.com")
                .passwordHash("pwd")
                .referralCode("REF123")
                .createdAt(LocalDateTime.now())
                .build();

        userRole = new Role();
        userRole.setId(100L);
        userRole.setName(CommonConstants.ROLE_USER);
    }

    // 1.1 Validation
    @Test
    void shouldThrow_whenUsernameIsEmpty() {
        request.setUsername(null);
        assertThrows(RegistrationException.class, () ->
                registrationService.createPendingRegistration(request)
        );

        // sanity check
        //registrationService.createPendingRegistration(request);
    }

    @Test
    void shouldThrow_whenUsernameAlreadyExists() {
        when(userRepo.existsByUsername("testuser")).thenReturn(true);
        assertThrows(RegistrationException.class, () ->
                registrationService.createPendingRegistration(request)
        );
    }

    @Test
    void shouldThrow_whenEmailAlreadyExists() {
        when(userRepo.existsByEmail("test@example.com")).thenReturn(true);
        assertThrows(RegistrationException.class, () ->
                registrationService.createPendingRegistration(request)
        );
    }

    @Test
    void shouldThrow_whenReferralCodeInvalid() {
        when(userRepo.existsByReferralCode("REF123")).thenReturn(false);
        assertThrows(RegistrationException.class, () ->
                registrationService.createPendingRegistration(request)
        );
    }

    // 1.2 OTP send
    @Test
    void shouldCreatePendingUserAndSendOtp() {
        when(userRepo.existsByUsername(any())).thenReturn(false);
        when(userRepo.existsByEmail(any())).thenReturn(false);
        when(userRepo.existsByReferralCode(any())).thenReturn(true);
        when(passwordEncoder.encode(any())).thenReturn("hashedPwd");

        OtpSession session = new OtpSession("sid", "test@example.com", "REGISTER");
        when(otpService.createSession(any(), any(), anyInt())).thenReturn(session);

        OtpSession result = registrationService.createPendingRegistration(request);

        assertEquals("sid", result.sessionId());
        verify(pendingRepo).save(any(PendingUser.class));
        verify(otpService).sendOtp(eq(session), eq("EMAIL"));
    }

    // 1.3 Max attempt exceeded
    @Test
    void shouldThrow_whenOtpAttemptsExceeded() {
        String sessionId = "sid";
        OtpSession session = new OtpSession(sessionId, "test@example.com", "REGISTER");
        when(otpService.getSession(sessionId)).thenReturn(Optional.of(session));

        // no doThrow here → incrementAttempts will not throw
        //doNothing().when(otpService).incrementAttempts(eq(sessionId), anyInt());

        doThrow(new TooManyOtpAttemptsException(5))
                .when(otpService).incrementAttempts(eq(sessionId), anyInt());


        assertThrows(TooManyOtpAttemptsException.class, () ->
                registrationService.completeRegistration(sessionId, "123456")
        );
    }

    // 1.4 Verify OTP
    @Test
    void shouldCompleteRegistration_whenOtpIsValid() {
        String sessionId = "sid";
        OtpSession session = new OtpSession(sessionId, "test@example.com", "REGISTER");

        PendingUser pending = PendingUser.builder()
                .username("testuser").email("test@example.com").passwordHash("hashed")
                .referralCode("REF123").build();

        when(otpService.getSession(sessionId)).thenReturn(Optional.of(session));
        when(otpService.verifyOtp(sessionId, "123456")).thenReturn(true);
        when(pendingRepo.findByEmail("test@example.com")).thenReturn(Optional.of(pending));
        when(roleRepository.findByName(CommonConstants.ROLE_USER))
                .thenReturn(Optional.of(new Role(1L, CommonConstants.ROLE_USER)));
        when(userRepo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(authService.issueTokenForUsername(any())).thenReturn(new AuthResponse("token123", null, 0, 0));

        AuthResponse response = registrationService.completeRegistration(sessionId, "123456");

        assertEquals("token123", response.accessToken());
        verify(publisher).publishEvent(any(UserRegisteredEvent.class));
    }

    // 1.5 Referral code unique
    @Test
    void shouldGenerateUniqueReferralCode() {
        when(userRepo.existsByReferralCode(any())).thenReturn(false);
        String code = registrationService.generateUniqueReferralCode();
        assertNotNull(code);
    }

    // 1.6 Default rank and role
    @Test
    void shouldAssignDefaultRankAndRole() {
        Role role = new Role(1L, CommonConstants.ROLE_USER);
        when(roleRepository.findByName(CommonConstants.ROLE_USER)).thenReturn(Optional.of(role));
        PendingUser pending = PendingUser.builder()
                .username("u1").passwordHash("h").email("e@e.com").referralCode("REF123").build();

        User user = registrationService.mapFromPending(pending);

        assertEquals("RANK_0", user.getRankCode());
        assertTrue(user.getRoles().contains(role));
    }

    // 1.8 + 1.9 Hierarchy update
    @Test
    void shouldUpdateHierarchyWhenReferrerExists() {
        // Arrange: setup referrer
        User referrer = new User();
        referrer.setId(1L);
        when(userRepo.findByReferralCode("REF123")).thenReturn(Optional.of(referrer));

        // Arrange: role
        Role userRole = new Role();
        userRole.setId(10L);
        userRole.setName("ROLE_USER");
        when(roleRepository.findByName(CommonConstants.ROLE_USER)).thenReturn(Optional.of(userRole));

        // Arrange: repo save assigns ID
        when(userRepo.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            if (u.getId() == null) {
                u.setId(2L); // new user gets ID
            }
            return u;
        });

        // Arrange: pending user
        PendingUser pendingUser = PendingUser.builder()
                .username("u2")
                .email("e2@x.com")
                .passwordHash("pwd")
                .referralCode("REF123")
                .createdAt(LocalDateTime.now())
                .build();
        when(pendingRepo.findByEmail("e2@x.com")).thenReturn(Optional.of(pendingUser));

        // Arrange: OTP session
        String sessionId = "sid";
        OtpSession otpSession = new OtpSession(sessionId, "e2@x.com", "REGISTER");
        when(otpService.getSession(sessionId)).thenReturn(Optional.of(otpSession));
        when(otpService.verifyOtp(sessionId, "123456")).thenReturn(true);

        // Act
        registrationService.completeRegistration(sessionId, "123456");

        // Assert
        verify(userHierarchyService).updateHierarchy(1L, 2L);
    }

    @Test
    void shouldThrow_whenOtpSessionExpired() {
        String sessionId = "sid";

        // otpService returns empty session → expired
        when(otpService.getSession(sessionId)).thenReturn(Optional.empty());

        assertThrows(RegistrationException.class,
                () -> registrationService.completeRegistration(sessionId, "123456"));
    }

    @Test
    void shouldThrow_whenInvalidOtp() {
        String sessionId = "sid";
        OtpSession otpSession = new OtpSession(sessionId, "u1@example.com", "REGISTER");
        when(otpService.getSession(sessionId)).thenReturn(Optional.of(otpSession));
        when(otpService.verifyOtp(sessionId, "badOtp")).thenReturn(false);

        assertThrows(RegistrationException.class,
                () -> registrationService.completeRegistration(sessionId, "badOtp"));
    }

    @Test
    void shouldThrow_whenReferralCycleDetected() {
        User selfUser = new User();
        selfUser.setId(1L);
        selfUser.setUsername("u1");

        when(userRepo.findByReferralCode("SELF123")).thenReturn(Optional.of(selfUser));

        PendingUser pu = PendingUser.builder()
                .username("u1")
                .email("u1@example.com")
                .passwordHash("pwd")
                .referralCode("SELF123")
                .createdAt(LocalDateTime.now())
                .build();

        when(pendingRepo.findByEmail("u1@example.com")).thenReturn(Optional.of(pu));
        when(roleRepository.findByName(CommonConstants.ROLE_USER)).thenReturn(Optional.of(userRole));
        when(userRepo.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });

        OtpSession otpSession = new OtpSession("sid", "u1@example.com", "REGISTER");
        when(otpService.getSession("sid")).thenReturn(Optional.of(otpSession));
        when(otpService.verifyOtp("sid", "123456")).thenReturn(true);

        assertThrows(RuntimeException.class,
                () -> registrationService.completeRegistration("sid", "123456"));
    }

    @Test
    void shouldRetry_whenDuplicateReferralCodeGenerated() {
        User referrer = new User();
        referrer.setId(10L);
        when(userRepo.findByReferralCode("REF123")).thenReturn(Optional.of(referrer));

        when(roleRepository.findByName(CommonConstants.ROLE_USER)).thenReturn(Optional.of(userRole));

        // Save assigns ID
        when(userRepo.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            if (u.getId() == null) {
                u.setId(20L);
            }
            return u;
        });

        // Referral code already exists once, then free
        when(userRepo.existsByReferralCode(anyString()))
                .thenReturn(true)   // first attempt fails
                .thenReturn(false); // second attempt succeeds

        when(pendingRepo.findByEmail("u1@example.com")).thenReturn(Optional.of(pendingUser));

        OtpSession otpSession = new OtpSession("sid", "u1@example.com", "REGISTER");
        when(otpService.getSession("sid")).thenReturn(Optional.of(otpSession));
        when(otpService.verifyOtp("sid", "123456")).thenReturn(true);

        when(authService.issueTokenForUsername("u1"))
                .thenReturn(new AuthResponse("token", "refresh", 3600L, 3600L));

        AuthResponse response = registrationService.completeRegistration("sid", "123456");

        assertThat(response.accessToken()).isEqualTo("token");
        verify(userRepo, atLeast(2)).existsByReferralCode(anyString());
    }

    @Test
    void shouldHandleDirectRegisterWithoutEmail() {
        User referrer = new User();
        referrer.setId(1L);
        when(userRepo.findByReferralCode("REF123")).thenReturn(Optional.of(referrer));

        when(userRepo.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            if (u.getId() == null) {
                u.setId(2L);
            }
            return u;
        });

        User newUser = new User();
        newUser.setUsername("uNoEmail");
        newUser.setEmail(null); // no email provided
        newUser.setPassword("pwd");
        newUser.setRoles(new HashSet<>(List.of(userRole)));

        // Call private-ish method via reflection or package-private if allowed
        // but for demo assume doRegister is public/protected
        User registered = invokeDoRegister(newUser, "REF123");

        assertThat(registered.getId()).isEqualTo(2L);
        assertThat(registered.getEmail()).isNull(); // if no fallback email logic
        verify(userHierarchyService).updateHierarchy(1L, 2L);
    }

    // helper to call protected/private doRegister
    private User invokeDoRegister(User u, String ref) {
        try {
            var method = RegistrationServiceImpl.class
                    .getDeclaredMethod("doRegister", User.class, String.class);
            method.setAccessible(true);
            return (User) method.invoke(registrationService, u, ref);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
