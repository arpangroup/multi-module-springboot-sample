package com.trustai.common.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductPurchasedEvent {
    private Long userId;
    private Long productId;
    private LocalDateTime purchasedAt;

    public ProductPurchasedEvent(Long userId, Long productId) {
        this.userId = userId;
        this.productId = productId;
        this.purchasedAt = LocalDateTime.now();
    }
}
