package com.trustai.aggregator.dto;

import java.math.BigDecimal;

public record NftItem(
        String imgSrc,
        String title,
        String alt,
        String badge,
        String ownerImg,
        String ownerName,
        String price,
        String timeLeft,
        String change,
        String currencySymbol,
        String currencyUnit
) {
}
