package com.trustai.userservice.user.service.impl;

import com.trustai.common.domain.user.User;
import com.trustai.common.repository.user.UserRepository;
import com.trustai.userservice.user.exception.IdNotFoundException;
import com.trustai.userservice.user.service.UserReferralService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserReferralServiceImpl implements UserReferralService {
    private final UserRepository userRepo;

    @Override
    public User getUserById(Long userId) {
        return userRepo.findById(userId).orElseThrow(()-> new IdNotFoundException("userId: " + userId + " not found"));
    }

    @Override
    public List<User> getUserByIds(List<Long> userIds) {
        return userRepo.findByIdIn(userIds);
    }

    @Override
    public User getUserByReferralCode(String referralCode) {
        return userRepo.findByReferralCode(referralCode).orElseThrow(() -> new IdNotFoundException("invalid referralCode"));
    }

    @Override
    public void approveReferralBonus(Long userId) {

    }
}
