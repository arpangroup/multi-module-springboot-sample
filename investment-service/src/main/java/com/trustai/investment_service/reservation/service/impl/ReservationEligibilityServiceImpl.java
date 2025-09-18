package com.trustai.investment_service.reservation.service.impl;

import com.trustai.common.api.RankConfigApi;
import com.trustai.common.api.UserApi;
import com.trustai.common.dto.RankConfigDto;
import com.trustai.common.dto.UserInfo;
import com.trustai.investment_service.dto.EligibleInvestmentSummary;
import com.trustai.investment_service.dto.SchemaSummary;
import com.trustai.investment_service.entity.InvestmentSchema;
import com.trustai.investment_service.enums.InvestmentType;
import com.trustai.investment_service.repository.SchemaRepository;
import com.trustai.investment_service.reservation.entity.UserReservation;
import com.trustai.investment_service.reservation.repository.UserReservationRepository;
import com.trustai.investment_service.reservation.service.ReservationEligibilityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationEligibilityServiceImpl implements ReservationEligibilityService {
    private final SchemaRepository schemaRepository;
    private final UserReservationRepository userReservationRepository;
    private final RankConfigApi rankConfigApi;
    private final UserApi userApi;
    private final DecimalFormat df = new DecimalFormat("0.##");

    /*@Override
    public List<EligibleInvestmentSummary> getEligibleInvestmentSummaries(Long userId) {
        log.info("Fetching investment summary for userId={}", userId);

        UserInfo user = userApi.getUserById(userId);
        log.debug("Retrieved user info: id={}, rankCode={}", user.getId(), user.getRankCode());

        List<RankConfigDto> allRanks = rankConfigApi.getAllRanks();
        log.debug("Total ranks fetched: {}", allRanks.size());

        String userRankCode = user.getRankCode();
        RankConfigDto userRank = allRanks.stream()
                .filter(r -> r.getCode().equalsIgnoreCase(userRankCode))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Rank not found for user rankCode={}", userRankCode);
                    return new RuntimeException("User rank not found");
                });
        log.info("Generating investment summary for ranks (userRankOrder={}): {}", userRank.getRankOrder(), userRankCode);

        List<InvestmentSchema> activeSchemas = schemaRepository.findByIsActiveTrueAndInvestmentType(InvestmentType.STAKE);
        log.debug("Fetched total active schemas: {}", activeSchemas.size());

        return allRanks.stream().map(rank -> {
            log.debug("Evaluating rank: code={}, displayName={}", rank.getCode(), rank.getDisplayName());

            // Filter  active stake schemas by linkedRank and range
            List<InvestmentSchema> eligibleSchemas = activeSchemas.stream()
                    .filter(schema -> rank.getCode().equalsIgnoreCase(schema.getLinkedRank()))
                    //.filter(schema -> isWithinRange(rank, schema))
                    .toList();
            log.debug("Eligible schemas for rank '{}': {}", rank.getCode(), eligibleSchemas.size());


            // Calculate income range (from returnRate field in eligible schemas)
            Optional<BigDecimal> minReturnRate = eligibleSchemas.stream()
                    .map(InvestmentSchema::getReturnRate)
                    .min(BigDecimal::compareTo);

            Optional<BigDecimal> maxReturnRate = eligibleSchemas.stream()
                    .map(InvestmentSchema::getReturnRate)
                    .max(BigDecimal::compareTo);

            String incomeRange = minReturnRate.isPresent() && maxReturnRate.isPresent()
                    //? String.format("%.2f - %.2f%%", minReturnRate.get(), maxReturnRate.get())
                    ? String.format("%s - %s%%", df.format(minReturnRate.get()), df.format(maxReturnRate.get()))
                    : "N/A";

            // Convert to SchemaSummary DTO
            List<SchemaSummary> schemaSummaries = eligibleSchemas.stream()
                    .map(schema -> new SchemaSummary(schema.getId(), schema.getName(), schema.getReturnRate()))
                    .toList();

            EligibleInvestmentSummary dto = new EligibleInvestmentSummary();
            dto.setRankCode(rank.getCode());
            dto.setRankDisplayName(rank.getDisplayName());
            dto.setMinInvestmentAmount(rank.getMinInvestmentAmount());
            dto.setMaxInvestmentAmount(rank.getMaxInvestmentAmount());
            dto.setIncomePercentageRange(incomeRange);
            // dto.setEnabled(rank.getRankOrder() <= userRank.getRankOrder());
            dto.setEnabled(rank.getCode().equalsIgnoreCase(userRankCode));
            dto.setSchemas(schemaSummaries);

            log.debug("RankInvestmentSummary created: rank={}, incomeRange={}, schemaCount={}",
                    rank.getCode(), incomeRange, schemaSummaries.size());

            return dto;
        }).toList();
    }*/

    @Override
    public List<EligibleInvestmentSummary> getEligibleInvestmentSummaries(Long userId) {
        log.info("Fetching investment summary for userId={}", userId);

        UserInfo user = userApi.getUserById(userId);
        String userRankCode = user.getRankCode();
        log.debug("Retrieved user info: id={}, rankCode={}", user.getId(), userRankCode);

        List<RankConfigDto> allRanks = rankConfigApi.getAllRanks().stream()
                .sorted(Comparator.comparingInt(RankConfigDto::getRankOrder))
                .toList();
        log.debug("Total ranks fetched: {}", allRanks.size());

        List<UserReservation> todayReservations = userReservationRepository.findAllByUserIdAndToday(userId, LocalDate.now());

        return IntStream.range(0, allRanks.size())
                .mapToObj(i -> {
                    RankConfigDto current = allRanks.get(i);
                    RankConfigDto next = (i + 1 < allRanks.size()) ? allRanks.get(i + 1) : null;

                    int txnPerDay = current.getTxnPerDay();
                    int dailyTxnLimit = txnPerDay == 0 ? 0 :  txnPerDay - todayReservations.size();

                            // ✅ incomePercentageRange logic
                    String incomeRange;
                    if (current.getCommissionPercentage() != null && next != null && next.getCommissionPercentage() != null) {
                        incomeRange = current.getCommissionPercentage().stripTrailingZeros().toPlainString()
                                + " - " + next.getCommissionPercentage().stripTrailingZeros().toPlainString() + "%";
                    } else if (current.getCommissionPercentage() != null) {
                        incomeRange = current.getCommissionPercentage().stripTrailingZeros().toPlainString()
                                + " - " + current.getCommissionPercentage().stripTrailingZeros().toPlainString() + "%";
                    } else {
                        incomeRange = "N/A";
                    }

                    // build summary
                    EligibleInvestmentSummary dto = new EligibleInvestmentSummary();
                    dto.setRankCode(current.getCode());
                    dto.setRankDisplayName(current.getDisplayName());
                    dto.setMinDeposit(current.getMinDepositAmount());
                    dto.setMaxDeposit(next == null ? null : next.getMinDepositAmount());
                    dto.setMinInvestmentAmount(current.getMinInvestmentAmount());
                    dto.setMaxInvestmentAmount(current.getMaxInvestmentAmount());
                    dto.setIncomePercentageRange(incomeRange);
                    dto.setEnabled(current.getCode().equalsIgnoreCase(userRankCode));
                    dto.setTxnLimit(dailyTxnLimit);

                    // load schemas for this rank (assuming you fetch schemaSummaries elsewhere)
                    //List<SchemaSummary> schemaSummaries = fetchSchemasForRank(current);
                    //dto.setSchemas(schemaSummaries);

                    return dto;
                })
                .toList();
    }

    private boolean isWithinRange(RankConfigDto rank, InvestmentSchema schema) {
        BigDecimal min = schema.getMinimumInvestmentAmount();
        BigDecimal max = schema.getMaximumInvestmentAmount();
//        return min.compareTo(rank.getMinInvestmentAmount()) >= 0 &&
//                max.compareTo(rank.getMaxInvestmentAmount()) <= 0;

        boolean match1 = min.compareTo(rank.getMinInvestmentAmount()) >= 0;
        boolean match2 = max.compareTo(rank.getMaxInvestmentAmount()) <= 0;
        return match1 && match2;
    }
}
