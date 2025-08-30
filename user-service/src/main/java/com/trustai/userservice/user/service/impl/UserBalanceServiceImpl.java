package com.trustai.userservice.user.service.impl;

import com.trustai.common.repository.user.UserRepository;
import com.trustai.userservice.user.service.UserBalanceService;
import com.trustai.userservice.user.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserBalanceServiceImpl implements UserBalanceService {
    private final UserRepository userRepository;
    private final UserProfileService profileService;

    @Override
    public Optional<BigDecimal> findWalletBalanceById(Long userId) {
        log.debug("Fetching wallet balance for userId: {}", userId);
        //return userRepository.findWalletBalanceById(userId);
        var user = profileService.getUserById(userId);
        log.info("Wallet balance retrieved for userId {}: {}", userId, user.getWalletBalance());
        return Optional.ofNullable(user.getWalletBalance());
    }

    @Override
    public void updateWalletBalance(long userId, BigDecimal updatedAmount) {
        log.info("Updating wallet balance for userId: {} to {}", userId, updatedAmount);
        userRepository.updateWalletBalance(userId, updatedAmount);
        log.debug("Wallet balance updated in repository for userId: {}", userId);
    }

   /* @Override
    public Optional<BigDecimal> findDepositBalanceById(Long userId) {
        var user = profileService.getUserById(userId);
        return Optional.ofNullable(user.getDepositBalance());
    }

    @Override
    public void updateDepositBalance(long userId, BigDecimal updatedTotalDepositAmount) {
        userRepository.updateDepositBalance(userId, updatedTotalDepositAmount);
    }*/
}
