//package com.trustai.product_service.scheduler;
//
//import com.trustai.investment_service.entity.UserInvestment;
//import com.trustai.investment_service.repository.UserInvestmentRepository;
//import com.trustai.product_service.service.InvestmentProfitCalculator;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.hibernate.Transaction;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class InvestmentPayoutProcessor {
//    private final UserInvestmentRepository investmentRepository;
//    private final InvestmentProfitCalculator profitCalculator;
//    private final BonusTransactionService bonusTransactionService;
//
//
//    @Scheduled(cron = "0 0 * * * *") // every hour — adjust as needed
//    public void processInvestmentReturns() {
//        LocalDateTime now = LocalDateTime.now();
//        List<UserInvestment> activeInvestments = userInvestmentRepository.findActiveInvestmentsDueForPayout(LocalDateTime.now());
//
//        for (UserInvestment investment : activeInvestments) {
//            BigDecimal interest = investmentProfitCalculator.calculateProfit(investment);
//
//            if (interest.compareTo(BigDecimal.ZERO) > 0) {
//                Transaction txn = bonusTransactionService.applyInterest(
//                        investment.getUserId(),
//                        interest,
//                        "Return for " + LocalDate.now()
//                );
//
//                investment.setReceivedReturnAmount(
//                        investment.getReceivedReturnAmount().add(interest)
//                );
//                investment.setLastPayoutAt(LocalDateTime.now());
//                investment.setNextPayoutAt(investment.getNextPayoutAt().plusMinutes(
//                        investment.getSchema().getReturnSchedule().getIntervalMinutes()
//                ));
//
//                // If fully paid:
//                if (investment.isCompleted()) {
//                    investment.setStatus(InvestmentStatus.COMPLETED);
//                }
//
//                userInvestmentRepository.save(investment);
//            }
//        }
//    }
//
//    /*@Scheduled(cron = "0 0 * * * *") // every hour — adjust based on schedule interval
//    public void processDueInvestmentReturns() {
//        LocalDateTime now = LocalDateTime.now();
//
//        List<UserInvestment> dueInvestments = investmentRepository.findActiveInvestmentsDueForPayout(now);
//
//        for (UserInvestment investment : dueInvestments) {
//            try {
//                processSingleInvestmentReturn(investment);
//            } catch (Exception ex) {
//                log.error("Failed to process investment ID {}: {}", investment.getId(), ex.getMessage(), ex);
//            }
//        }
//    }*/
//
//    private void processSingleInvestmentReturn(UserInvestment investment) {
//        BigDecimal profit = profitCalculator.calculateProfit(investment);
//
//        if (profit.compareTo(BigDecimal.ZERO) <= 0) {
//            log.warn("Skipping zero-profit investment for user {}", investment.getUserId());
//            return;
//        }
//
//        Transaction txn = bonusTransactionService.applyInterest(
//                investment.getUserId(),
//                profit,
//                "Investment ID #" + investment.getId()
//        );
//
//        // Update investment record
//        investment.setReceivedReturnAmount(
//                investment.getReceivedReturnAmount().add(profit)
//        );
//
//        investment.setLastPayoutAt(LocalDateTime.now());
//        investment.setNextPayoutAt(
//                investment.getNextPayoutAt().plusMinutes(
//                        investment.getSchema().getReturnSchedule().getIntervalMinutes()
//                )
//        );
//
//        if (isInvestmentCompleted(investment)) {
//            investment.setStatus(InvestmentStatus.COMPLETED);
//        }
//
//        investmentRepository.save(investment);
//        log.info("Credited investment return of ₹{} to user {} [txnId={}]", profit, investment.getUserId(), txn.getId());
//    }
//
//    private boolean isInvestmentCompleted(UserInvestment investment) {
//        int total = investment.getSchema().getTotalReturnPeriods();
//        int paid = investment.getCompletedPeriods(); // You can calculate from timestamps if needed
//        return paid + 1 >= total;
//    }
//
//}
