package com.trustai.common.event;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class ProductSoldEvent {
    private Long sellerId;
    private Long productId;
    private BigDecimal productValue;

    public ProductSoldEvent(Long sellerId, Long productId, BigDecimal productValue) {
        this.sellerId = sellerId;
        this.productId = productId;
        this.productValue = productValue;
    }
}
