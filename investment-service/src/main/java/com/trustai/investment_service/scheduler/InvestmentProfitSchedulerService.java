package com.trustai.investment_service.scheduler;

import com.trustai.common.api.WalletApi;
import com.trustai.investment_service.entity.UserInvestment;
import com.trustai.investment_service.repository.UserInvestmentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvestmentProfitSchedulerService {
    private final UserInvestmentRepository investmentRepo;
    private final InvestmentPayoutProcessorV1 payoutProcessor;
    private final WalletApi walletApi;

    @Transactional
    @Scheduled(cron = "0 0 2 * * *") // auto run daily at 2 AM
    public void autoProcessProfitsAndMaturities() {
        log.info("---- Running automatic daily investment profit scheduler ----");
        processDailyProfits();
        processMaturedInvestments();
    }

    /** Manually trigger daily profit payout */
    @Transactional
    public void processDailyProfits() {
        LocalDateTime now = LocalDateTime.now();
        List<UserInvestment> dueInvestments = investmentRepo.findDueInvestments(now);
        log.info("Found {} investments due for daily profit payout", dueInvestments.size());

        for (UserInvestment inv : dueInvestments) {
            try {
                payoutProcessor.processProfit(inv);
            } catch (Exception e) {
                log.error("❌ Failed to process profit for investment {}: {}", inv.getId(), e.getMessage());
            }
        }
    }

    /** Manually trigger maturity processing */
    @Transactional
    public void processMaturedInvestments() {
        LocalDateTime now = LocalDateTime.now();
        List<UserInvestment> maturedList = investmentRepo.findMaturedInvestments(now);
        log.info("Found {} matured investments", maturedList.size());

        for (UserInvestment inv : maturedList) {
            try {
                payoutProcessor.settleMaturedInvestment(inv);
            } catch (Exception e) {
                log.error("❌ Failed to settle matured investment {}: {}", inv.getId(), e.getMessage());
            }
        }
    }
}
