package com.trustai.common.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class UserHierarchyStats {
    private Map<Integer, Long> depthWiseCounts; // e.g., {1=3, 2=5, 3=10}
    private long totalTeamSize;                 // sum of depth > 0
}
