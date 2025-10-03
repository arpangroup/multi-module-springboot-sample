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
                existing.setWithdrawLimitFromWalletInPercentage(updated.getWithdrawLimitFromWalletInPercentage());
                existing.setWithdrawLimitFromProfitWalletInPercentage(updated.getWithdrawLimitFromProfitWalletInPercentage());
                existing.setMaxWithdrawFromWallet(updated.getMaxWithdrawFromWallet());
                existing.setMaxWithdrawFromProfitWallet(updated.getMaxWithdrawFromProfitWallet());
                existing.setServiceCharge(updated.getServiceCharge());
                existing.setDailyWithdrawLimit(updated.getDailyWithdrawLimit());
                existing.setTotalWithdrawLimit(updated.getTotalWithdrawLimit());
                withdrawRuleRepository.save(existing);
            });
        }
        return ResponseEntity.ok().build();
    }

    /*@PatchMapping("/update")
    public ResponseEntity<?> updateRules(@RequestBody List<WithdrawRule> updatedRules) {
        for (WithdrawRule updated : updatedRules) {
            withdrawRuleRepository.findById(updated.getId()).ifPresent(existing -> {
                if (updated.getWithdrawLimitFromWalletInPercentage() != null) {
                    existing.setWithdrawLimitFromWalletInPercentage(updated.getWithdrawLimitFromWalletInPercentage());
                }
                if (updated.getWithdrawLimitFromProfitWalletInPercentage() != null) {
                    existing.setWithdrawLimitFromProfitWalletInPercentage(updated.getWithdrawLimitFromProfitWalletInPercentage());
                }
                if (updated.getMaxWithdrawFromWallet() != null) {
                    existing.setMaxWithdrawFromWallet(updated.getMaxWithdrawFromWallet());
                }
                if (updated.getMaxWithdrawFromProfitWallet() != null) {
                    existing.setMaxWithdrawFromProfitWallet(updated.getMaxWithdrawFromProfitWallet());
                }
                if (updated.getServiceCharge() != null) {
                    existing.setServiceCharge(updated.getServiceCharge());
                }
                if (updated.getDailyWithdrawLimit() > 0) {
                    existing.setDailyWithdrawLimit(updated.getDailyWithdrawLimit());
                }
                if (updated.getTotalWithdrawLimit() > 0) {
                    existing.setTotalWithdrawLimit(updated.getTotalWithdrawLimit());
                }
                withdrawRuleRepository.save(existing);
            });
        }
        return ResponseEntity.ok().build();
    }*/
}
