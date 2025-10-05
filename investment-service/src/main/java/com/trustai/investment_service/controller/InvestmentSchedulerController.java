/*
package com.trustai.investment_service.controller;

import com.trustai.investment_service.scheduler.InvestmentProfitSchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/investments/scheduler")
@RequiredArgsConstructor
@Slf4j
public class InvestmentSchedulerController {
    private final InvestmentProfitSchedulerService schedulerService;

    @PostMapping("/run-daily")
    public ResponseEntity<String> runDailyProfit() {
        schedulerService.processDailyProfits();
        return ResponseEntity.ok("✅ Manual daily profit processing executed successfully.");
    }

    @PostMapping("/run-maturity")
    public ResponseEntity<String> runMaturityProcessing() {
        schedulerService.processMaturedInvestments();
        return ResponseEntity.ok("✅ Manual maturity processing executed successfully.");
    }

    @PostMapping("/run-all")
    public ResponseEntity<String> runAllSchedulers() {
        schedulerService.processDailyProfits();
        schedulerService.processMaturedInvestments();
        return ResponseEntity.ok("✅ Manual daily + maturity processing executed successfully.");
    }

}
*/
