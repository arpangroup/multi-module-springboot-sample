//package com.trustai.userservice.e2e;
//
//import com.trustai.common.auth.dto.request.OtpVerifyRequest;
//import com.trustai.common.auth.dto.response.AuthResponse;
//import com.trustai.common.auth.repository.RoleRepository;
//import com.trustai.common.auth.service.AuthService;
//import com.trustai.common.auth.service.otp.OtpService;
//import com.trustai.common.auth.service.otp.OtpSession;
//import com.trustai.common.constants.CommonConstants;
//import com.trustai.common.domain.user.Role;
//import com.trustai.common.domain.user.User;
//import com.trustai.common.dto.ApiResponse;
//import com.trustai.common.repository.user.UserRepository;
//import com.trustai.userservice.user.registration.PendingUserRepository;
//import com.trustai.userservice.user.registration.RegistrationRequest;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.transaction.annotation.Propagation;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.*;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@AutoConfigureMockMvc
//@Transactional(propagation = Propagation.NOT_SUPPORTED) // disable wrapping test in transaction
//public class RegistrationControllerTest {
//
//    @LocalServerPort
//    private int port;
//
//    @Autowired
//    private TestRestTemplate restTemplate;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private PendingUserRepository pendingUserRepository;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @MockBean
//    private OtpService otpService;
//
//    @MockBean
//    private AuthService authService;
//
//    @Autowired
//    private RoleRepository roleRepository;
//
//    private Role userRole;
//
//    @BeforeEach
//    void setUp() {
//        userRole = roleRepository.findByName(CommonConstants.ROLE_USER)
//                .orElseGet(() -> roleRepository.save(new Role(CommonConstants.ROLE_USER)));
//
//        // Clean up any users and pending users before each test
//        userRepository.deleteAll();
//        pendingUserRepository.deleteAll();
//
//        // Create a referrer user with a referral code to test referral code handling
//        User referrer = new User();
//        referrer.setUsername("referrer");
//        referrer.setEmail("ref@example.com");
//        referrer.setReferralCode("DUMMY1234");
//        referrer.setPassword(passwordEncoder.encode("123456"));
//        referrer.setRoles(new java.util.HashSet<>());
//        referrer.getRoles().add(userRole);
//        userRepository.save(referrer);
//
//        // Verify referrer exists with the referral code
//        assertThat(userRepository.existsByReferralCode("DUMMY1234"))
//                .as("Referrer user should exist with referral code DUMMY1234")
//                .isTrue();
//    }
//
//    @Test
//    void shouldRegisterUserEndToEnd() {
//        // 1️⃣ Prepare registration request
//        RegistrationRequest request = new RegistrationRequest();
//        request.setUsername("e2eUser");
//        request.setPassword("pass123");
//        request.setEmail("e2e@example.com");
//        request.setReferralCode("DUMMY1234");
//        request.setMobile("1234567890");
//
//        // 2️⃣ Mock OTP Service behavior
//        OtpSession fakeSession = new OtpSession("sid123", request.getEmail(), "REGISTER");
//        when(otpService.createSession(request.getEmail(), "REGISTER", 5)).thenReturn(fakeSession);
//        when(otpService.getSession("sid123")).thenReturn(Optional.of(fakeSession));
//        when(otpService.verifyOtp("sid123", "123456")).thenReturn(true);
//        doNothing().when(otpService).sendOtp(any(OtpSession.class), eq("EMAIL"));
//
//        // 3️⃣ Mock AuthService to issue token after registration
//        when(authService.issueTokenForUsername("e2eUser"))
//                .thenReturn(new AuthResponse("token123", null, 3600, 3600));
//
//        // 4️⃣ Send registration request to /api/register endpoint
//        ResponseEntity<OtpSession> createResponse = restTemplate.postForEntity(
//                "http://localhost:" + port + "/api/register",
//                request,
//                OtpSession.class
//        );
//
//        // Debug logs for insight
//        System.out.println("Create Response: " + createResponse.getStatusCode());
//        System.out.println("Create Response Body: " + createResponse.getBody());
//
//        // Assert successful response and non-null OtpSession with correct sessionId
//        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
//        OtpSession session = createResponse.getBody();
//        assertThat(session).isNotNull();
//        assertThat(session.sessionId()).isEqualTo("sid123");
//
//        // 5️⃣ Verify OTP - simulate user submitting OTP for verification
//        OtpVerifyRequest verifyRequest = new OtpVerifyRequest();
//        verifyRequest.setSessionId("sid123");
//        verifyRequest.setOtp("123456");
//
//        ResponseEntity<ApiResponse> verifyResponse = restTemplate.postForEntity(
//                "http://localhost:" + port + "/api/register/verify",
//                verifyRequest,
//                ApiResponse.class
//        );
//
//        System.out.println("Verify Response Status: " + verifyResponse.getStatusCode());
//        System.out.println("Verify Response Body: " + verifyResponse.getBody());
//
//        // Assert successful OTP verification and completion of registration
//        assertThat(verifyResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(verifyResponse.getBody().getMessage()).isEqualTo("Registration completed successfully");
//
//        // Verify mocks were called as expected
//        verify(otpService).createSession(request.getEmail(), "REGISTER", 5);
//        verify(otpService).sendOtp(fakeSession, "EMAIL");
//        verify(otpService).verifyOtp("sid123", "123456");
//        verify(authService).issueTokenForUsername("e2eUser");
//    }
//}
