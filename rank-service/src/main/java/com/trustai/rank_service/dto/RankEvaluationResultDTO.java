package com.trustai.rank_service.dto;

public record RankEvaluationResultDTO(
        Long userId,
        String oldRankCode,
        String newRankCode,
        boolean upgraded,
        String reason
) {}