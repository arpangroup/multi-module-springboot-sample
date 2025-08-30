package com.trustai.investment_service.service;

import com.trustai.investment_service.dto.SchemaUpsertRequest;
import com.trustai.investment_service.entity.InvestmentSchema;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Map;

public interface SchemaService {
    Page<InvestmentSchema> getAllSchemas(@Nullable InvestmentSchema.InvestmentSubType investmentSubType, @Nullable Pageable pageable);
    InvestmentSchema getSchemaById(Long id);
    Page<InvestmentSchema> getSchemaByLinkedRank(@NonNull String rankCode, @Nullable InvestmentSchema.InvestmentSubType investmentSubType, @Nullable Pageable pageable);

    InvestmentSchema createSchema(InvestmentSchema investmentSchema);
    InvestmentSchema createSchema(SchemaUpsertRequest request);

    InvestmentSchema updateSchema(Long id, Map<String, Object> updates);
    InvestmentSchema updateSchema(Long schemaId, SchemaUpsertRequest request);

}
