package com.trustai.common.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StakeSoldEvent {
    private Long sellerId;
    private BigDecimal saleAmount;
}
