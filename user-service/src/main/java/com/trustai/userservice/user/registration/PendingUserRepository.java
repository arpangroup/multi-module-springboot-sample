package com.trustai.userservice.user.registration;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PendingUserRepository extends JpaRepository<PendingUser, Long> {
    Optional<PendingUser> findByEmail(String email);
    Optional<PendingUser> findByMobile(String mobile);
    Optional<PendingUser> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
