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
        Schedule scheduleHourly = scheduleRepository.findByScheduleNameIgnoreCase("Hourly");
        Schedule scheduleDaily = scheduleRepository.findByScheduleNameIgnoreCase("Daily");
        Schedule scheduleWeekly = scheduleRepository.findByScheduleNameIgnoreCase("Weekly");
        Schedule schedule2Weekly = scheduleRepository.findByScheduleNameIgnoreCase("2 Week");
        Schedule scheduleMonthly = scheduleRepository.findByScheduleNameIgnoreCase("Monthly");
        Schedule noSchedule = scheduleRepository.findByScheduleNameIgnoreCase("No Schedule");


        // Investment Schema 1 - FIXED + PERIOD + cancellable
        InvestmentSchema schema1 = new InvestmentSchema();
        schema1.setTitle("Fixed 1-Year Plan");
        schema1.setSchemaBadge("FIXED_PLAN");
        schema1.setSchemaType(SchemaType.FIXED);
        schema1.setMinimumInvestmentAmount(new BigDecimal("1000.00"));
        schema1.setReturnRate(new BigDecimal("6.5"));
        schema1.setInterestCalculationMethod(InterestCalculationType.PERCENTAGE);
        schema1.setReturnSchedule(scheduleWeekly);
        schema1.setReturnType(ReturnType.PERIOD);
        schema1.setTotalReturnPeriods(52);
        schema1.setCapitalReturned(true);
        schema1.setFeatured(true);
        schema1.setCancellable(true);
        schema1.setCancellationGracePeriodMinutes(1440);
        schema1.setTradeable(false);
        schema1.setActive(true);
        schema1.setDescription("Fixed 1-Year investment with weekly returns.");
        schema1.setCreatedAt(LocalDateTime.now());
        schema1.setUpdatedAt(LocalDateTime.now());
        schema1.setCreatedBy("admin");
        schema1.setUpdatedBy("admin");
        schema1.setCurrency(CurrencyType.USD);
        schema1.setEarlyExitPenalty(new BigDecimal("50.00"));
        schema1.setTermsAndConditionsUrl("https://example.com/tc/fixed1yr");
        schemaRepository.save(schema1);

        // Investment Schema 2 - RANGE + LIFETIME + not cancellable
        InvestmentSchema schema2 = new InvestmentSchema();
        schema2.setTitle("Flexible Lifetime Growth Plan");
        schema2.setSchemaBadge("LIFETIME_PLAN");
        schema2.setSchemaType(SchemaType.RANGE);
        schema2.setMinimumInvestmentAmount(new BigDecimal("500.00"));
        schema2.setMaximumInvestmentAmount(new BigDecimal("10000.00"));
        schema2.setReturnRate(new BigDecimal("4.0"));
        schema2.setInterestCalculationMethod(InterestCalculationType.FLAT);
        schema2.setReturnSchedule(scheduleDaily);
        schema2.setReturnType(ReturnType.LIFETIME);
        schema2.setCapitalReturned(false);
        schema2.setFeatured(false);
        schema2.setCancellable(false);
        schema2.setTradeable(true);
        schema2.setActive(true);
        schema2.setDescription("Lifetime income with flexible investment range.");
        schema2.setCreatedAt(LocalDateTime.now());
        schema2.setUpdatedAt(LocalDateTime.now());
        schema2.setCreatedBy("system");
        schema2.setUpdatedBy("system");
        schema2.setCurrency(CurrencyType.INR);
        schema2.setEarlyExitPenalty(new BigDecimal("100.00"));
        schema2.setTermsAndConditionsUrl("https://example.com/tc/flexiblelife");
        schemaRepository.save(schema2);

        // Investment Schema 3 - FIXED + LIFETIME + cancellable
        InvestmentSchema schema3 = new InvestmentSchema();
        schema3.setTitle("Fixed Income for Life");
        schema3.setSchemaBadge("CRYPTO");
        schema3.setSchemaType(SchemaType.FIXED);
        schema3.setMinimumInvestmentAmount(new BigDecimal("2500.00"));
        schema3.setReturnRate(new BigDecimal("5.25"));
        schema3.setInterestCalculationMethod(InterestCalculationType.PERCENTAGE);
        schema3.setReturnSchedule(scheduleMonthly);
        schema3.setReturnType(ReturnType.LIFETIME);
        schema3.setCapitalReturned(false);
        schema3.setFeatured(true);
        schema3.setCancellable(true);
        schema3.setCancellationGracePeriodMinutes(4320);
        schema3.setTradeable(true);
        schema3.setActive(false);
        schema3.setDescription("Monthly lifetime returns on a fixed deposit.");
        schema3.setCreatedAt(LocalDateTime.now());
        schema3.setUpdatedAt(LocalDateTime.now());
        schema3.setCreatedBy("manager");
        schema3.setUpdatedBy("manager");
        schema3.setCurrency(CurrencyType.EUR);
        schema3.setEarlyExitPenalty(new BigDecimal("75.00"));
        schema3.setTermsAndConditionsUrl("https://example.com/tc/lifetimefixed");
        schemaRepository.save(schema3);

        // Investment Schema 4 - RANGE + PERIOD + not cancellable
        InvestmentSchema schema4 = new InvestmentSchema();
        schema4.setTitle("Dynamic Tiered Plan");
        schema4.setSchemaBadge("DYNAMIC");
        schema4.setSchemaType(SchemaType.RANGE);
        schema4.setMinimumInvestmentAmount(new BigDecimal("1000.00"));
        schema4.setMaximumInvestmentAmount(new BigDecimal("20000.00"));
        schema4.setReturnRate(new BigDecimal("7.0"));
        schema4.setInterestCalculationMethod(InterestCalculationType.PERCENTAGE);
        schema4.setReturnSchedule(schedule2Weekly); // IMPORTANT
        schema4.setReturnType(ReturnType.PERIOD);
        schema4.setTotalReturnPeriods(26);
        schema4.setCapitalReturned(true);
        schema4.setFeatured(false);
        schema4.setCancellable(false);
        schema4.setTradeable(true);
        schema4.setActive(true);
        schema4.setDescription("Tiered returns for a range of investments.");
        schema4.setCreatedAt(LocalDateTime.now());
        schema4.setUpdatedAt(LocalDateTime.now());
        schema4.setCreatedBy("admin");
        schema4.setUpdatedBy("admin");
        schema4.setCurrency(CurrencyType.USD);
        schema4.setEarlyExitPenalty(new BigDecimal("150.00"));
        schema4.setTermsAndConditionsUrl("https://example.com/tc/dynamictier");
        schemaRepository.save(schema4);

        InvestmentSchema weeklyRoiSchema = new InvestmentSchema();
        weeklyRoiSchema.setTitle("Weekly Growth Plan");
        weeklyRoiSchema.setSchemaType(SchemaType.FIXED);
        weeklyRoiSchema.setMinimumInvestmentAmount(new BigDecimal("1000"));
        weeklyRoiSchema.setMaximumInvestmentAmount(new BigDecimal("1000"));
        weeklyRoiSchema.setReturnRate(new BigDecimal("5"));
        weeklyRoiSchema.setInterestCalculationMethod(InterestCalculationType.PERCENTAGE);
        weeklyRoiSchema.setReturnSchedule(scheduleWeekly);
        weeklyRoiSchema.setReturnType(ReturnType.PERIOD);
        weeklyRoiSchema.setTotalReturnPeriods(12);
        weeklyRoiSchema.setCapitalReturned(true);
        weeklyRoiSchema.setCancellable(true);
        weeklyRoiSchema.setCancellationGracePeriodMinutes(1440); // 1 day
        weeklyRoiSchema.setCurrency(CurrencyType.INR);
        weeklyRoiSchema.setPayoutMode(PayoutMode.WEEKLY);
        weeklyRoiSchema.setPayoutDays(EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.THURSDAY));
        schemaRepository.save(weeklyRoiSchema);

        InvestmentSchema monthlySchema = new InvestmentSchema();
        monthlySchema.setTitle("Monthly Growth Plan");
        monthlySchema.setSchemaType(SchemaType.FIXED);
        monthlySchema.setMinimumInvestmentAmount(new BigDecimal("1000"));
        monthlySchema.setMaximumInvestmentAmount(new BigDecimal("1000"));
        monthlySchema.setReturnRate(new BigDecimal("5"));
        monthlySchema.setInterestCalculationMethod(InterestCalculationType.PERCENTAGE);
        monthlySchema.setReturnSchedule(scheduleMonthly);
        monthlySchema.setReturnType(ReturnType.PERIOD);
        monthlySchema.setTotalReturnPeriods(12);
        monthlySchema.setCapitalReturned(true);
        monthlySchema.setCancellable(true);
        monthlySchema.setCancellationGracePeriodMinutes(1440); // 1 day
        monthlySchema.setCurrency(CurrencyType.INR);
        monthlySchema.setPayoutMode(PayoutMode.MONTHLY);
        monthlySchema.setPayoutDates(Set.of(1, 15));
        schemaRepository.save(monthlySchema);


        InvestmentSchema schema15 = new InvestmentSchema();
        schema15.setTitle("CRYPTO_15$");
        schema15.setSchemaBadge("LIFETIME_PLAN");
        schema15.setSchemaType(SchemaType.FIXED);
        schema15.setMinimumInvestmentAmount(new BigDecimal("15.00"));
        //schema15.setMaximumInvestmentAmount(new BigDecimal("30.00"));
        schema15.setReturnRate(new BigDecimal("0.0"));
        schema15.setInterestCalculationMethod(InterestCalculationType.PERCENTAGE);
        schema15.setReturnSchedule(scheduleDaily);
        schema15.setReturnType(ReturnType.PERIOD);
        schema15.setTotalReturnPeriods(90);
        schema15.setCapitalReturned(true);
        schema15.setFeatured(false);
        schema15.setCancellable(false);
        schema15.setTradeable(true);
        schema15.setActive(true);
        schema15.setDescription("CRYPTO_40$");
        schema15.setCreatedAt(LocalDateTime.now());
        schema15.setUpdatedAt(LocalDateTime.now());
        schema15.setCreatedBy("system");
        schema15.setUpdatedBy("system");
        schema15.setCurrency(CurrencyType.USDT);
        schema15.setEarlyExitPenalty(new BigDecimal("0.00"));
        schema15.setTermsAndConditionsUrl("https://example.com/tc/flexiblelife");
        schemaRepository.save(schema15);

        InvestmentSchema schema40 = new InvestmentSchema();
        schema40.setTitle("CRYPTO_40$");
        schema40.setSchemaBadge("LIFETIME_PLAN");
        schema40.setSchemaType(SchemaType.FIXED);
        schema40.setMinimumInvestmentAmount(new BigDecimal("40.00"));
        //schema2.setMaximumInvestmentAmount(new BigDecimal("30.00"));
        schema40.setReturnRate(new BigDecimal("1.0"));
        schema40.setInterestCalculationMethod(InterestCalculationType.PERCENTAGE);
        schema40.setReturnSchedule(scheduleDaily);
        schema40.setReturnType(ReturnType.PERIOD);
        schema40.setTotalReturnPeriods(900);
        schema40.setCapitalReturned(true);
        schema40.setFeatured(false);
        schema40.setCancellable(false);
        schema40.setTradeable(true);
        schema40.setActive(true);
        schema40.setDescription("CRYPTO_40$");
        schema40.setCreatedAt(LocalDateTime.now());
        schema40.setUpdatedAt(LocalDateTime.now());
        schema40.setCreatedBy("system");
        schema40.setUpdatedBy("system");
        schema40.setCurrency(CurrencyType.USDT);
        schema40.setEarlyExitPenalty(new BigDecimal("0.00"));
        schema40.setTermsAndConditionsUrl("https://example.com/tc/flexiblelife");
        schemaRepository.save(schema40);

    }
}
