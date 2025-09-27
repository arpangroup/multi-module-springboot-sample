package com.trustai.transaction_service.config;

import com.trustai.common.lifecycle.Reloadable;
import com.trustai.transaction_service.entity.WithdrawRule;
import com.trustai.transaction_service.repository.WithdrawRuleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Slf4j
public class WithdrawRuleConfigCache implements Reloadable {
    private final WithdrawRuleRepository withdrawRuleRepository;
    private final Map<String, WithdrawRule> withdrawRuleCache = new HashMap<>();

    @Override
    public void reload() {
        preload();
    }

    @PostConstruct
    public void preload() {
        log.info("Preloading rank configs into cache...");
        List<WithdrawRule> rules = withdrawRuleRepository.findAll();
        this.withdrawRuleCache.clear();
        rules.forEach(r -> withdrawRuleCache.put(r.getRankCode(), r));
    }

    public WithdrawRule getByRankCode(String code) {
        WithdrawRule rule = withdrawRuleCache.get(code);
        if (rule == null) {
            throw new IllegalArgumentException("No withdraw rule found for rank code: " + code);
        }
        return rule;
    }
}
