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
            new WithdrawRule(null, "RANK_0", 0, 1, BigDecimal.valueOf(100)),
            new WithdrawRule(null, "RANK_1", 2, 2, BigDecimal.valueOf(500)),
            new WithdrawRule(null, "RANK_2", 1, 3, BigDecimal.valueOf(1000)),
            new WithdrawRule(null, "RANK_3", 2, 4, BigDecimal.valueOf(3000)),
            new WithdrawRule(null, "RANK_4", 2, 6, BigDecimal.valueOf(5000)),
            new WithdrawRule(null, "RANK_5", 2, 8, BigDecimal.valueOf(10000)),
            new WithdrawRule(null, "RANK_6", 3, 12, BigDecimal.valueOf(20000)),
            new WithdrawRule(null, "RANK_7", 5, 15, BigDecimal.valueOf(30000))
        );
    }
}
