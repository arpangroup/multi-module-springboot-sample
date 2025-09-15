package com.trustai.investment_service.entity.data;

import com.trustai.common.enums.CurrencyType;
import com.trustai.investment_service.entity.InvestmentSchema;
import com.trustai.investment_service.entity.Schedule;
import com.trustai.investment_service.enums.InterestCalculationType;
import com.trustai.investment_service.enums.PayoutMode;
import com.trustai.investment_service.enums.ReturnType;
import com.trustai.investment_service.enums.SchemaType;
import com.trustai.investment_service.repository.ScheduleRepository;
import com.trustai.investment_service.repository.SchemaRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Set;

@Component
@DependsOn("scheduleData")
@RequiredArgsConstructor
public class InvestmentSchemaDataInitializer {

    private final SchemaRepository schemaRepository;
    private final ScheduleRepository scheduleRepository;

    @PostConstruct
    public void init() {
        // Load schedules
        Schedule scheduleHourly = scheduleRepository.findByScheduleNameIgnoreCase("Hourly");
        Schedule scheduleDaily = scheduleRepository.findByScheduleNameIgnoreCase("Daily");
        Schedule scheduleWeekly = scheduleRepository.findByScheduleNameIgnoreCase("Weekly");
        Schedule schedule2Weekly = scheduleRepository.findByScheduleNameIgnoreCase("2 Week");
        Schedule scheduleMonthly = scheduleRepository.findByScheduleNameIgnoreCase("Monthly");
        Schedule noSchedule = scheduleRepository.findByScheduleNameIgnoreCase("No Schedule");

        // Investment Schema 1 - Fixed 1-Year Plan
        saveIfNotExists("Fixed 1-Year Plan", schema -> {
            schema.setSchemaBadge("FIXED_PLAN");
            schema.setSchemaType(SchemaType.FIXED);
            schema.setMinimumInvestmentAmount(new BigDecimal("1000.00"));
            schema.setReturnRate(new BigDecimal("6.5"));
            schema.setInterestCalculationMethod(InterestCalculationType.PERCENTAGE);
            schema.setReturnSchedule(scheduleWeekly);
            schema.setReturnType(ReturnType.PERIOD);
            schema.setTotalReturnPeriods(52);
            schema.setCapitalReturned(true);
            schema.setFeatured(true);
            schema.setCancellable(true);
            schema.setCancellationGracePeriodMinutes(1440);
            schema.setTradeable(false);
            schema.setActive(true);
            schema.setDescription("Fixed 1-Year investment with weekly returns.");
            schema.setCreatedAt(LocalDateTime.now());
            schema.setUpdatedAt(LocalDateTime.now());
            schema.setCreatedBy("admin");
            schema.setUpdatedBy("admin");
            schema.setCurrency(CurrencyType.USD);
            schema.setEarlyExitPenalty(new BigDecimal("50.00"));
            schema.setTermsAndConditionsUrl("https://example.com/tc/fixed1yr");
        });

        // Investment Schema 2 - Flexible Lifetime Growth Plan
        saveIfNotExists("Flexible Lifetime Growth Plan", schema -> {
            schema.setSchemaBadge("LIFETIME_PLAN");
            schema.setSchemaType(SchemaType.RANGE);
            schema.setMinimumInvestmentAmount(new BigDecimal("500.00"));
            schema.setMaximumInvestmentAmount(new BigDecimal("10000.00"));
            schema.setReturnRate(new BigDecimal("4.0"));
            schema.setInterestCalculationMethod(InterestCalculationType.FLAT);
            schema.setReturnSchedule(scheduleDaily);
            schema.setReturnType(ReturnType.LIFETIME);
            schema.setCapitalReturned(false);
            schema.setFeatured(false);
            schema.setCancellable(false);
            schema.setTradeable(true);
            schema.setActive(true);
            schema.setDescription("Lifetime income with flexible investment range.");
            schema.setCreatedAt(LocalDateTime.now());
            schema.setUpdatedAt(LocalDateTime.now());
            schema.setCreatedBy("system");
            schema.setUpdatedBy("system");
            schema.setCurrency(CurrencyType.INR);
            schema.setEarlyExitPenalty(new BigDecimal("100.00"));
            schema.setTermsAndConditionsUrl("https://example.com/tc/flexiblelife");
        });

        // Investment Schema 3 - Fixed Income for Life
        saveIfNotExists("Fixed Income for Life", schema -> {
            schema.setSchemaBadge("CRYPTO");
            schema.setSchemaType(SchemaType.FIXED);
            schema.setMinimumInvestmentAmount(new BigDecimal("2500.00"));
            schema.setReturnRate(new BigDecimal("5.25"));
            schema.setInterestCalculationMethod(InterestCalculationType.PERCENTAGE);
            schema.setReturnSchedule(scheduleMonthly);
            schema.setReturnType(ReturnType.LIFETIME);
            schema.setCapitalReturned(false);
            schema.setFeatured(true);
            schema.setCancellable(true);
            schema.setCancellationGracePeriodMinutes(4320);
            schema.setTradeable(true);
            schema.setActive(false);
            schema.setDescription("Monthly lifetime returns on a fixed deposit.");
            schema.setCreatedAt(LocalDateTime.now());
            schema.setUpdatedAt(LocalDateTime.now());
            schema.setCreatedBy("manager");
            schema.setUpdatedBy("manager");
            schema.setCurrency(CurrencyType.EUR);
            schema.setEarlyExitPenalty(new BigDecimal("75.00"));
            schema.setTermsAndConditionsUrl("https://example.com/tc/lifetimefixed");
        });

        // Investment Schema 4 - Dynamic Tiered Plan
        saveIfNotExists("Dynamic Tiered Plan", schema -> {
            schema.setSchemaBadge("DYNAMIC");
            schema.setSchemaType(SchemaType.RANGE);
            schema.setMinimumInvestmentAmount(new BigDecimal("1000.00"));
            schema.setMaximumInvestmentAmount(new BigDecimal("20000.00"));
            schema.setReturnRate(new BigDecimal("7.0"));
            schema.setInterestCalculationMethod(InterestCalculationType.PERCENTAGE);
            schema.setReturnSchedule(schedule2Weekly);
            schema.setReturnType(ReturnType.PERIOD);
            schema.setTotalReturnPeriods(26);
            schema.setCapitalReturned(true);
            schema.setFeatured(false);
            schema.setCancellable(false);
            schema.setTradeable(true);
            schema.setActive(true);
            schema.setDescription("Tiered returns for a range of investments.");
            schema.setCreatedAt(LocalDateTime.now());
            schema.setUpdatedAt(LocalDateTime.now());
            schema.setCreatedBy("admin");
            schema.setUpdatedBy("admin");
            schema.setCurrency(CurrencyType.USD);
            schema.setEarlyExitPenalty(new BigDecimal("150.00"));
            schema.setTermsAndConditionsUrl("https://example.com/tc/dynamictier");
        });

        // Weekly Growth Plan
        saveIfNotExists("Weekly Growth Plan", schema -> {
            schema.setSchemaType(SchemaType.FIXED);
            schema.setMinimumInvestmentAmount(new BigDecimal("1000"));
            schema.setMaximumInvestmentAmount(new BigDecimal("1000"));
            schema.setReturnRate(new BigDecimal("5"));
            schema.setInterestCalculationMethod(InterestCalculationType.PERCENTAGE);
            schema.setReturnSchedule(scheduleWeekly);
            schema.setReturnType(ReturnType.PERIOD);
            schema.setTotalReturnPeriods(12);
            schema.setCapitalReturned(true);
            schema.setCancellable(true);
            schema.setCancellationGracePeriodMinutes(1440);
            schema.setCurrency(CurrencyType.INR);
            schema.setPayoutMode(PayoutMode.WEEKLY);
            schema.setPayoutDays(EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.THURSDAY));
        });

