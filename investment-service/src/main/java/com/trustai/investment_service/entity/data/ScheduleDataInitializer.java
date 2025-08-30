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
        Schedule scheduleHourly = new Schedule(null, "Hourly", 60, "Every hour");                     // 60 minutes
        Schedule scheduleDaily = new Schedule(null, "Daily", 24 * 60, "Every day");                  // 1440 minutes
        Schedule scheduleWeekly = new Schedule(null, "Weekly", 7 * 24 * 60, "Every week");            // 10080 minutes
        Schedule schedule2Weekly = new Schedule(null, "2 Week", 14 * 24 * 60, "Every 2 weeks");        // 20160 minutes <== 14 days
        Schedule scheduleMonthly = new Schedule(null, "Monthly", 30 * 24 * 60, "Every month (approx)");// 43200 minutes
        Schedule noSchedule = new Schedule(null, "No Schedule", 100 * 30 * 24 * 60, "100Yrs (approx)");// 43200 minutes

        scheduleHourly = scheduleRepository.save(scheduleHourly);
        scheduleDaily = scheduleRepository.save(scheduleDaily);
        scheduleWeekly = scheduleRepository.save(scheduleWeekly);
        schedule2Weekly = scheduleRepository.save(schedule2Weekly);
        scheduleMonthly = scheduleRepository.save(scheduleMonthly);
        noSchedule = scheduleRepository.save(noSchedule);
    }
}
