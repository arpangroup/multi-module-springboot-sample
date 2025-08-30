package com.trustai.userservice.user.repository;

import com.trustai.userservice.user.entity.Kyc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KycRepository extends JpaRepository<Kyc, Long> {
    List<Kyc> findByStatus(Kyc.KycStatus status);
    Page<Kyc> findByStatus(Kyc.KycStatus status, Pageable pageable);
}
