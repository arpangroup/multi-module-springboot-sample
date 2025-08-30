package com.trustai.income_service.listeners;


import com.trustai.common.event.StakeSoldEvent;
import com.trustai.income_service.income.service.IncomeDistributionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StakeSoldEventListener {
    private final IncomeDistributionService incomeDistributionService;

    @EventListener
    public void handleStakeSoldEvent(StakeSoldEvent event) {
        log.info("Received StakeSoldEvent: sellerId={}, saleAmount={}", event.getSellerId(), event.getSaleAmount());
        incomeDistributionService.distributeIncome(event.getSellerId(), event.getSaleAmount());
    }
}
