package com.trustai.investment_service.service.impl;

import com.trustai.common.constants.CommonConstants;
import com.trustai.common.dto.RankConfigDto;
import com.trustai.common.enums.CurrencyType;
import com.trustai.investment_service.dto.SchemaUpsertRequest;
import com.trustai.investment_service.entity.InvestmentSchema;
import com.trustai.investment_service.entity.Schedule;
import com.trustai.investment_service.enums.InterestCalculationType;
import com.trustai.investment_service.enums.ReturnType;
import com.trustai.investment_service.enums.SchemaType;
import com.trustai.investment_service.exception.ResourceNotFoundException;
import com.trustai.common.api.RankConfigApi;
import com.trustai.investment_service.repository.ScheduleRepository;
import com.trustai.investment_service.repository.SchemaRepository;
import com.trustai.investment_service.service.SchemaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchemaServiceImpl implements SchemaService {
    private final SchemaRepository schemaRepository;
    private final ScheduleRepository scheduleRepository;
    private final RankConfigApi rankConfigApi;

    @Override
    public Page<InvestmentSchema> getAllSchemas(
            @Nullable InvestmentSchema.InvestmentSubType investmentSubType,
            @Nullable Pageable pageable
    ) {
        log.info("Fetching all investment schemas for investmentSubType: {}...", investmentSubType);
        if (pageable == null) pageable = PageRequest.of(0, CommonConstants.DEFAULT_PAGE_SIZE);

        //pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "id"));
        pageable = PageRequest.of(
                pageable != null ? pageable.getPageNumber() : 0,
                pageable != null ? pageable.getPageSize(): 10,
                Sort.by(Sort.Order.asc("linkedRank"), Sort.Order.asc("minimumInvestmentAmount"))
        );
        if (investmentSubType != null) {
            return schemaRepository.findByInvestmentSubType(investmentSubType, pageable);
        } else {
            return schemaRepository.findAll(pageable);
        }
    }

    @Override
    public InvestmentSchema getSchemaById(Long id) {
        log.info("Fetching schema with ID: {}", id);
        return schemaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Investment schema not found for ID: " + id));
    }

    @Override
    public Page<InvestmentSchema> getSchemaByLinkedRank(
            @NonNull String rankCode,
            @Nullable InvestmentSchema.InvestmentSubType investmentSubType,
            @Nullable Pageable pageable
    ) {
        log.info("Fetching investment schemas for rankCode: {} and investmentSubType: {}", rankCode, investmentSubType);
        if (pageable == null) pageable = PageRequest.of(0, CommonConstants.DEFAULT_PAGE_SIZE);

        if (investmentSubType != null) {
            return schemaRepository.findByLinkedRankAndInvestmentSubType(rankCode, investmentSubType, pageable);
        } else {
            return schemaRepository.findByLinkedRank(rankCode, pageable);
        }
    }

    @Override
    public InvestmentSchema createSchema(InvestmentSchema investmentSchema) {
        log.info("Attempting to persist InvestmentSchema: {}", investmentSchema);
        investmentSchema.setCreatedAt(java.time.LocalDateTime.now());
        investmentSchema.setUpdatedAt(java.time.LocalDateTime.now());
        log.info("Creating new investment schema: {}", investmentSchema.getTitle());
        InvestmentSchema savedSchema = schemaRepository.save(investmentSchema);
        log.info("Successfully created investment schema with ID: {}", savedSchema.getId());
        return savedSchema;
    }

    @Override
    public InvestmentSchema createSchema(SchemaUpsertRequest request) {
        log.info("Received request to create investment schema: {}", request);
        if (request.getLinkedRankCode() == null) throw new ResourceNotFoundException("Invalid rank: " + request.getLinkedRankCode());

        InvestmentSchema schema = new InvestmentSchema();

        log.debug("Fetching rank config for rank code: {}", request.getLinkedRankCode());
        RankConfigDto linkedRank = rankConfigApi.getRankConfigByRankCode(request.getLinkedRankCode());
        if (linkedRank == null) {
            log.error("Rank not found for rank code: {}", request.getLinkedRankCode());
            throw new ResourceNotFoundException("Invalid rank: " + request.getLinkedRankCode());
        }

        if (request.getMinimumInvestmentAmount().compareTo(linkedRank.getMinInvestmentAmount()) < 0) {
            throw new ResourceNotFoundException("minimumInvestmentAmount should be more than " + linkedRank.getMinInvestmentAmount());
        }

        log.debug("Fetching schedule by ID: {}", request.getReturnScheduleId());
        Schedule schedule = scheduleRepository.findById(request.getReturnScheduleId())
                .orElseThrow(() -> {
                    log.error("Schedule not found for ID: {}", request.getReturnScheduleId());
                    return new IllegalArgumentException("Invalid schedule ID: " + request.getReturnScheduleId());
                });

        BigDecimal minInvestAmount = request.getMinimumInvestmentAmount() != null ? request.getMinimumInvestmentAmount() : BigDecimal.ZERO;
        BigDecimal maxInvestAmount = request.getMaximumInvestmentAmount() != null ? request.getMaximumInvestmentAmount() : BigDecimal.ZERO;
        BigDecimal returnRate      = request.getReturnRate() != null ? request.getReturnRate() : BigDecimal.ZERO;
        int totalReturnPeriods     = request.getTotalReturnPeriods() != null ? request.getTotalReturnPeriods() : 0;
        boolean capitalReturned    = request.getCapitalReturned() != null ? request.getCapitalReturned() : false;
        boolean active             = request.getActive() != null ? request.getActive() : false;
        String title               = generateTitle(request.getLinkedRankCode(), returnRate);

        log.info("Creating schema with title: {}", title);
        schema.setLinkedRank(request.getLinkedRankCode());
        schema.setTitle(title);                 // default ==> title is mandatory
        schema.setSchemaBadge(title);           // default
        schema.setSchemaType(SchemaType.RANGE); // default
        schema.setMinimumInvestmentAmount(minInvestAmount);
        schema.setMaximumInvestmentAmount(maxInvestAmount);
        schema.setReturnRate(returnRate);
        schema.setInterestCalculationMethod(InterestCalculationType.PERCENTAGE); // default
        schema.setReturnSchedule(schedule);
        schema.setReturnType(ReturnType.PERIOD); // default
        schema.setTotalReturnPeriods(totalReturnPeriods);
        schema.setCapitalReturned(capitalReturned);
        schema.setActive(active);

        log.debug("Final schema object before persistence: {}", schema);
        return createSchema(schema);
    }

    @Override
    public InvestmentSchema updateSchema(Long id, Map<String, Object> updates) {
        InvestmentSchema schema = getSchemaById(id);
        log.info("Starting partial update for InvestmentSchema ID: {}", id);

        updates.forEach((key, value) -> {
            try {
                switch (key) {
                    case "title" -> {
                        schema.setTitle((String) value);
                        log.debug("Updated field 'title' to '{}'", value);
                    }
                    case "schemaBadge" -> {
                        schema.setSchemaBadge((String) value);
                        log.debug("Updated field 'schemaBadge' to '{}'", value);
                    }
                    case "schemaType" -> {
                        schema.setSchemaType(SchemaType.valueOf((String) value));
                        log.debug("Updated field 'schemaType' to '{}'", value);
                    }
                    case "minimumInvestmentAmount" -> {

                        try{
                            RankConfigDto linkedRank = rankConfigApi.getRankConfigByRankCode((String) value);
                            if (linkedRank == null) {
                                log.error("Rank not found for rank code: {}", value);
                            } else {
                                BigDecimal amount = new BigDecimal((String) value);
                                if (amount.compareTo(linkedRank.getMinInvestmentAmount()) < 0) {
                                    throw new ResourceNotFoundException("minimumInvestmentAmount should be more than " + linkedRank.getMinInvestmentAmount());
                                }
                            }
                        } catch (Exception e) {
                            log.error("Invalid linkedRank value: {}", value);
                            e.printStackTrace();
                        }

                        schema.setMinimumInvestmentAmount(new BigDecimal(value.toString()));
                        log.debug("Updated field 'minimumInvestmentAmount' to '{}'", value);
                    }
                    case "maximumInvestmentAmount" -> {
                        schema.setMaximumInvestmentAmount(new BigDecimal(value.toString()));
                        log.debug("Updated field 'maximumInvestmentAmount' to '{}'", value);
                    }
                    case "returnRate" -> {
                        schema.setReturnRate(new BigDecimal(value.toString()));
                        log.debug("Updated field 'returnRate' to '{}'", value);
                    }
                    case "interestCalculationMethod" -> {
                        schema.setInterestCalculationMethod(InterestCalculationType.valueOf((String) value));
                        log.debug("Updated field 'interestCalculationMethod' to '{}'", value);
                    }
                    case "returnType" -> {
                        schema.setReturnType(ReturnType.valueOf((String) value));
                        log.debug("Updated field 'returnType' to '{}'", value);
                    }
                    case "totalReturnPeriods" -> {
                        schema.setTotalReturnPeriods(Integer.parseInt((String) value));
                        log.debug("Updated field 'totalReturnPeriods' to '{}'", value);
                    }
                    case "isCapitalReturned", "capitalReturned" -> {
                        schema.setCapitalReturned((Boolean) value);
                        log.debug("Updated field 'isCapitalReturned' to '{}'", value);
                    }
                    case "isFeatured", "featured" -> {
                        schema.setFeatured((Boolean) value);
                        log.debug("Updated field 'isFeatured' to '{}'", value);
                    }
                    case "isCancellable", "cancellable" -> {
                        schema.setCancellable((Boolean) value);
                        log.debug("Updated field 'isCancellable' to '{}'", value);
                    }
                    case "cancellationGracePeriodMinutes" -> {
                        schema.setCancellationGracePeriodMinutes(Integer.parseInt((String) value));
                        log.debug("Updated field 'cancellationGracePeriodMinutes' to '{}'", value);
                    }
                    case "isTradeable", "tradeable" -> {
                        schema.setTradeable((Boolean) value);
                        log.debug("Updated field 'isTradeable' to '{}'", value);
                    }
                    case "isActive", "active" -> {
                        schema.setActive((Boolean) value);
                        log.debug("Updated field 'isActive' to '{}'", value);
                    }
                    case "description" -> {
                        schema.setDescription((String) value);
                        log.debug("Updated field 'description' to '{}'", value);
                    }
                    case "currency" -> {
                        log.debug("Updating field 'currency' to '{}'", value);
                        try {
                            CurrencyType currencyEnum = CurrencyType.valueOf(((String) value).toUpperCase());
                            schema.setCurrency(currencyEnum);
                            log.debug("Updated field 'currency' to '{}'", currencyEnum);
                        } catch (Exception  e) {
                            log.error("Invalid currency value: {}", value);
                            e.printStackTrace();
                        }
                    }
                    case "earlyExitPenalty" -> {
                        schema.setEarlyExitPenalty(new BigDecimal(value.toString()));
                        log.debug("Updated field 'earlyExitPenalty' to '{}'", value);
                    }
                    case "termsAndConditionsUrl" -> {
                        schema.setTermsAndConditionsUrl((String) value);
                        log.debug("Updated field 'termsAndConditionsUrl' to '{}'", value);
                    }
                    case "returnSchedule" -> {
                        log.debug("Updating field 'returnSchedule' to '{}'", value);
                        try {
                            Long scheduleId = Long.parseLong((String) value);
                            Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(() -> new ResourceNotFoundException("scheduleId = " + scheduleId + " not found"));
                            schema.setReturnSchedule(schedule);
                            log.debug("Updated field 'returnSchedule' to scheduleId: '{}', scheduleName: '{}' ", scheduleId, schedule.getScheduleName());
                        } catch (Exception e) {
                            log.error("Invalid returnSchedule value: {}", value);
                            e.printStackTrace();
                        }
                    }

                    case "linkedRank" -> {
                        log.debug("Updating field 'linkedRank' to '{}'", value);
                        try{
                            RankConfigDto linkedRank = rankConfigApi.getRankConfigByRankCode((String) value);
                            if (linkedRank == null) {
                                log.error("Rank not found for rank code: {}", value);
                            } else {
                                schema.setLinkedRank(linkedRank.getCode());
                            }
                        } catch (Exception e) {
                            log.error("Invalid linkedRank value: {}", value);
                            e.printStackTrace();
                        }
                    }

                    //case "updatedBy" -> schema.setUpdatedBy((String) value);

                    // Special handling for nested Schedule
                /*case "returnScheduleId" -> {
                    Long scheduleId = Long.valueOf(value.toString());
                    Schedule schedule = new Schedule();
                    schedule.setId(scheduleId); // or fetch full entity if needed
                    schema.setReturnSchedule(schedule);
                }*/

                    default -> log.warn("Unknown field received in update: {}", key);
                }
            } catch (Exception e) {
                log.error("Error updating field '{}': {}", key, e.getMessage(), e);
                throw new IllegalArgumentException("Invalid value for field: " + key);
            }
        });

        schema.setUpdatedAt(LocalDateTime.now());
        log.info("Partially updating investment schema ID: {}", id);
        return schemaRepository.save(schema);
    }

    @Override
    public InvestmentSchema updateSchema(Long schemaId, SchemaUpsertRequest request) {
        log.info("Initiating update for InvestmentSchema with ID: {}", schemaId);

        InvestmentSchema schema = schemaRepository.findById(schemaId).orElseThrow(() -> {
            log.error("Schema not found for schemaId: {}", schemaId);
           return new ResourceNotFoundException("Invalid schemaId: " + schemaId);
        });

        if (request.getLinkedRankCode() != null) {
            log.debug("Fetching rank config for rank code: {}", request.getLinkedRankCode());
            RankConfigDto linkedRank = rankConfigApi.getRankConfigByRankCode(request.getLinkedRankCode());
            if (linkedRank == null) {
                log.error("Rank not found for rank code: {}", request.getLinkedRankCode());
                throw new ResourceNotFoundException("Invalid rank: " + request.getLinkedRankCode());
            }
            log.debug("Rank config found: {}", linkedRank);
            schema.setLinkedRank(request.getLinkedRankCode());
        }

        log.debug("Applying updates to schema fields...");
        if (request.getMinimumInvestmentAmount() != null) schema.setMinimumInvestmentAmount(request.getMinimumInvestmentAmount());
        if (request.getMaximumInvestmentAmount() != null) schema.setMaximumInvestmentAmount(request.getMaximumInvestmentAmount());
        if (request.getReturnRate() != null) schema.setReturnRate(request.getReturnRate());
        if (request.getTotalReturnPeriods() != null) schema.setTotalReturnPeriods(request.getTotalReturnPeriods());
        if (request.getCapitalReturned() != null) schema.setCapitalReturned(request.getCapitalReturned());
        if (request.getActive() != null) schema.setActive(request.getActive());

        if (request.getReturnScheduleId() != null) {
            log.debug("Fetching schedule by ID: {}", request.getReturnScheduleId());
            Schedule schedule = scheduleRepository.findById(request.getReturnScheduleId())
                    .orElseThrow(() -> {
                        log.error("Schedule not found for ID: {}", request.getReturnScheduleId());
                        return new IllegalArgumentException("Invalid schedule ID: " + request.getReturnScheduleId());
                    });
            schema.setReturnSchedule(schedule);
            log.debug("Return schedule set: {}", schedule.getId());
        }

        InvestmentSchema savedSchema = schemaRepository.save(schema);
        log.info("Successfully updated InvestmentSchema with ID: {}", savedSchema.getId());
        return savedSchema;
    }

    private String generateTitle(String rankCode, BigDecimal returnRate) {
        // Format to 1 decimal place, omit trailing .0
        DecimalFormat df = new DecimalFormat("0.#");
        String formattedRate = df.format(returnRate);
        return rankCode + "@" + formattedRate + "%";
    }
}
