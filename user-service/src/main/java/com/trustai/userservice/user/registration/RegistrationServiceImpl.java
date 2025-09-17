package com.trustai.userservice.user.registration;

import com.trustai.common.auth.dto.response.AuthResponse;
import com.trustai.common.auth.exception.BadCredentialsException;
import com.trustai.common.auth.repository.RoleRepository;
import com.trustai.common.auth.service.AuthService;
import com.trustai.common.auth.service.otp.OtpService;
import com.trustai.common.auth.service.otp.OtpSession;
import com.trustai.common.constants.CommonConstants;
import com.trustai.common.constants.SecurityConstants;
import com.trustai.common.domain.user.Role;
import com.trustai.common.domain.user.User;
import com.trustai.common.dto.NotificationRequest;
import com.trustai.common.enums.NotificationChannel;
import com.trustai.common.event.NotificationEvent;
import com.trustai.common.event.UserRegisteredEvent;
import com.trustai.common.exceptions.RegistrationException;
import com.trustai.common.repository.user.UserRepository;
import com.trustai.common.utils.StringUtils;
import com.trustai.userservice.hierarchy.service.UserHierarchyService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
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
    private final UserHierarchyService userHierarchyService;
    private final ApplicationEventPublisher publisher;

    private static final String REG_FLOW = "REGISTER";
    private static final int MAX_REFERRAL_CODE_LENGTH = 8;

    @Override
    public OtpSession createPendingRegistration(RegistrationRequest request) {
        log.info("Start pending registration for email: {}", request.getEmail());
        validateRegistrationRequest(request);

        // Step 1: Step 1: Check permanent users
        if (userRepo.existsByUsername(request.getUsername())) {
            throw new RegistrationException("Username already exists");
        }
        if (userRepo.existsByEmail(request.getEmail())) {
            throw new RegistrationException("Email already exists");
        }
        if (!userRepo.existsByReferralCode(request.getReferralCode())) { // Step4: Verify ReferralCode:
            log.warn("Invalid referral code: {}", request.getReferralCode());
            throw new RegistrationException("referralCode is invalid");
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
                    return new RegistrationException("Invalid or expired OTP session");
                });


        // 2. Increment attempts *before* verification
        otpService.incrementAttempts(sessionId, SecurityConstants.MAX_OTP_ATTEMPTS);

        // 3. Verify OTP
        if (!otpService.verifyOtp(sessionId, otp)) {
            log.warn("Invalid OTP provided for session: {}", sessionId);
            throw new RegistrationException("Invalid OTP");
        }

        // 4. OTP verified → promote PendingUser → User
        PendingUser pendingUser = pendingRepo.findByEmail(session.username())// we are storing email as username o=in otp session
                .orElseThrow(() -> {
                    log.error("Pending user not found for username: {}", session.username());
                    return new RegistrationException("username not found in pending user");
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
    @Transactional
    public User directRegister(User user, String referralCode) {
        log.info("Direct registration for username: {}", user.getUsername());

        userRepo.findByReferralCode(referralCode).orElseThrow(() -> new RegistrationException("invalid referralCode"));
        userRepo.findByUsernameIgnoreCase(user.getUsername()).ifPresent(u -> {
            throw new RegistrationException("username already exist");
        });

        // Fetch existing role from DB
        Role userRole = roleRepository.findByName(CommonConstants.ROLE_USER)
                .orElseThrow(() -> new RegistrationException("Default role not found: " + CommonConstants.ROLE_USER));

        user.setReferralCode(user.getUsername());
        user.setPassword(passwordEncoder.encode("123"));
        user.getRoles().add(userRole);

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

        return newUser;
    }

    @Override
    public void resendOtp(String sessionId) {
        log.info("Resending OTP for sessionId: {}", sessionId);

        // 1. Fetch OTP session
        OtpSession oldSession = otpService.getSession(sessionId)
                .orElseThrow(() -> {
                    log.warn("Invalid or expired OTP session: {}", sessionId);
                    return new RegistrationException("Invalid or expired OTP session");
                });

        // 2. Validate pending user exists for this session
        PendingUser pendingUser = pendingRepo.findByEmail(oldSession.username()) // OTP session username = email
                .orElseThrow(() -> {
                    log.error("No pending user found for email: {}", oldSession.username());
                    return new RegistrationException("Pending user not found");
                });

        /*
        // Invalidate old session
        otpService.invalidateSession(sessionId);

        // Create new OTP session
        OtpSession newSession = otpService.createSession(pendingUser.getEmail(), REG_FLOW, SecurityConstants.MAX_OTP_ATTEMPTS);

        otpService.sendOtp(newSession, "EMAIL");
         */

        // 3. Increment attempts (protect against brute force / spam)
        otpService.incrementAttempts(sessionId, SecurityConstants.MAX_OTP_ATTEMPTS);

        // 4. Resend the OTP using configured channel (EMAIL here)
        otpService.sendOtp(oldSession, "EMAIL");

        log.info("OTP resent successfully for username: {}", pendingUser.getUsername());
    }

    /*@Override
    public void resendOtp__withNewOTP(String sessionId) {
        log.info("Resending OTP for sessionId: {}", sessionId);

        OtpSession oldSession = otpService.getSession(sessionId)
                .orElseThrow(() -> new RegistrationException("Invalid or expired OTP session"));

        PendingUser pendingUser = pendingRepo.findByEmail(oldSession.username())
                .orElseThrow(() -> new RegistrationException("Pending user not found"));

        // Invalidate old session
        otpService.invalidateSession(sessionId);

        // Create new OTP session
        OtpSession newSession = otpService.createSession(
                pendingUser.getEmail(),
                pendingUser.getEmail(), // using email as username
                SecurityConstants.MAX_OTP_ATTEMPTS,
                SecurityConstants.OTP_EXPIRE_MINUTES
        );

        // Send new OTP
        otpService.sendOtp(newSession, "EMAIL");

        log.info("New OTP generated and sent for username: {}", pendingUser.getUsername());
    }*/

    @Transactional
    private User doRegister(User user, String inviteCode) {
        log.info("Registering user: {}", user.getUsername());
        User newUser = userRepo.save(user);
        log.info("User persisted with ID: {}", newUser.getId());


        userRepo.findByReferralCode(inviteCode).ifPresent(referrer -> {
            if (referrer.getId() != null && referrer.getId().equals(user.getId())) {
                throw new RegistrationException("User cannot refer themselves");
            }
            newUser.setReferrer(referrer);
        });

        // Generate a unique referral code
        log.info("Generating referralCode for userId: {}.....", newUser.getId());
        String referralCode = generateUniqueReferralCode();
        newUser.setReferralCode(referralCode);
        userRepo.save(newUser);
        log.info("Referral code generated and saved: {}", referralCode);

        if (newUser.getReferrer() != null) {
            log.info("User has referrer (ID: {}). Updating hierarchy...", newUser.getReferrer().getId());
            userHierarchyService.updateHierarchy(newUser.getReferrer().getId(), newUser.getId());
        }

        publishRegistrationSuccessEvents(newUser);
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
            throw new RegistrationException("Invalid username");
        }
        if (StringUtils.isBlank(request.getPassword())) {
            log.warn("Validation failed: password is blank");
            throw new RegistrationException("Invalid password");
        }
        if (StringUtils.isBlank(request.getEmail())) {
            log.warn("Validation failed: email is blank");
            throw new RegistrationException("Invalid email");
        }
        if (StringUtils.isBlank(request.getReferralCode())) {
            log.warn("Validation failed: referralCode is blank");
            throw new RegistrationException("Invalid referralCode");
        }
    }


    public User mapFromPending(PendingUser pendingUser) {
        log.debug("Mapping PendingUser to User for: {}", pendingUser.getUsername());
        User newUser = new User();
        newUser.setUsername(pendingUser.getUsername());
        newUser.setPassword(pendingUser.getPasswordHash());
        newUser.setEmail(pendingUser.getEmail());
        newUser.setEmailVerified(true);
        newUser.setMobile(pendingUser.getMobile());

        // Fetch existing role from DB
        Role userRole = roleRepository.findByName(CommonConstants.ROLE_USER)
                .orElseThrow(() -> new RegistrationException("Default role not found: " + CommonConstants.ROLE_USER));

        if (newUser.getRoles() == null) {
            newUser.setRoles(new HashSet<>());
        }

        //newUser.setRoles(new HashSet<>(List.of(userRole)));
        newUser.getRoles().add(userRole);
        return newUser;
    }

    public String generateUniqueReferralCode() {
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

    @Async
    private void publishRegistrationSuccessEvents(User newUser) {
        try {
            Long userId = newUser.getId();
            Long referrerId = newUser.getReferrer() != null ? newUser.getReferrer().getId() : null;
            String email = newUser.getEmail();

            log.info("➡️  Publishing UserRegisteredEvent | userId={}, referrerId={}", userId, referrerId);
            publisher.publishEvent(new UserRegisteredEvent(userId, referrerId));

            // Common data
            String subjectOrTitle = "Registration Success";
            String message = "Thanks for registering TrustAI";


            log.info("➡️  Publishing InApp Notification | userId={}, title='Registration Success'", userId);
            publisher.publishEvent(new NotificationEvent(this,
                    NotificationRequest.forInApp(
                            String.valueOf(userId),
                            subjectOrTitle,
                            message
                    )
            ));

            log.info("➡️  Publishing Email Notification | email={}, subject='Registration Success'", email);
            publisher.publishEvent(new NotificationEvent(this,
                    NotificationRequest.forEmail(
                            email,
                            subjectOrTitle,
                            message
                    )
            ));

            log.info("✅ Registration success flow completed for userId={}", userId);
        } catch (Exception e) {
            log.error("❌ Failed to publish registration success events for userId={}", newUser.getId(), e);
        }
    }

    /*@Async
    private void publishRegistrationSuccessEvents(User newUser) {
        try {
            Long userId = newUser.getId();
            Long referrerId = newUser.getReferrer() != null ? newUser.getReferrer().getId() : null;
            String email = newUser.getEmail();

            log.info("➡️  Publishing UserRegisteredEvent | userId={}, referrerId={}", userId, referrerId);
            publisher.publishEvent(new UserRegisteredEvent(userId, referrerId));

            String title = "Registration Success";
            String message = "Thanks for registering TrustAI";

            NotificationRequest request = NotificationRequest.createMultiChannelNotification(
                    email,                           // email recipient
                    String.valueOf(userId),          // in-app recipient
                    title,
                    message,
                    NotificationChannel.EMAIL,
                    NotificationChannel.IN_APP
            );

            log.info("➡️  Publishing Multi-Channel Notification | userId={}, email={}", userId, email);
            publisher.publishEvent(new NotificationEvent(this, request));

            log.info("✅ Registration success flow completed for userId={}", userId);
        } catch (Exception e) {
            log.error("❌ Failed to publish registration success events for userId={}", newUser.getId(), e);
        }
    }*/
}
