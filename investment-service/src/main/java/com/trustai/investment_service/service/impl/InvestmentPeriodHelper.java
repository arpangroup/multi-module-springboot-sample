package com.trustai.investment_service.service.impl;

import com.trustai.investment_service.entity.InvestmentSchema;
import com.trustai.investment_service.entity.Schedule;
import com.trustai.investment_service.entity.UserInvestment;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;

@Component
public class InvestmentPeriodHelper {

    public int calculateCompletedPeriods(UserInvestment investment) {
        InvestmentSchema schema = investment.getSchema();
        Schedule schedule = schema.getReturnSchedule();

        LocalDateTime start = investment.getSubscribedAt();
        LocalDateTime now = LocalDateTime.now();

        if (start == null || schedule == null) return 0;

        long elapsedMinutes = Duration.between(start, now).toMinutes();
        int interval = schedule.getIntervalMinutes();

        return (int) Math.min(
                elapsedMinutes / interval,
                schema.getTotalReturnPeriods()
        );
    }

    public LocalDateTime calculateNextPayoutDate(UserInvestment investment) {
        int completed = calculateCompletedPeriods(investment);
        int interval = investment.getSchema().getReturnSchedule().getIntervalMinutes();
        return investment.getSubscribedAt().plusMinutes((long) interval * (completed + 1));
    }

    public LocalDateTime calculateNextPayoutDateV1(UserInvestment investment) {
        InvestmentSchema schema = investment.getSchema();
        LocalDateTime now = LocalDateTime.now();

        switch (schema.getPayoutMode()) {
            case WEEKLY:
                return nextMatchingDayOfWeek(now, schema.getPayoutDays());
            case MONTHLY:
                return nextMatchingDayOfMonth(now, schema.getPayoutDates());
            default:
                // fallback to interval-based schedule
                return investment.getSubscribedAt().plusMinutes(
                        schema.getReturnSchedule().getIntervalMinutes()
                );
        }
    }

    public LocalDateTime calculateMaturityDate(UserInvestment investment) {
        int totalPeriods = investment.getSchema().getTotalReturnPeriods();
        int interval = investment.getSchema().getReturnSchedule().getIntervalMinutes();
        return investment.getSubscribedAt().plusMinutes((long) interval * totalPeriods);
    }

    public int calculateRemainingPeriods(UserInvestment investment) {
        int total = investment.getSchema().getTotalReturnPeriods();
        int completed = calculateCompletedPeriods(investment);
        return Math.max(0, total - completed);
    }

    public long calculateRemainingMinutesToNextPayout(UserInvestment investment) {
        Schedule schedule = investment.getSchema().getReturnSchedule();
        if (schedule == null || investment.getSubscribedAt() == null) return 0;

        int interval = schedule.getIntervalMinutes();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextPayout = investment.getSubscribedAt()
                .plusMinutes(interval * calculateCompletedPeriods(investment) + interval);

        return Duration.between(now, nextPayout).toMinutes();
    }

    public boolean isCancellableNow(UserInvestment investment) {
        if (!investment.getSchema().isCancellable()) return false;

        long grace = investment.getSchema().getCancellationGracePeriodMinutes();
        LocalDateTime eligibleFrom = investment.getSubscribedAt().plusMinutes(grace);
        return LocalDateTime.now().isAfter(eligibleFrom);
    }

    private LocalDateTime nextMatchingDayOfWeek(LocalDateTime from, Set<DayOfWeek> days) {
        for (int i = 0; i < 7; i++) {
            LocalDateTime candidate = from.plusDays(i);
            if (days.contains(candidate.getDayOfWeek())) return candidate;
        }
        return from.plusDays(7); // fallback
    }

    private LocalDateTime nextMatchingDayOfMonth(LocalDateTime from, Set<Integer> days) {
        for (int i = 0; i < 31; i++) {
            LocalDateTime candidate = from.plusDays(i);
            if (days.contains(candidate.getDayOfMonth())) return candidate;
        }
        return from.plusDays(30); // fallback
    }
}
