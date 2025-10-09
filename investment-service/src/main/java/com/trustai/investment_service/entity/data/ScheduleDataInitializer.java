package com.trustai.investment_service.entity.data;

import com.trustai.investment_service.entity.Schedule;
import com.trustai.investment_service.repository.ScheduleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("scheduleData")
@RequiredArgsConstructor
public class ScheduleDataInitializer {
    private final ScheduleRepository scheduleRepository;

    @PostConstruct
    public void init() {
        createIfNotExists("Hourly", 60, "Every hour");
        createIfNotExists("Daily", 24 * 60, "Every day");
        createIfNotExists("Weekly", 7 * 24 * 60, "Every week");
        createIfNotExists("2 Week", 14 * 24 * 60, "Every 2 weeks");
        createIfNotExists("Monthly", 30 * 24 * 60, "Every month (approx)");
        createIfNotExists("No Schedule", 100 * 30 * 24 * 60, "100Yrs (approx)");
    }

    private void createIfNotExists(String name, int intervalMinutes, String description) {
        if (!scheduleRepository.existsByScheduleName(name)) {
            Schedule schedule = new Schedule(null, name, intervalMinutes, description);
            scheduleRepository.save(schedule);
        }
    }
}
