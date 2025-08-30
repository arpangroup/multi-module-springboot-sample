package com.trustai.common.auth.registration;

import com.trustai.common.auth.dto.request.OtpVerifyRequest;
import com.trustai.common.auth.dto.response.AuthResponse;
import com.trustai.common.auth.entity.RegistrationProgress;
import com.trustai.common.auth.entity.VerificationToken;
import com.trustai.common.auth.entity.VerificationType;
import com.trustai.common.auth.exception.AuthException;
import com.trustai.common.auth.exception.BadCredentialsException;
import com.trustai.common.auth.repository.RoleRepository;
import com.trustai.common.auth.service.AuthService;
import com.trustai.common.auth.service.otp.OtpService;
import com.trustai.common.auth.service.otp.OtpSession;
import com.trustai.common.constants.CommonConstants;
import com.trustai.common.constants.SecurityConstants;
import com.trustai.common.domain.user.Role;
import com.trustai.common.domain.user.User;
import com.trustai.common.event.UserRegisteredEvent;
import com.trustai.common.repository.user.UserRepository;
import com.trustai.common.utils.IdConverter;
import com.trustai.common.utils.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static com.trustai.common.constants.SecurityConstants.isExpired;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationServiceImpl implements RegistrationService {
    private final PendingUserRepository pendingRepo;
    private final UserRepository userRepo;
    private final RoleRepository roleRepository;
    private final OtpService otpService;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;
    private final ApplicationEventPublisher publisher;

    private static final String REG_FLOW = "REGISTER";
    private static final int MAX_REFERRAL_CODE_LENGTH = 8;

    @Override
    public OtpSession createPendingRegistration(RegistrationRequest request) {
        log.info("Start pending registration for email: {}", request.getEmail());
        validateRegistrationRequest(request);

        // Step 1: Step 1: Check permanent users
        if (userRepo.existsByUsername(request.getUsername())) {
            throw new BadCredentialsException("Username already exists");
        }
        if (userRepo.existsByEmail(request.getEmail())) {
            throw new BadCredentialsException("Email already exists");
        }
        if (!userRepo.existsByReferralCode(request.getReferralCode())) { // Step4: Verify ReferralCode:
            log.warn("Invalid referral code: {}", request.getReferralCode());
            throw new BadCredentialsException("referralCode is invalid");
        }

        // Step 2: Clean up expired pending records for username/email
        cleanupExpiredPending(request.getUsername(), request.getEmail());

        // Step 3: Handle active pending record
        Optional<PendingUser> existing = pendingRepo.findByUsername(request.getUsername());
        if (existing.isPresent()) {
            PendingUser pending = existing.get();

            // check if still valid (OTP session not expired)
            Optional<OtpSession> otpSessionOpt = otpService.getSessionByUsername(pending.getEmail());
            if (otpSessionOpt.isPresent()) {
                log.info("User {} already has an active pending registration. Resending OTP.", request.getUsername());
                otpService.incrementAttempts(otpSessionOpt.get().sessionId(), SecurityConstants.MAX_OTP_ATTEMPTS);
                otpService.sendOtp(otpSessionOpt.get(), "EMAIL");
                return otpSessionOpt.get();
            } else {
                log.info("Pending record expired for {}, cleaning up and creating fresh one", request.getUsername());
                pendingRepo.delete(pending);
            }

        }


        // Step 4: Create PendingUser
        log.info("Creating pending user for username: {}", request.getUsername());
        PendingUser pending = PendingUser.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .mobile(request.getMobile())
                .passwordHash(passwordEncoder.encode(request.getPassword())) // hash before saving in real system!
                .referralCode(request.getReferralCode())
                .createdAt(LocalDateTime.now())
                .build();

        pendingRepo.save(pending);
        log.info("Pending user saved successfully: {}", request.getUsername());

        // Step 5. Create OTP Session and send
        OtpSession otpSession = otpService.createSession(request.getEmail(), REG_FLOW, SecurityConstants.MAX_OTP_ATTEMPTS);
        otpService.sendOtp(otpSession, "EMAIL"); // or SMS, depending on channel


        log.info("Enriching IP Details...");
        //registrationHelper.enrichWithIpDetails(progress, servletRequest);

        //emailService.sendVerificationEmail(request.getEmail(), token);

        log.info("OTP session created and sent for username: {}", request.getUsername());
        return otpSession;
    }

    @Transactional
    public AuthResponse completeRegistration(String sessionId, String otp) {
        log.info("Completing registration for sessionId: {}", sessionId);

        // 1. Fetch OTP session
        OtpSession session = otpService.getSession(sessionId)
                .orElseThrow(() -> {
                    log.warn("Invalid or expired OTP session: {}", sessionId);
                    return new BadCredentialsException("Invalid or expired OTP session");
                });


        // 2. Increment attempts *before* verification
        otpService.incrementAttempts(sessionId, SecurityConstants.MAX_OTP_ATTEMPTS);

        // 3. Verify OTP
        if (!otpService.verifyOtp(sessionId, otp)) {
            log.warn("Invalid OTP provided for session: {}", sessionId);
            throw new BadCredentialsException("Invalid OTP");
        }

        // 4. OTP verified → promote PendingUser → User
        PendingUser pendingUser = pendingRepo.findByEmail(session.username())// we are storing email as username o=in otp session
                .orElseThrow(() -> {
                    log.error("Pending user not found for username: {}", session.username());
                    return new RuntimeException("username not found in pending user");
                });

        log.info("Mapping pending user to permanent user: {}", pendingUser.getUsername());
        User newUser = mapFromPending(pendingUser);
        doRegister(newUser, pendingUser.getReferralCode());

        // Delete pending user
        pendingRepo.delete(pendingUser);
        log.info("Pending user deleted: {}", pendingUser.getUsername());

        // 5. Invalidate OTP session after success
        otpService.invalidateSession(sessionId);

        // 6. Issue token
        AuthResponse response = authService.issueTokenForUsername(newUser.getUsername());
        log.info("Registration complete for sessionId: {}", sessionId);

        return response;
    }

    @Override
    public User directRegister(User user, String referralCode) {
        log.info("Direct registration for username: {}", user.getUsername());

        userRepo.findByReferralCode(referralCode).ifPresent(user::setReferrer);

        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            String generatedEmail = user.getUsername() + "@trustai.com";
            user.setEmail(generatedEmail);
            log.info("Email not provided. Generated default: {}", generatedEmail);
        }
//        Kyc kyc = new Kyc();
//        kyc.setEmail(user.getEmail());
//        kyc.setPhone(user.getMobile());
//        kyc.setFirstname(user.getUsername());
        //user.setKycInfo(kyc);

        User newUser = doRegister(user, referralCode);
        log.info("Direct registration completed for userId: {}", newUser.getId());

        return doRegister(user, referralCode);
    }

    private User doRegister(User user, String inviteCode) {
        log.info("Registering user: {}", user.getUsername());
        userRepo.findByReferralCode(inviteCode).ifPresent(user::setReferrer);

        User newUser = userRepo.save(user);
        log.info("User persisted with ID: {}", newUser.getId());

        // Generate a unique referral code
        log.info("Generating referralCode for userId: {}.....", newUser.getId());
        String referralCode = generateUniqueReferralCode();
        newUser.setReferralCode(referralCode);
        userRepo.save(newUser);
        log.info("Referral code generated and saved: {}", referralCode);

        if (newUser.getReferrer() != null) {
            log.info("User has referrer (ID: {}). Updating hierarchy...", newUser.getReferrer().getId());
            // TODO handle UserHierarchy Update
//            userHierarchyService.updateHierarchy(newUser.getReferrer().getId(), newUser.getId());
        }

        log.info("Publishing UserRegisteredEvent for userId: {}", newUser.getId());
        publisher.publishEvent(new UserRegisteredEvent(
                newUser.getId(),
                newUser.getReferrer() != null ? newUser.getReferrer().getId() : null
        ));

        return newUser;
    }

    private void cleanupExpiredPending(String username, String email) {
        pendingRepo.findByUsername(username).ifPresent(p -> {
            if (isExpired(p.getCreatedAt())) {
                pendingRepo.delete(p);
            }
        });

        pendingRepo.findByEmail(email).ifPresent(p -> {
            if (isExpired(p.getCreatedAt())) {
                pendingRepo.delete(p);
            }
        });
    }

    private void validateRegistrationRequest(RegistrationRequest request) {
        // Validate input
        if (StringUtils.isBlank(request.getUsername())) {
            log.warn("Validation failed: username is blank");
            throw new BadCredentialsException("Invalid username");
        }
        if (StringUtils.isBlank(request.getPassword())) {
            log.warn("Validation failed: password is blank");
            throw new BadCredentialsException("Invalid password");
        }
        if (StringUtils.isBlank(request.getEmail())) {
            log.warn("Validation failed: email is blank");
            throw new BadCredentialsException("Invalid email");
        }
        if (StringUtils.isBlank(request.getReferralCode())) {
            log.warn("Validation failed: referralCode is blank");
            throw new BadCredentialsException("Invalid referralCode");
        }
    }


    private User mapFromPending(PendingUser pendingUser) {
        log.debug("Mapping PendingUser to User for: {}", pendingUser.getUsername());
        User newUser = new User();
        newUser.setUsername(pendingUser.getUsername());
        newUser.setPassword(pendingUser.getPasswordHash());
        newUser.setEmail(pendingUser.getEmail());
        newUser.setEmailVerified(true);
        newUser.setMobile(pendingUser.getMobile());

        // Fetch existing role from DB
        Role userRole = roleRepository.findByName(CommonConstants.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Default role not found: " + CommonConstants.ROLE_USER));

        newUser.setRoles(new HashSet<>(List.of(userRole)));
        return newUser;
    }

    private String generateUniqueReferralCode() {
        String code;
        int attempts = 0;
        do {
            code = ReferralCodeUtil.generate(MAX_REFERRAL_CODE_LENGTH);
            attempts++;
        } while (userRepo.existsByReferralCode(code));

        log.info("Generated unique referral code '{}' after {} attempt(s)", code, attempts);
        return code;
    }

    public HttpServletRequest getCurrentHttpRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs.getRequest();
    }
}
