package com.trustai.investment_service.entity.data;

import com.trustai.common.api.RankConfigApi;
import com.trustai.common.constants.CommonConstants;
import com.trustai.common.enums.CurrencyType;
import com.trustai.investment_service.entity.InvestmentSchema;
import com.trustai.investment_service.entity.Schedule;
import com.trustai.investment_service.enums.*;
import com.trustai.investment_service.repository.ScheduleRepository;
import com.trustai.investment_service.repository.SchemaRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@DependsOn("scheduleData")
@RequiredArgsConstructor
public class StakeDataInitializer {
    private final SchemaRepository schemaRepository;
    private final ScheduleRepository scheduleRepository;
    private final RankConfigApi rankConfigApi;

    int count = 0;
    List<String> imageList = new ArrayList<>();


    @PostConstruct
    public void init() {
        //Schedule scheduleHourly = scheduleRepository.findByScheduleNameIgnoreCase("Hourly");
        Schedule scheduleDaily = scheduleRepository.findByScheduleNameIgnoreCase("Daily");
        //Schedule scheduleWeekly = scheduleRepository.findByScheduleNameIgnoreCase("Weekly");
        //Schedule schedule2Weekly = scheduleRepository.findByScheduleNameIgnoreCase("2 Week");
        //Schedule scheduleMonthly = scheduleRepository.findByScheduleNameIgnoreCase("Monthly");
        Schedule noSchedule = scheduleRepository.findByScheduleNameIgnoreCase("No Schedule");

        imageList = loadImages();

        // RANK_0: 100-200
        createStake("RANK_0", 100, 200, 200, 90, 0, noSchedule);
        // RANK_1: 200-300
        createStake("RANK_1", 200, 300, 300, 90, 1.5f, scheduleDaily);
        createStake("RANK_1", 200, 300, 290, 90, 1.8f, scheduleDaily);
        createStake("RANK_1", 200, 300, 280, 90, 2.4f, scheduleDaily);
        createStake("RANK_1", 200, 300, 270, 90, 3.0f, scheduleDaily);
        createStake("RANK_1", 200, 300, 260, 90, 3.4f, scheduleDaily);
        createStake("RANK_1", 200, 300, 250, 90, 4.0f, scheduleDaily);
        // RANK_2: 300-400
        createStake("RANK_2", 300, 400, 390, 90, 5.0f, scheduleDaily);
        // RANK_3: 400-500
        createStake("RANK_3", 400, 500, 490, 90, 6.0f, scheduleDaily);
    }

    private void createStake(String rank, int minInvest, int maxInvest, int stakePrice, int days, float roi, Schedule schedule) {
        if (stakePrice < minInvest || stakePrice > maxInvest) {
            throw new RuntimeException("StakePrice should be between the investment schema range");
        }

        // Investment Schema 1 - FIXED + PERIOD + cancellable
        InvestmentSchema stake1 = new InvestmentSchema();
        stake1.setLinkedRank(rank);
        stake1.setName(rank + " " + days +" days plan of " + stakePrice + " for roi " + roi);
        stake1.setSchemaBadge("STAKE_" + rank + "_" + days);
        stake1.setImageUrl(imageList.get(++count));
        stake1.setInvestmentType(InvestmentType.STAKE);
        stake1.setSchemaType(SchemaType.RANGE);
        stake1.setStakePrice(new BigDecimal(stakePrice));
        stake1.setMinimumInvestmentAmount(new BigDecimal(minInvest));
        stake1.setMaximumInvestmentAmount(new BigDecimal(maxInvest));
        stake1.setHandlingFee(BigDecimal.ZERO);
        stake1.setMinimumWithdrawalAmount(BigDecimal.ZERO);
        stake1.setReturnRate(new BigDecimal(roi));
        stake1.setInterestCalculationMethod(InterestCalculationType.PERCENTAGE);
        stake1.setReturnSchedule(schedule);
        stake1.setReturnType(ReturnType.PERIOD);
        stake1.setTotalReturnPeriods(days);
        stake1.setCapitalReturned(true);
        stake1.setFeatured(true);
        stake1.setCancellable(false);
        stake1.setCancellationGracePeriodMinutes(1440);
        stake1.setTradeable(true);
        stake1.setActive(true);
        stake1.setDescription(null);
        stake1.setCurrency(CurrencyType.USDT);
        stake1.setPayoutMode(PayoutMode.DAILY);
        stake1.setCreatedAt(LocalDateTime.now());
        stake1.setUpdatedAt(LocalDateTime.now());
        stake1.setCreatedBy("admin");
        stake1.setUpdatedBy("admin");
        stake1.setEarlyExitPenalty(new BigDecimal("50.00"));
        stake1.setTermsAndConditionsUrl("https://example.com/tc/fixed1yr");
        schemaRepository.save(stake1);
    }

    private  List<String> loadImages() {
        return IntStream.rangeClosed(1, 20)
                .mapToObj(i -> CommonConstants.BASE_URL + CommonConstants.IMAGE_PATH + "/stake_" + i + ".png")
                .collect(Collectors.toList());
    }
}
