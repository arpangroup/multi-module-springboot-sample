package com.trustai.investment_service.reservation.mapper;

import com.trustai.investment_service.entity.InvestmentSchema;
import com.trustai.investment_service.reservation.dto.UserReservationDto;
import com.trustai.investment_service.reservation.entity.UserReservation;
import org.springframework.stereotype.Component;

@Component
public class UserReservationMapper {
    public UserReservationDto toDto(UserReservation reservation) {
        InvestmentSchema schema = reservation.getSchema();
        return UserReservationDto.builder()
                .reservationId(reservation.getId())
                .schemaTitle(reservation.getSchema().getTitle())
                .imageUrl(reservation.getSchema().getImageUrl())
                .reservedAmount(reservation.getReservedAmount())
                .reservedAt(reservation.getReservedAt())
                .expiryAt(reservation.getExpiryAt())
                .incomeEarned(reservation.getIncomeEarned())
                //
                .returnRate(schema.getReturnRate())
                .handlingFee(schema.getHandlingFee())
                .valuationDelta(reservation.getValuationDelta())
                .build();
    }
}
