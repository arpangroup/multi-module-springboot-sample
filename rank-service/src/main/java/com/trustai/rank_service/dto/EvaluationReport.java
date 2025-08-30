package com.trustai.rank_service.dto;

import com.trustai.rank_service.entity.RankConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationReport {
    private RankConfig matchedRank;
    private List<SpecificationResult> specResults;
    private boolean downgradePrevented;
}