        // Monthly Growth Plan
        saveIfNotExists("Monthly Growth Plan", schema -> {
            schema.setSchemaType(SchemaType.FIXED);
            schema.setMinimumInvestmentAmount(new BigDecimal("1000"));
            schema.setMaximumInvestmentAmount(new BigDecimal("1000"));
            schema.setReturnRate(new BigDecimal("5"));
            schema.setInterestCalculationMethod(InterestCalculationType.PERCENTAGE);
            schema.setReturnSchedule(scheduleMonthly);
            schema.setReturnType(ReturnType.PERIOD);
            schema.setTotalReturnPeriods(12);
            schema.setCapitalReturned(true);
            schema.setCancellable(true);
            schema.setCancellationGracePeriodMinutes(1440);
            schema.setCurrency(CurrencyType.INR);
            schema.setPayoutMode(PayoutMode.MONTHLY);
            schema.setPayoutDates(Set.of(1, 15));
        });

        // CRYPTO 15$
        saveIfNotExists("CRYPTO_15$", schema -> {
            schema.setSchemaBadge("LIFETIME_PLAN");
            schema.setSchemaType(SchemaType.FIXED);
            schema.setMinimumInvestmentAmount(new BigDecimal("15.00"));
            schema.setReturnRate(new BigDecimal("0.0"));
            schema.setInterestCalculationMethod(InterestCalculationType.PERCENTAGE);
            schema.setReturnSchedule(scheduleDaily);
            schema.setReturnType(ReturnType.PERIOD);
            schema.setTotalReturnPeriods(90);
            schema.setCapitalReturned(true);
            schema.setFeatured(false);
            schema.setCancellable(false);
            schema.setTradeable(true);
            schema.setActive(true);
            schema.setDescription("CRYPTO_15$ Plan");
            schema.setCreatedAt(LocalDateTime.now());
            schema.setUpdatedAt(LocalDateTime.now());
            schema.setCreatedBy("system");
            schema.setUpdatedBy("system");
            schema.setCurrency(CurrencyType.USDT);
            schema.setEarlyExitPenalty(BigDecimal.ZERO);
            schema.setTermsAndConditionsUrl("https://example.com/tc/flexiblelife");
        });

