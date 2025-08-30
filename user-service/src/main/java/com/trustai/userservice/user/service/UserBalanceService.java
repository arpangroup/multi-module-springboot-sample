package com.trustai.userservice.user.service;

import java.math.BigDecimal;
import java.util.Optional;

public interface UserBalanceService {
    Optional<BigDecimal> findWalletBalanceById(Long userId);
    void updateWalletBalance(long userId, BigDecimal updatedAmount);
    /*Optional<BigDecimal> findDepositBalanceById(Long userId);
    void updateDepositBalance(long userId, BigDecimal updatedTotalDepositAmount);*/
}
