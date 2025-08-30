package com.trustai.investment_service.service.impl;

import com.trustai.common.api.RankConfigApi;
import com.trustai.common.api.WalletApi;
import com.trustai.common.dto.RankConfigDto;
import com.trustai.common.dto.UserInfo;
import com.trustai.common.exceptions.ErrorCode;
import com.trustai.common.exceptions.ValidationException;
import com.trustai.investment_service.entity.InvestmentSchema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;


@Component
@RequiredArgsConstructor
@Slf4j
public class InvestmentValidator {
    private final RankConfigApi rankConfigClient;
    private final WalletApi walletClient;

    public void validateEligibility(UserInfo user, InvestmentSchema schema, BigDecimal investmentAmount) {
        log.debug("Validating eligibility for userId={}, schemaId={}, amount={}", user.getId(), schema.getId(), investmentAmount);
        validateAmountAgainstSchema(schema, investmentAmount);
        validateInvestAmountEligibility(user, investmentAmount);
        validateUserEligibilityAgainstSchema(user, schema);
        log.debug("User {} passed all eligibility validations", user.getId());
    }

    private void validateAmountAgainstSchema(InvestmentSchema schema, BigDecimal amount) {
        log.debug("Validating amount against schema: schemaId={}, schemaType={}, amount={}", schema.getId(), schema.getSchemaType(), amount);
        if (!schema.isActive()) {
            log.warn("Schema {} is inactive", schema.getId());
            throw new ValidationException("Schema is inactive");
        }

        if (amount == null) {
            log.warn("Amount is null for schema {}", schema.getId());
            throw new ValidationException("Invalid amount, should not be null");
        }

        switch (schema.getSchemaType()) {
            case FIXED:
                if (amount.compareTo(schema.getMinimumInvestmentAmount()) != 0) {
                    log.warn("Amount {} does not match fixed minimum {}", amount, schema.getMinimumInvestmentAmount());
                    throw new ValidationException("Amount must be exactly " + schema.getMinimumInvestmentAmount(), ErrorCode.FIXED_AMOUNT_MISMATCH);
                }
                break;
            case RANGE:
                if (amount.compareTo(schema.getMinimumInvestmentAmount()) < 0 || amount.compareTo(schema.getMaximumInvestmentAmount()) > 0) {
                    log.warn("Amount {} not within range [{}, {}]", amount, schema.getMinimumInvestmentAmount(), schema.getMaximumInvestmentAmount());
                    throw new ValidationException("Amount must be between " + schema.getMinimumInvestmentAmount() + " and " + schema.getMaximumInvestmentAmount(), ErrorCode.INVESTMENT_AMOUNT_OUT_OF_RANGE);
                }
                break;
            default:
                log.warn("Unknown schema type for schema {}", schema.getId());
                throw new ValidationException("Unknown schema type", ErrorCode.UNKNOWN_SCHEMA_TYPE);
        }

        log.debug("Amount {} is valid for schema {}", amount, schema.getId());
    }

    private void validateInvestAmountEligibility(UserInfo user, BigDecimal investmentAmount) {
        log.debug("Validating wallet and rank eligibility for userId={}, rankCode={}, amount={}", user.getId(), user.getRankCode(), investmentAmount);

        // Wallet Check
        //BigDecimal walletBalance = walletClient.getWalletBalance(user.getId());
        BigDecimal walletBalance = user.getWalletBalance();
        if (walletBalance.compareTo(investmentAmount) < 0) {
            log.warn("User {} has insufficient balance: required={}, actual={}", user.getId(), investmentAmount, walletBalance);
            throw new ValidationException("Insufficient wallet balance", ErrorCode.INSUFFICIENT_BALANCE);
        }

        // Rank config check
        //RankConfig userRank = rankService.getUserRank(user.getId());
        RankConfigDto rankConfig = rankConfigClient.getRankConfigByRankCode(user.getRankCode());
        if (rankConfig == null) {
            log.error("RankConfig not found for rankCode={}", user.getRankCode());
            throw new ValidationException("Invalid rank configuration", ErrorCode.INVALID_RANK_CONFIG);
        }

        if (investmentAmount.compareTo(rankConfig.getMinInvestmentAmount()) < 0) {
            log.warn("Amount {} is below min required {} for user rank {}", investmentAmount, rankConfig.getMinInvestmentAmount(), user.getRankCode());
            throw new ValidationException("Doesn't meet min investment for current rank, minimum investment should be = " + rankConfig.getMinInvestmentAmount(), ErrorCode.MIN_INVESTMENT_NOT_MET);
        }
        log.debug("User {} passed wallet and rank eligibility", user.getId());
    }

    private void validateUserEligibilityAgainstSchema(UserInfo user, InvestmentSchema schema) {
        log.debug("Validating user level against schema: userId={}, rankCode={}, schemaId={}", user.getId(), user.getRankCode(), schema.getId());

        if (!schema.getParticipationLevels().isEmpty() && !schema.getParticipationLevels().contains(user.getRankCode())) {
            log.warn("User {} with rankCode={} not in schema's participation levels", user.getId(), user.getRankCode());
            throw new ValidationException("User level not eligible for this stake", ErrorCode.USER_RANK_NOT_IN_PARTICIPATION_LEVELS);
        }

        if (schema.getLinkedRank() != null && !schema.getLinkedRank().equals(user.getRankCode())) {
            log.warn("User {} rankCode={} does not match schema's linked rank {}", user.getId(), user.getRankCode(), schema.getLinkedRank());
            throw new ValidationException("User rank not eligible for this stake", ErrorCode.USER_RANK_MISMATCH_LINKED_RANK );
        }
        log.debug("User {} passed schema-specific eligibility checks", user.getId());
    }
}
