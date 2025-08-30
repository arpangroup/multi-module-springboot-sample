package com.trustai.common.auth.repository;

import com.trustai.common.auth.entity.OtpSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OtpSessionRepository extends JpaRepository<OtpSessionEntity, String> {
    Optional<OtpSessionEntity> findByUsername(String username);
    List<OtpSessionEntity> findAllByUsernameOrderByCreatedAtDesc(String username);
}
