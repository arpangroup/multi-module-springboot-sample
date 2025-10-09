package com.trustai.investment_service.entity;

import com.trustai.common.enums.CurrencyType;
import com.trustai.investment_service.dto.SchemaRequest;
import com.trustai.investment_service.enums.InterestCalculationType;
import com.trustai.investment_service.enums.InvestmentType;
import com.trustai.investment_service.enums.ReturnType;
import com.trustai.investment_service.enums.SchemaType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.trustai.investment_service.enums.InvestmentType.STAKE;

public class SchemaBuilder {

    private static final BigDecimal DEFAULT_HANDLING_FEE = BigDecimal.ZERO;
    private static final BigDecimal DEFAULT_WITHDRAWAL_AMOUNT = BigDecimal.ZERO;
    private static final int DEFAULT_CANCELLATION_GRACE_MINUTES = 300;

    private InvestmentSchema schema;

    private SchemaBuilder() {
        schema = new InvestmentSchema();

        // Default Values
        schema.setInterestCalculationMethod(InterestCalculationType.PERCENTAGE);
        schema.setActive(true);
        schema.setCreatedAt(LocalDateTime.now());
        schema.setUpdatedAt(LocalDateTime.now());
    }

    public static SchemaBuilder buildFromRequest(SchemaRequest request) {
        SchemaBuilder builder = new SchemaBuilder();

        InvestmentType investmentType = InvestmentType.fromString(request.getInvestmentType());
        SchemaType schemaType = investmentType == STAKE ? SchemaType.FIXED  : SchemaType.fromString(request.getSchemaType());
        ReturnType returnType = investmentType == STAKE ? ReturnType.PERIOD : ReturnType.fromString(request.getReturnType());

        builder.setBasicDetails(request.getName(), investmentType, request.getSchemaBadge());
        builder.setPricing(schemaType, request.getStakePrice(), request.getMinimumInvestmentAmount(), request.getMaximumInvestmentAmount());
        builder.setWithdrawal(request.getMinimumWithdrawalAmount(), request.getHandlingFee());
        builder.setReturns(returnType, request.getReturnRate(), request.getTotalReturnPeriods(), request.isCapitalReturned());
        builder.setCancellation(request.isCancellable(), request.getCancellationGracePeriodMinutes(), request.getEarlyExitPenalty());
        builder.setOptionalDetails(request.isFeatured(), request.isTradeable(), request.getDescription(), request.getTermsAndConditionsUrl());
        builder.schema.setCurrency(request.getCurrency() == null ? CurrencyType.USD : CurrencyType.fromString(request.getCurrency()));

        return builder;
    }

    public static SchemaBuilder stakingSchema(String name, BigDecimal price, BigDecimal returnRate, int periods) {
        SchemaBuilder builder = new SchemaBuilder();

        builder.setBasicDetails(name, STAKE, "STAKE");
        builder.setPricing(SchemaType.FIXED, price, price, BigDecimal.ZERO);
        builder.setWithdrawal(DEFAULT_WITHDRAWAL_AMOUNT, DEFAULT_HANDLING_FEE);
        builder.setReturns(ReturnType.PERIOD, returnRate, periods, true);
        builder.setCancellation(false, DEFAULT_CANCELLATION_GRACE_MINUTES, null);
        builder.setOptionalDetails(false, true, null, null);
        builder.schema.setCurrency(CurrencyType.USDT);

        return builder;
    }

    private void setBasicDetails(String title, InvestmentType investmentType, String badge) {
        schema.setName(title);
        schema.setInvestmentType(investmentType);
        schema.setSchemaBadge(badge == null || badge.isEmpty() ? investmentType.name() : badge);
    }

    private void setPricing(SchemaType schemaType, BigDecimal stakePrice, BigDecimal minInvestment, BigDecimal maxInvestment) {
        schema.setSchemaType(schemaType);
        schema.setStakePrice(stakePrice);
        schema.setMinimumInvestmentAmount(minInvestment);
        schema.setMaximumInvestmentAmount(schemaType == SchemaType.RANGE ? maxInvestment : BigDecimal.ZERO);
    }

    private void setWithdrawal(BigDecimal minWithdrawal, BigDecimal handlingFee) {
        schema.setMinimumWithdrawalAmount(minWithdrawal != null ? minWithdrawal : DEFAULT_WITHDRAWAL_AMOUNT);
        schema.setHandlingFee(handlingFee != null ? handlingFee : DEFAULT_HANDLING_FEE);
    }

    private void setReturns(ReturnType returnType, BigDecimal returnRate, int periods, boolean capitalReturned) {
        schema.setReturnType(returnType);
        schema.setReturnRate(returnRate);
        schema.setTotalReturnPeriods(periods);
        schema.setCapitalReturned(capitalReturned);
    }

    private void setCancellation(boolean cancellable, Integer gracePeriod, BigDecimal penalty) {
        schema.setCancellable(cancellable);
        schema.setCancellationGracePeriodMinutes(gracePeriod != null ? gracePeriod : DEFAULT_CANCELLATION_GRACE_MINUTES);
        schema.setEarlyExitPenalty(penalty);
    }

    private void setOptionalDetails(boolean featured, boolean tradeable, String description, String termsUrl) {
        schema.setFeatured(featured);
        schema.setTradeable(tradeable);
        schema.setDescription(description);
        schema.setTermsAndConditionsUrl(termsUrl);
    }


    public SchemaBuilder withReturnSchedule(Schedule schedule) {
        schema.setReturnSchedule(schedule);
        return this;
    }

    public SchemaBuilder withImageUrl(String url) {
        schema.setImageUrl(url);
        return this;
    }

    public InvestmentSchema build() {
        return schema;
    }
}
