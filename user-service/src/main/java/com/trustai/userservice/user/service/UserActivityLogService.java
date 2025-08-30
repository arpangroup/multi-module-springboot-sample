package com.trustai.userservice.user.service;

import com.trustai.userservice.user.entity.UserActivityLog;
import com.trustai.userservice.user.repository.UserActivityLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserActivityLogService {
    private final UserActivityLogRepository repository;

    public void save(UserActivityLog log) {
        repository.save(log);
    }
}