        // CRYPTO 40$
        saveIfNotExists("CRYPTO_40$", schema -> {
            schema.setSchemaBadge("LIFETIME_PLAN");
            schema.setSchemaType(SchemaType.FIXED);
            schema.setMinimumInvestmentAmount(new BigDecimal("40.00"));
            schema.setReturnRate(new BigDecimal("1.0"));
            schema.setInterestCalculationMethod(InterestCalculationType.PERCENTAGE);
            schema.setReturnSchedule(scheduleDaily);
            schema.setReturnType(ReturnType.PERIOD);
            schema.setTotalReturnPeriods(900);
            schema.setCapitalReturned(true);
            schema.setFeatured(false);
            schema.setCancellable(false);
            schema.setTradeable(true);
            schema.setActive(true);
            schema.setDescription("CRYPTO_40$ Plan");
            schema.setCreatedAt(LocalDateTime.now());
            schema.setUpdatedAt(LocalDateTime.now());
            schema.setCreatedBy("system");
            schema.setUpdatedBy("system");
            schema.setCurrency(CurrencyType.USDT);
            schema.setEarlyExitPenalty(BigDecimal.ZERO);
            schema.setTermsAndConditionsUrl("https://example.com/tc/flexiblelife");
        });
    }

    // Utility method to avoid duplicates
    private void saveIfNotExists(String name, java.util.function.Consumer<InvestmentSchema> consumer) {
        if (!schemaRepository.existsByName(name)) {
            InvestmentSchema schema = new InvestmentSchema();
            schema.setName(name);
            consumer.accept(schema);
            schemaRepository.save(schema);
        }
    }
}
