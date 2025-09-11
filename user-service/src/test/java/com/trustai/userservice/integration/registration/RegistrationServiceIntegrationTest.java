//package com.trustai.userservice.integration.registration;
//
//import com.trustai.common.auth.dto.response.AuthResponse;
//import com.trustai.common.auth.repository.RoleRepository;
//import com.trustai.common.auth.service.AuthService;
//import com.trustai.common.auth.service.otp.OtpService;
//import com.trustai.common.auth.service.otp.OtpSession;
//import com.trustai.common.constants.CommonConstants;
//import com.trustai.common.domain.user.Role;
//import com.trustai.common.domain.user.User;
//import com.trustai.common.repository.user.UserRepository;
//import com.trustai.userservice.hierarchy.repository.UserHierarchyRepository;
//import com.trustai.userservice.hierarchy.service.UserHierarchyService;
//import com.trustai.userservice.unit.registration.RegistrationServiceImplTest;
//import com.trustai.userservice.user.registration.PendingUser;
//import com.trustai.userservice.user.registration.PendingUserRepository;
//import com.trustai.userservice.user.registration.RegistrationRequest;
//import com.trustai.userservice.user.registration.RegistrationService;
//import jakarta.transaction.Transactional;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.context.ApplicationEventPublisher;
//import org.springframework.context.event.EventListener;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.test.annotation.DirtiesContext;
//
//import java.time.LocalDateTime;
//import java.util.Optional;
//import java.util.Set;
//import java.util.concurrent.atomic.AtomicBoolean;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.Mockito.*;
//
//@SpringBootTest
//@Transactional
//public class RegistrationServiceIntegrationTest {
//
//    @Autowired
//    private RegistrationService registrationService;
//
//    @Autowired
//    private UserRepository userRepo;
//
//    @Autowired
//    private PendingUserRepository pendingRepo;
//
//    @Autowired
//    private RoleRepository roleRepository;
//
//    @Autowired
//    private UserHierarchyService userHierarchyService;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Autowired
//    private ApplicationEventPublisher publisher;
//
//    @Autowired
//    private AuthService authService;
//
//    @Autowired
//    private OtpService otpService;
//
//    private Role userRole;
//
//    @BeforeEach
//    void setUp() {
//        // create default ROLE_USER if not exists
//        userRole = roleRepository.findByName(CommonConstants.ROLE_USER)
//                .orElseGet(() -> roleRepository.save(new Role(null, CommonConstants.ROLE_USER)));
//    }
//
//    @Test
//    void shouldCompleteFullRegistrationFlow() {
//        // pre-create referrer user
//        User referrer = new User();
//        referrer.setUsername("refUser");
//        referrer.setEmail("ref@example.com");
//        referrer.setPassword(passwordEncoder.encode("pwd"));
//        referrer.setReferralCode("REF123");
//        referrer.setRoles(Set.of(userRole));
//        userRepo.save(referrer);
//
//        // 1️⃣ Create pending registration
//        RegistrationRequest request = new RegistrationRequest();
//        request.setUsername("intUser");
//        request.setPassword("pass123");
//        request.setEmail("int@example.com");
//        request.setReferralCode("REF123"); // assume valid existing referrer
//        request.setMobile("1234567890");
//
//        OtpSession session = otpService.createSession(request.getEmail(), "REGISTER", 5);
//        registrationService.createPendingRegistration(request);
//
//        // verify pending user created
//        PendingUser pending = pendingRepo.findByEmail(request.getEmail()).orElseThrow();
//        assertThat(pending).isNotNull();
//
//        // 2️⃣ Complete registration
//        when(otpService.getSession(session.sessionId())).thenReturn(Optional.of(session));
//        when(otpService.verifyOtp(session.sessionId(), "123456")).thenReturn(true);
//        when(authService.issueTokenForUsername("intUser"))
//                .thenReturn(new AuthResponse("token123", null, 3600, 3600));
//        when(roleRepository.findByName(CommonConstants.ROLE_USER)).thenReturn(Optional.of(userRole));
//
//        AuthResponse response = registrationService.completeRegistration(session.sessionId(), "123456");
//
//        // ✅ Assertions
//        assertThat(response.accessToken()).isEqualTo("token123");
//
//        // user promoted from pending → permanent
//        User newUser = userRepo.findByUsername("intUser").orElseThrow();
//        assertThat(newUser.getEmail()).isEqualTo("int@example.com");
//        assertThat(newUser.getRoles()).extracting(Role::getName).contains(CommonConstants.ROLE_USER);
//        assertThat(newUser.getReferrer()).isNotNull();
//        assertThat(newUser.getReferrer().getUsername()).isEqualTo("refUser");
//
//        // pending user deleted
//        assertThat(pendingRepo.findByEmail("int@example.com")).isEmpty();
//
//        // hierarchy updated
//        verify(userHierarchyService).updateHierarchy(referrer.getId(), newUser.getId());
//    }
//}
