package com.trustai.common.auth.service;//package com.trustai.common.auth.service;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.trustai.common.auth.entity.RegistrationProgress;
//import com.trustai.common.auth.entity.VerificationToken;
//import com.trustai.common.auth.entity.VerificationType;
//import com.trustai.common.domain.user.User;
//import com.trustai.common.auth.dto.InitiateRegistrationRequest;
//import com.trustai.common.auth.repository.VerificationTokenRepository;
//import jakarta.servlet.http.HttpServletRequest;
//import lombok.RequiredArgsConstructor;
//import lombok.SneakyThrows;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Component;
//
//import java.time.Duration;
//import java.time.LocalDateTime;
//import java.util.Random;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class RegistrationHelper {
//    private final VerificationTokenRepository tokenRepo;
////    private final IpLocationService ipLocationService;
////    private final PasswordEncoder passwordEncoder;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    private static final Duration EXPIRY_DURATION = Duration.ofMinutes(15);
//    private static final String HEADER_X_FORWARDED_FOR = "X-Forwarded-For";
//
//    protected RegistrationProgress prepareRegistrationProgress(InitiateRegistrationRequest request, RegistrationProgress progress, HttpServletRequest servletRequest) {
//        log.info("preparing RegistrationProgress...");
//
//        progress.setEmail(request.email);
//        progress.setUsername(request.username);
//        progress.setMobile(request.mobile);
//        progress.setIpAddress(servletRequest.getRemoteAddr());
//        progress.setUserAgent(servletRequest.getHeader("User-Agent"));
//        progress.setReferrer(servletRequest.getHeader("Referer"));
//        progress.setCountry("IN"); // Can replace with actual IP location lookup
//        progress.setCity("Kolkata"); // Can replace with actual IP location lookup
//        progress.setCreatedAt(LocalDateTime.now());
//        progress.setLastUpdated(LocalDateTime.now());
//
//        return progress;
//    }
//
//
//    protected VerificationToken prepareVerificationCode(String email) {
//        String verificationCode = this.generateVerificationCode();
//
//        log.info("preparing verification code for email: {}, verificationCode: {}", email, verificationCode);
//        LocalDateTime expiryTime = LocalDateTime.now().plus(EXPIRY_DURATION);
//
//        VerificationToken token = tokenRepo.findByTypeAndTarget(VerificationType.EMAIL, email).orElse(new VerificationToken());
//        token.setTarget(email);
//        token.setType(VerificationType.EMAIL);
//        token.setCode(verificationCode);
//        token.setExpiresAt(expiryTime);
//        token.setVerified(false);
//        token.setCreatedAt(LocalDateTime.now());
//        return token;
//    }
//
//    protected User mapToUser(RegistrationProgress progress, String password) {
//        log.info("creating user from RegistrationProgress.......");
//        User newUser = new User();
//        newUser.setEmail(progress.getEmail());
//        newUser.setUsername(progress.getUsername());
//        newUser.setMobile(progress.getMobile());
////        newUser.setPassword(passwordEncoder.encode(password));
//        newUser.setEmailVerified(true);
//        newUser.setCreatedAt(LocalDateTime.now());
//
//        // KYC Info
////        Kyc kyc = new Kyc();
////        kyc.setEmail(newUser.getEmail());
////        kyc.setPhone(newUser.getMobile());
////        kyc.setEmailVerifyStatus(progress.isEmailVerified() ? Kyc.EpaStatus.VERIFIED : Kyc.EpaStatus.UNVERIFIED);
////        kyc.setPhoneVerifyStatus(progress.isMobileVerified() ? Kyc.EpaStatus.VERIFIED : Kyc.EpaStatus.UNVERIFIED);
//        //newUser.setKycInfo(kyc);
//
//        return newUser;
//    }
//
//    protected boolean isValidEmail(String email) {
//        return email != null && email.matches("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,}$");
//    }
//
//    private String generateVerificationCode() {
//        return String.valueOf(new Random().nextInt(900_000) + 100_000); // 6-digit code
//    }
//
//    /*protected IpApiResponse getIpInfo(RegistrationProgress progress) {
//        try {
//            return objectMapper.readValue(progress.getIpDetailsJson(), IpApiResponse.class);
//        } catch (JsonProcessingException e) {
//            return null;
//        }
//    }*/
//
//    protected String getClientIp(HttpServletRequest request) {
//        String xfHeader = request.getHeader(HEADER_X_FORWARDED_FOR);
//        if (xfHeader != null) {
//            return xfHeader.split(",")[0];
//        }
//        return request.getRemoteAddr();
//    }
//
//
//
//
//    @SneakyThrows
//    @Async
//    protected void enrichWithIpDetails(RegistrationProgress progress, HttpServletRequest servletRequest) {
//        /*try {
//            IpApiResponse ipInfo = ipLocationService.fetchIpDetails(servletRequest.getRemoteAddr());
//
//            if (ipInfo != null && "success".equals(ipInfo.getStatus())) {
//                progress.setCountry(ipInfo.getCountry());
//                progress.setCity(ipInfo.getCity());
//                *//*progress.setRegion(ipInfo.getRegionName());
//                progress.setZip(ipInfo.getZip());
//                progress.setIsp(ipInfo.getIsp());
//                progress.setTimezone(ipInfo.getTimezone());
//                progress.setLatitude(ipInfo.getLat());
//                progress.setLongitude(ipInfo.getLon());*//*
//                String json = objectMapper.writeValueAsString(ipInfo);
//                progress.setIpDetailsJson(json);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }*/
//
//        /*String userAgentHeader = servletRequest.getHeader("User-Agent");
//        progress.setUserAgent(userAgentHeader);
//        progress.setReferrer(servletRequest.getHeader("Referer"));
//        try {
//            Client client = userAgentParser.parse(userAgentHeader);
//            progress.setDeviceType(client.device.family);
//            progress.setDeviceOs(client.os.family);
//            progress.setDeviceBrowser(client.userAgent.family + " " + client.userAgent.major);
//        } catch (Exception ignored) {}*/
//    }
//
//
//}
