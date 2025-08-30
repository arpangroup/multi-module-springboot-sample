package com.trustai.rank_service.exception;

import lombok.Getter;

@Getter
public class RankNotFoundException extends RuntimeException {
    public RankNotFoundException(String code) {
        super("Rank with code '" + code + "' was not found.");
    }
}
