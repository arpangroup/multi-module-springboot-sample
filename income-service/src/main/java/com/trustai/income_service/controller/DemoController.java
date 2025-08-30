package com.trustai.income_service.controller;

import com.trustai.income_service.income.service.IncomeDistributionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/dailyIncome")
@RequiredArgsConstructor
@Slf4j
public class DemoController {
    private final IncomeDistributionService incomeDistributionService;

    @GetMapping
    public void schedule(@RequestParam("sellerId") Long sellerId, @RequestParam("sellAmount") BigDecimal sellAmount) {
        incomeDistributionService.distributeIncome(sellerId, sellAmount);
    }
}
