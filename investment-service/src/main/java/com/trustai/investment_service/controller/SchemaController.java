package com.trustai.investment_service.controller;

import com.trustai.investment_service.dto.SchemaRequest;
import com.trustai.investment_service.dto.SchemaUpsertRequest;
import com.trustai.investment_service.entity.InvestmentSchema;
import com.trustai.investment_service.enums.InvestmentType;
import com.trustai.investment_service.service.SchemaService;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/investment-schemas")
@RequiredArgsConstructor
@Slf4j
public class SchemaController {
    private final SchemaService schemaService;

    @GetMapping
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<InvestmentSchema>> getAllSchemas(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false, defaultValue = "") String rankCode,
            @RequestParam(value = "type", required = false, defaultValue = "") String type
    ) {
        log.info("Received request for all investment schemas");
        Pageable pageable = PageRequest.of(page, size);

        InvestmentType investmentType = null;
        if (StringUtils.isNotEmpty(type)) {
            try {
                investmentType = InvestmentType.valueOf(type);
            } catch (Exception e) {
                log.warn("invalid investmentSubType: {}", investmentType);
            }
        }

        Page<InvestmentSchema> schemas;
        if (StringUtils.isNotEmpty(rankCode)) {
            schemas = schemaService.getSchemaByLinkedRank(rankCode, investmentType, pageable);
        } else {
            schemas = schemaService.getAllSchemas(investmentType, pageable);
        }
        return ResponseEntity.ok(schemas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvestmentSchema> getSchemaById(@PathVariable Long id) {
        InvestmentSchema schema = schemaService.getSchemaById(id);
        return ResponseEntity.ok(schema);
    }

    @PostMapping
    public ResponseEntity<InvestmentSchema> createSchema(@RequestBody @Valid SchemaRequest schemaRequest) {
        InvestmentSchema created = schemaService.createSchema(schemaRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }


    @PutMapping("/{id}")
    public ResponseEntity<InvestmentSchema> updateSchema(@PathVariable Long id,  @RequestBody Map<String, Object> updates) {
        log.info("Received request to update InvestmentSchema with ID: {} and updates: {}", id, updates.keySet());
        InvestmentSchema updatedSchema = schemaService.updateSchema(id, updates);
        log.info("Successfully updated InvestmentSchema with ID: {}", id);
        return ResponseEntity.ok(updatedSchema );
    }

    @PostMapping("/bulk-upsert")
    public ResponseEntity<?> createOrUpdateStake(@RequestBody List<SchemaUpsertRequest> requests) {
        log.info("Received bulk request to create or update {} InvestmentSchemas", requests.size());

        for (SchemaUpsertRequest request : requests) {
            if (request.getId() != null) {
                log.info("Updating InvestmentSchema with ID: {}", request.getId());
                schemaService.updateStake(request.getId(), request);
            } else {
                log.info("Creating new InvestmentSchema");
                schemaService.createStake(request);
            }
        }
        log.info("Bulk createOrUpdateSchemas operation completed successfully");
        return ResponseEntity.ok().build();
    }
}
