package com.trustai.income_service.listeners;

import com.trustai.common.enums.IncomeType;
import com.trustai.common.event.IncomeCreditedEvent;
import com.trustai.income_service.constant.Remarks;
import com.trustai.income_service.income.entity.IncomeHistory;
import com.trustai.income_service.income.repository.IncomeHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class IncomeCreditedEventListener {
    private final IncomeHistoryRepository incomeRepo;

    @EventListener
    public void handleStakeSoldEvent(IncomeCreditedEvent event) {
        log.info("Saving income of type {} of amount {} for user: {}...........", event.incomeType(), event.amount(), event.userId());

        IncomeHistory incomeHistory = IncomeHistory.builder()
                .userId(event.userId())
                .amount(event.amount())
                .incomeType(event.incomeType())
                .sourceUserId(event.userId())
                .note(Remarks.DAILY_INCOME) // Self income
                .build();
        IncomeHistory saved = incomeRepo.save(incomeHistory);
        log.info("✅ Income credited successfully | IncomeID: {}, Amount: {}, UserID: {}", saved.getId(), saved.getAmount(), saved.getUserId());
    }
}
