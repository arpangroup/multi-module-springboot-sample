package com.trustai.rank_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SpecificationResult {
    private boolean satisfied;
    private String specName;
    private String reason;

    @Override
    public String toString() {
        String status = satisfied ? "✅ PASSED" : "❌ FAILED";
        return String.format("%-30s | %-10s | %s", specName, status, reason);
    }
}
