/*
package com.trustai.investment_service.reservation.service.impl;

import com.trustai.common.api.RankConfigApi;
import com.trustai.common.api.UserApi;
import com.trustai.common.dto.RankConfigDto;
import com.trustai.common.dto.UserInfo;
import com.trustai.investment_service.repository.SchemaRepository;
import com.trustai.investment_service.reservation.dto.UserReservationDto;
import com.trustai.investment_service.reservation.entity.UserReservation;
import com.trustai.investment_service.reservation.mapper.UserReservationMapper;
import com.trustai.investment_service.reservation.repository.StakeReservationRepository;
import com.trustai.investment_service.reservation.service.StakeReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Deprecated
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductReservationServiceImpl implements StakeReservationService {
    private final StakeReservationRepository reservationRepository;
    private final SchemaRepository schemaRepository;
    private final UserReservationMapper mapper;
    private final UserApi userApi;
    private final RankConfigApi rankConfigApi;

    @Override
    public UserReservation autoReserve(Long userId) {
        log.info("Attempting to reserve - userId: {}", userId);
        UserInfo user = userApi.getUserById(userId);
        RankConfigDto rankConfig = rankConfigApi.getRankConfigByRankCode(user.getRankCode());

        // 1. Check existing reservation for today
        boolean alreadyReserved = reservationRepository.existsByUserIdAndReservationDate(userId, LocalDate.now());
        if (alreadyReserved) {
            log.warn("User has already reserved today - userId: {}", userId);
            throw new IllegalStateException("User has already reserved a stake today");
        }

        // 2. Get max-priced eligible stake schema (Select best fit product within range)
        Product product = productRepository
                .findTopByCategoryNameAndPriceBetweenAndIsActiveOrderByPriceDesc(
                        "STAKE",
                        rankConfig.getMinDepositAmount(),
                        rankConfig.getMaxInvestmentAmount()
                )
                .orElseThrow(() -> {
                    log.error("No suitable stake product found for reservation");
                    return new IllegalArgumentException("No suitable stake product found for reservation");
                });

        // 3. Validate user wallet balance
        BigDecimal walletBalance = user.getWalletBalance();
        if (walletBalance.compareTo(rankConfig.getMinInvestmentAmount()) > 0 ||
                walletBalance.compareTo(rankConfig.getMaxInvestmentAmount()) < 0) {
            throw new IllegalStateException("Wallet balance not in range for reservation");
        }

        // 4. Create and save reservation
        UserReservation reservation = UserReservation.builder()
                .userId(userId)
                .schema(null)// <--------for product we need other infos to display in UI
                .price(product.getPrice())
                .reservedAt(LocalDateTime.now())
                .expiryAt(LocalDateTime.now().plusDays(1))
                .incomeEarned(BigDecimal.ZERO)
                .isSold(false)
                .build();

        UserReservation savedReservation = reservationRepository.save(reservation);
        log.info("Reservation successful - reservationId: {}, userId: {}", savedReservation.getId(), userId);

        return savedReservation;
    }

    @Deprecated
    @Override
    public UserReservation reserve(Long userId, Long schemaId, BigDecimal amount) {
        // TODO: This method is deprecated and should no longer be used.
        // If still needed, implement reservation logic considering user, schema, and amount parameters.
        // Otherwise, remove or replace with the updated reservation method.
        return null;
    }


    @Override
    public void sellReservation(Long reservationId, Long userId) {
        // TODO: Implement logic to mark the reservation as sold for the specified user.
        // Validate reservation ownership and availability before selling.
    }


    @Override
    public List<UserReservationDto> getActiveReservations(Long userId) {
        // TODO: Implement method to fetch and return all active reservations for the given userId.
        // Active reservations are those that have not yet expired or been canceled.
        return new ArrayList<>();
    }


    @Override
    public void expireUnclaimedReservations() {
        // TODO: Implement cleanup process to expire reservations that have not been claimed within the allowed timeframe.
        // Update reservation status and notify users if necessary.
    }
}
*/
