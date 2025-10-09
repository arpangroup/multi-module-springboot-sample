package com.trustai.transaction_service.entity;

import com.trustai.transaction_service.repository.WithdrawRuleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class WithdrawRuleDataInitializer {
    private final WithdrawRuleRepository withdrawRuleRepository;

    @PostConstruct
    public void init() {
        List<WithdrawRule> rules = getAllWithdrawRules();

        // Only save rules that do not already exist
        for (WithdrawRule rule : rules) {
            if (!withdrawRuleRepository.existsByRankCode(rule.getRankCode())) {
                withdrawRuleRepository.save(rule);
            }
        }

    }

    private List<WithdrawRule> getAllWithdrawRules() {
        return List.of(
                WithdrawRule.builder()
                        .rankCode("RANK_0")
                        .withdrawLimitFromWalletInPercentage(BigDecimal.ZERO)
                        .withdrawLimitFromProfitWalletInPercentage(BigDecimal.valueOf(50))
                        .maxWithdrawFromWallet(BigDecimal.valueOf(999999))
                        .maxWithdrawFromProfitWallet(BigDecimal.valueOf(999999))
                        .dailyWithdrawLimit(3)
                        .totalWalletWithdrawLimit(1)
                        .totalProfitWithdrawLimit(999999)
                        .build(),

                WithdrawRule.builder()
                        .rankCode("RANK_1")
                        .withdrawLimitFromWalletInPercentage(BigDecimal.ZERO)
                        .withdrawLimitFromProfitWalletInPercentage(BigDecimal.valueOf(50))
                        .maxWithdrawFromWallet(BigDecimal.valueOf(999999))
                        .maxWithdrawFromProfitWallet(BigDecimal.valueOf(999999))
                        .dailyWithdrawLimit(1)
                        .totalWalletWithdrawLimit(1)
                        .totalProfitWithdrawLimit(999999)
                        .build(),

                WithdrawRule.builder()
                        .rankCode("RANK_2")
                        .withdrawLimitFromWalletInPercentage(BigDecimal.valueOf(50))
                        .withdrawLimitFromProfitWalletInPercentage(BigDecimal.valueOf(50))
                        .maxWithdrawFromWallet(BigDecimal.valueOf(999999))
                        .maxWithdrawFromProfitWallet(BigDecimal.valueOf(999999))
                        .dailyWithdrawLimit(5)
                        .totalWalletWithdrawLimit(100)
                        .totalProfitWithdrawLimit(999999)
                        .build(),

                WithdrawRule.builder()
                        .rankCode("RANK_3")
                        .withdrawLimitFromWalletInPercentage(BigDecimal.valueOf(100))
                        .withdrawLimitFromProfitWalletInPercentage(BigDecimal.valueOf(100))
                        .maxWithdrawFromWallet(BigDecimal.valueOf(999999))
                        .maxWithdrawFromProfitWallet(BigDecimal.valueOf(999999))
                        .dailyWithdrawLimit(1)
                        .totalWalletWithdrawLimit(100)
                        .totalProfitWithdrawLimit(999999)
                        .build(),

                WithdrawRule.builder()
                        .rankCode("RANK_4")
                        .withdrawLimitFromWalletInPercentage(BigDecimal.valueOf(100))
                        .withdrawLimitFromProfitWalletInPercentage(BigDecimal.valueOf(100))
                        .maxWithdrawFromWallet(BigDecimal.valueOf(999999))
                        .maxWithdrawFromProfitWallet(BigDecimal.valueOf(999999))
                        .dailyWithdrawLimit(1)
                        .totalWalletWithdrawLimit(100)
                        .totalProfitWithdrawLimit(999999)
                        .build(),

                WithdrawRule.builder()
                        .rankCode("RANK_5")
                        .withdrawLimitFromWalletInPercentage(BigDecimal.valueOf(100))
                        .withdrawLimitFromProfitWalletInPercentage(BigDecimal.valueOf(100))
                        .maxWithdrawFromWallet(BigDecimal.valueOf(999999))
                        .maxWithdrawFromProfitWallet(BigDecimal.valueOf(999999))
                        .dailyWithdrawLimit(1)
                        .totalWalletWithdrawLimit(100)
                        .totalProfitWithdrawLimit(999999)
                        .build(),

                WithdrawRule.builder()
                        .rankCode("RANK_6")
                        .withdrawLimitFromWalletInPercentage(BigDecimal.valueOf(100))
                        .withdrawLimitFromProfitWalletInPercentage(BigDecimal.valueOf(100))
                        .maxWithdrawFromWallet(BigDecimal.valueOf(999999))
                        .maxWithdrawFromProfitWallet(BigDecimal.valueOf(999999))
                        .dailyWithdrawLimit(1)
                        .totalWalletWithdrawLimit(99999)
                        .totalProfitWithdrawLimit(999999)
                        .build(),

                WithdrawRule.builder()
                        .rankCode("RANK_7")
                        .withdrawLimitFromWalletInPercentage(BigDecimal.valueOf(100))
                        .withdrawLimitFromProfitWalletInPercentage(BigDecimal.valueOf(100))
                        .maxWithdrawFromWallet(BigDecimal.valueOf(999999))
                        .maxWithdrawFromProfitWallet(BigDecimal.valueOf(999999))
                        .dailyWithdrawLimit(1)
                        .totalWalletWithdrawLimit(99999)
                        .totalProfitWithdrawLimit(999999)
                        .build()
        );
    }
}
