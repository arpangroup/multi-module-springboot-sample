package com.trustai.transaction_service.controller;

import com.trustai.transaction_service.config.WithdrawRuleConfigCache;
import com.trustai.transaction_service.entity.WithdrawRule;
import com.trustai.transaction_service.repository.WithdrawRuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/withdraw-rules")
@RequiredArgsConstructor
@Slf4j
public class WithdrawRuleConfigController {
    private final WithdrawRuleRepository withdrawRuleRepository;
    private final WithdrawRuleConfigCache withdrawRuleConfigCache;

    @GetMapping
    public List<WithdrawRule> getAllRules() {
        return withdrawRuleConfigCache.findAll();
    }


    @PatchMapping("/update")
    public ResponseEntity<?> updateRules(@RequestBody List<WithdrawRule> updatedRules) {
        for (WithdrawRule updated : updatedRules) {
            withdrawRuleRepository.findById(updated.getId()).ifPresent(existing -> {
//                if (updated.getRequiredTotalMembers() != 0) {
//                    existing.setRequiredTotalMembers(updated.getRequiredTotalMembers());
//                }
                if (updated.getRequiredDirectReferrals() != 0) {
                    existing.setRequiredDirectReferrals(updated.getRequiredDirectReferrals());
                }
                if (updated.getWithdrawLimit() != 0) {
                    existing.setWithdrawLimit(updated.getWithdrawLimit());
                }
                if (updated.getMaxWithdrawAmount() != null) {
                    existing.setMaxWithdrawAmount(updated.getMaxWithdrawAmount());
                }
                withdrawRuleRepository.save(existing);
            });
        }
        return ResponseEntity.ok().build();
    }
}
