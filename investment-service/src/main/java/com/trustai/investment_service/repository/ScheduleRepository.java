package com.trustai.investment_service.repository;

import com.trustai.investment_service.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    Schedule findByScheduleNameIgnoreCase(String scheduleName);
}
