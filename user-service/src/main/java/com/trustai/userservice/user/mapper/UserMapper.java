package com.trustai.userservice.user.mapper;

import com.trustai.common.domain.user.User;
import com.trustai.common.dto.*;
import com.trustai.common.utils.DateUtils;
import com.trustai.common.utils.IdConverter;
import com.trustai.common.utils.PhoneMaskingUtil;
import com.trustai.userservice.user.entity.Kyc;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    /*public UserInfoOld mapTo(User user) {
        return UserInfoOld.builder()
                .id(user.getId())
                .referralCode(user.getReferralCode())
                .walletBalance(user.getWalletBalance())
                .username(user.getUsername())
                .level(user.getRank())
//                .firstname(user.getFirstname())
//                .lastname(user.getLastname())
//                .email(user.getEmail())
//                .mobile(user.getMobile())
//                .referBy(user.getReferBy())
                .build();
    }*/

    public UserInfo mapTo(User user) {
        String accountId = IdConverter.encode(user.getId());
        return UserInfo.builder()
                .id(user.getId())
                .accountId(accountId)
                .username(user.getUsername())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .mobile(user.getMobile())
                .rankCode(user.getRankCode())
                .point(user.getPoint())
                .image(user.getImage())
                .country(user.getCountry())
                .walletAddress(user.getWalletAddress())
                // Balance:
                .walletBalance(user.getWalletBalance())
                .profitBalance(user.getProfitBalance())
                // Referral:
                .referralCode(user.getReferralCode())
                // Status:
                .isActive(user.getAccountStatus() == User.AccountStatus.ACTIVE)
                .accountStatus(user.getAccountStatus().name())
                //.kycStatus(user.getKycInfo().getStatus().name())
                // AuditLog
                .createdAt(user.getCreatedAt())
                .build();
    }

    public UserDetailsInfo mapToDetails(User user) {
        final User referrer = user.getReferrer();
        //String referrerId = IdConverter.encode(referrer.getId());
        return UserDetailsInfo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .phone(PhoneMaskingUtil.maskPhoneNumber(user.getMobile()))
                // Balance:
                .walletBalance(user.getWalletBalance())
                .profitBalance(user.getProfitBalance())
                // Referral:
                .referralCode(user.getReferralCode())
                .referrer(referrer == null ? null : new UserInfo(user.getId(), referrer.getUsername()))
                .rankCode(user.getRankCode())
                // KYC:
                //.kyc(convert(user.getKycInfo()))
                // Status:
                .accountStatus(convert(user))
                // AuditLog
                .createdAt(DateUtils.formatDisplayDate(user.getCreatedAt()))
                .build();
    }

    private AccountStatus convert(User user) {
        //Kyc kyc = getKycInfo(user);
        return AccountStatus.builder()
                .isAccountActive(user.getAccountStatus() == User.AccountStatus.ACTIVE)
                //.isKycVerified(kyc.status == Kyc.KycStatus.VERIFIED)
                //.isEmailVerified(kyc.getEmailVerifyStatus() == Kyc.EpaStatus.VERIFIED)
                //.isPhoneVerified(kyc.getPhoneVerifyStatus() == Kyc.EpaStatus.VERIFIED)

                .isDepositEnabled(user.depositStatus == User.TransactionStatus.ENABLED)
                .isWithdrawEnabled(user.withdrawStatus == User.TransactionStatus.ENABLED)
                .isSendMoneyEnabled(user.sendMoneyStatus == User.TransactionStatus.ENABLED)

                .accountStatus(user.accountStatus.name())
                //.kycStatus(kyc.status.name())
                //.emailVerifyStatus(kyc.getEmailVerifyStatus().name())
                //.phoneVerifyStatus(kyc.getPhoneVerifyStatus().name())

                //.kycRejectionReason(kyc.getKycRejectionReason())
                .build();
    }

    public KycInfo convert(Kyc kyc) {
        return KycInfo.builder()
                .kycId(kyc.getId())
                .email(kyc.getEmail())
                .phone(kyc.getPhone())
                .address(kyc.getAddress())
                .address(kyc.getAddress())
                .documentType(kyc.getDocumentType().name())
                .emailVerifyStatus(kyc.getEmailVerifyStatus().name())
                .phoneVerifyStatus(kyc.getPhoneVerifyStatus().name())
                .status(kyc.status.name())
                .kycRejectionReason(kyc.getKycRejectionReason())
                .build();
    }

//    public User mapTo(UserInfoOld info) {
//        return null;
//    }

    private Kyc getKycInfo(User user) {
        //Kyc kyc = user.getKycInfo();
        return null;
    }
}
