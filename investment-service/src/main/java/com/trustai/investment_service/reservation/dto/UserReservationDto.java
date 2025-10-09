package com.trustai.investment_service.reservation.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class UserReservationDto {
    private Long reservationId;
    private String schemaTitle;
    private String imageUrl;
    private BigDecimal reservedAmount;
    private LocalDateTime reservedAt;
    private LocalDateTime expiryAt;
    private BigDecimal incomeEarned;
    private boolean isSold;

    private BigDecimal returnRate;
    private BigDecimal handlingFee;
    private BigDecimal valuationDelta;
}
