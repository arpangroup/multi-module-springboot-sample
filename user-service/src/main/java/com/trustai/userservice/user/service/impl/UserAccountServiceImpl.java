package com.trustai.userservice.user.service.impl;

import com.trustai.common.domain.user.User;
import com.trustai.common.repository.user.UserRepository;
import com.trustai.userservice.user.exception.IdNotFoundException;
import com.trustai.userservice.user.service.UserAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAccountServiceImpl implements UserAccountService {
    private final UserRepository userRepository;


    @Override
    public User updateAccountStatus(Long userId, User.AccountStatus status) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IdNotFoundException("user not found"));
        user.setAccountStatus(status);
        return userRepository.save(user);
    }

    @Override
    public User updateTransactionStatus(Long userId, User.TransactionStatus depositStatus, User.TransactionStatus withdrawStatus, User.TransactionStatus sendMoneyStatus) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IdNotFoundException("user not found"));
        if (depositStatus != null) user.setDepositStatus(depositStatus);
        if (withdrawStatus != null) user.setWithdrawStatus(withdrawStatus);
        if (sendMoneyStatus != null) user.setSendMoneyStatus(sendMoneyStatus);
        return userRepository.save(user);
    }
}
