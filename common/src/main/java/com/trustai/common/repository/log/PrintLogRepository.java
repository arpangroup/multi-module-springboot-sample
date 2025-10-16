package com.trustai.common.repository.log;

import com.trustai.common.domain.log.PrintLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrintLogRepository extends JpaRepository<PrintLog, Long> {
    Page<PrintLog> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}
