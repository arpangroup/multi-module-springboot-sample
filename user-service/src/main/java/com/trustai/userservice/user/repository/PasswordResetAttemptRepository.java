package com.trustai.userservice.user.repository;

import com.trustai.userservice.user.entity.PasswordResetAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetAttemptRepository extends JpaRepository<PasswordResetAttempt, Long> {
    Optional<PasswordResetAttempt> findByEmail(String email);
    Optional<PasswordResetAttempt> findByEmailAndIpAddress(String email, String ipAddress);
}
