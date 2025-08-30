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
        return specName + " => " + (satisfied ? "PASSED" : "FAILED") + ": " + reason;
    }
}
