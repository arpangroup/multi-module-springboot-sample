package com.trustai.investment_service.reservation.service.impl;

import com.trustai.common.api.IncomeApi;
import com.trustai.common.api.UserApi;
import com.trustai.common.api.WalletApi;
import com.trustai.common.dto.*;
import com.trustai.common.enums.IncomeType;
import com.trustai.common.enums.TransactionType;
import com.trustai.common.event.NotificationEvent;
import com.trustai.common.event.StakeSoldEvent;
import com.trustai.common.exceptions.ErrorCode;
import com.trustai.common.exceptions.ValidationException;
import com.trustai.common.utils.DateUtils;
import com.trustai.investment_service.entity.InvestmentSchema;
import com.trustai.investment_service.enums.InvestmentType;
import com.trustai.investment_service.repository.SchemaRepository;
import com.trustai.investment_service.reservation.dto.ReservationSummary;
import com.trustai.investment_service.reservation.dto.UserReservationDto;
import com.trustai.investment_service.reservation.entity.UserReservation;
import com.trustai.investment_service.reservation.mapper.UserReservationMapper;
import com.trustai.investment_service.reservation.repository.UserReservationRepository;
import com.trustai.investment_service.reservation.service.StakeReservationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Primary
@RequiredArgsConstructor
@Slf4j
public class StakeReservationServiceImpl implements StakeReservationService {
    private final UserReservationRepository reservationRepository;
    private final SchemaRepository schemaRepository;
    private final UserReservationMapper mapper;
    private final UserApi userApi;
    private final WalletApi walletApi;
    private final IncomeApi incomeApi;
    private final ApplicationEventPublisher eventPublisher;

    @Value("${investment.stake.valuationDelta}")
    private BigDecimal stakeValuationDelta;


    @Override
    public ReservationSummary getReservationSummary(Long userId) {
        log.info("Retrieving reservation info for userId: {}", userId);
        var userInfo = userApi.getUserById(userId);
        List<IncomeSummaryDto> incomeSummary = incomeApi.getIncomeSummary(userId);

        var dailyIncome = incomeSummary.stream().filter(i -> i.getIncomeType() == IncomeType.DAILY).findFirst().get();
        var teamIncome = incomeSummary.stream().filter(i -> i.getIncomeType() == IncomeType.TEAM).findFirst().get();
        int totalOrders = dailyIncome.getTotalOrders();
        int processingOrders = dailyIncome.getProcessingOrders();

        return ReservationSummary.builder()
                .todayEarning(dailyIncome.getTodayAmount())
                .cumulativeIncome(dailyIncome.getTotalAmount())
                .todayTeamIncome(teamIncome.getTodayAmount())
                .totalTeamIncome(teamIncome.getTotalAmount())
                .reservationRange(new ReservationSummary.ReservationRange(BigDecimal.ONE, new BigDecimal("5000")))
                .reservedCount(1)
                .walletBalance(userInfo.getWalletBalance())
                .totalOrders(totalOrders)
                .processingOrders(processingOrders)
                .boughtOrders(processingOrders)
                .soldOrders(totalOrders - processingOrders)
                .build();
    }

    /**
     * Automatically reserves a stake for a given user based on available schemas.
     * Checks user's eligibility, wallet balance, and reserves the highest-priced schema.
     *
     * @param userId ID of the user making the reservation
     * @return Saved reservation object
     */
    @Override
    @Transactional
    public UserReservation autoReserve(Long userId) {
        log.info("Attempting to auto-reserve stake - userId: {}", userId);
        UserInfo user = userApi.getUserById(userId);

        // Step 1. CCheck if the user already has a reservation for today
        /*boolean alreadyReserved = reservationRepository.existsByUserIdAndReservationDate(userId, LocalDate.now());
        if (alreadyReserved) {
            log.warn("Reservation failed: User has already reserved today - userId: {}", userId);
            throw new ValidationException("User has already reserved a stake today", ErrorCode.STAKE_ALREADY_RESERVED );
        }*/

        // Step 2. Get highest-priced eligible active stake schema
        InvestmentSchema schema = schemaRepository
                .findTopByInvestmentTypeAndIsActiveTrueOrderByMinimumInvestmentAmountDesc(InvestmentType.STAKE)
                .orElseThrow(() -> {
                    log.error("Reservation failed: No suitable stake schema found");
                    throw new ValidationException("No suitable stake schema found for reservation", ErrorCode.STAKE_SCHEMA_NOT_FOUND );
                });

        // Step 3. Verify if the user has sufficient wallet balance for the reservation
        BigDecimal walletBalance = user.getWalletBalance();
        BigDecimal reservedAmount = schema.getStakePrice();
        BigDecimal minimumRequired = schema.getMinimumInvestmentAmount();

        if (walletBalance.compareTo(minimumRequired) < 0) {
            log.warn("Reservation failed: Insufficient wallet balance. userId={}, balance={}, required={}", userId, walletBalance, minimumRequired);
            throw new ValidationException("Insufficient Wallet Balance", ErrorCode.INSUFFICIENT_WALLET_BALANCE );
        }

        // Step 4. Construct a new reservation entity
        BigDecimal valuationDeltaSafe = stakeValuationDelta != null ? stakeValuationDelta : BigDecimal.ZERO;
        LocalDateTime now = LocalDateTime.now();

        UserReservation reservation = UserReservation.builder()
                .userId(userId)
                .schema(schema)
                .reservedAmount(schema.getStakePrice())
                .valuationDelta(valuationDeltaSafe)
                .reservedAt(now)
                .expiryAt(now.plusDays(1)) // Reservation valid for 1 day
                .incomeEarned(BigDecimal.ZERO)
                .isSold(false)
                .build();

        // Step 5. Deduct reserved amount from user's wallet and create a transaction record
        String remarks = "Investment reserved for reservationId: " + reservation.getId() +
                " and amount: " + reservation.getReservedAmount() +
                " at " + DateUtils.formatDisplayDate(LocalDateTime.now());
        TransactionDto walletTxn = updateWalletBalance(userId, schema.getStakePrice(), false, remarks);
        log.info("Wallet debited successfully - txnId: {}, userId: {}, amount: {}", walletTxn.getId(), userId, reservedAmount);

        // Step 6: Save the reservation
        UserReservation savedReservation = reservationRepository.save(reservation);
        log.info("Reservation created successfully - reservationId: {}, userId: {}", savedReservation.getId(), userId);

        sendReservationNotification(userId, reservation);
        return savedReservation;
    }


    /**
     * Deprecated reservation method with manual inputs. Not supported.
     */
    @Deprecated
    @Override
    public UserReservation reserve(Long userId, Long schemaId, BigDecimal amount) {
        return null;
    }


    /**
     * Sells an existing reservation by marking it as sold and setting the sold timestamp.
     * Future implementation can include profit calculation.
     *
     * @param orderId ID of the reservation to sell
     * @param userId  User who owns the reservation
     */
    @Override
    public void sellReservation(Long orderId, Long userId) {
        log.info("Attempting to sell reservation - orderId: {}, userId: {}", orderId, userId);

        // Step 1: Fetch active (unsold) reservation for the given user
        UserReservation reservation = reservationRepository
                .findByIdAndUserIdAndIsSoldFalse(orderId, userId)
                .orElseThrow(() -> {
                    log.warn("Sell failed - reservation not found or already sold. reservationId: {}, userId: {}", orderId, userId);
                    throw new ValidationException("Reservation not found or already sold.", ErrorCode.RESERVATION_NOT_FOUND_OR_SOLD );
                });

        // Step 2: Calculate sold amount and realized gain/loss
        BigDecimal reservedAmount = reservation.getReservedAmount();
        BigDecimal valuationDelta = reservation.getValuationDelta() != null ? reservation.getValuationDelta() : BigDecimal.ZERO;
        BigDecimal soldAmount = reservedAmount.add(valuationDelta);
        BigDecimal realizedGain = soldAmount.subtract(reservedAmount);

        // Step 3: Mark reservation as sold with soldAmount and timestamp
        reservation.setSold(true);
        reservation.setSoldAmount(soldAmount);
        reservation.setSoldAt(LocalDateTime.now());
        reservation.setProfit(realizedGain);

        // Step 4: Credit sold amount to user's wallet
        String remarks = "Stake sold for reservationId: " + reservation.getId() +
                ", amount: " + reservedAmount +
                ", gain: " + realizedGain +
                ", at: " + DateUtils.formatDisplayDate(LocalDateTime.now());

        TransactionDto walletTxn = updateWalletBalance(userId, reservedAmount, true, remarks);
        log.info("Wallet credited successfully - txnId: {}, userId: {}, amount: {}", walletTxn.getId(), userId, soldAmount);

        // Step 5: Persist updated reservation
        reservationRepository.save(reservation);
        log.info("Reservation marked as sold - orderId: {}, userId: {}, reservedAmount: {}, gain: {}", orderId, userId, reservedAmount, realizedGain);

        // Step 6: Publish StakeSoldEvent for downstream processing (e.g., income accrual)
        eventPublisher.publishEvent(new StakeSoldEvent(userId, reservedAmount));
        log.info("Published StakeSoldEvent for userId: {}, reservedAmount: {}", userId, reservedAmount);
        sendSaleNotification(userId, reservation, soldAmount);
    }


    /**
     * Fetch all active (unsold and unexpired) reservations for a user.
     *
     * @param userId ID of the user
     * @return List of active reservation DTOs
     */
    @Override
    public List<UserReservationDto> getReservations(Long userId, boolean activeOnly, LocalDate startDate, LocalDate endDate) {
        log.info("Fetching {} reservations for userId: {}, startDate: {}, endDate: {}",
                activeOnly ? "active" : "all", userId, startDate, endDate);

        List<UserReservation> reservations;
        if (activeOnly) {
            reservations = reservationRepository.findByUserIdAndIsSoldFalseAndExpiryAtAfter(userId, LocalDateTime.now());
        } else {
            reservations = reservationRepository.findAllReservationsWithOptionalDateRange(userId, startDate, endDate);
        }

        List<UserReservationDto> dtos = reservations.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
        log.info("Found {} {} reservations for userId: {}", dtos.size(), activeOnly ? "active" : "total", userId);
        return dtos;
    }


    /**
     * Expires all unclaimed reservations that are past their expiry time.
     * Currently marks them only; logic can be extended to refund or log investments.
     */
    @Override
    public void expireUnclaimedReservations() {
        log.info("Running expiration of unclaimed reservations...");
        List<UserReservation> expired = reservationRepository.findByIsSoldFalseAndExpiryAtBefore(LocalDateTime.now());

        if (expired.isEmpty()) {
            log.info("No unclaimed reservations to expire.");
            return;
        }

        for (UserReservation r : expired) {
            log.info("Expiring reservation id={} for user={}", r.getId(), r.getUserId());
            // You could also move to a collection or user investment if needed
        }

        reservationRepository.saveAll(expired);
        log.info("Expired {} reservations.", expired.size());
    }

    /**
     * Deducts the reserved amount from the user's wallet by creating an investment transaction.
     * Can be part of a larger transaction block if wallet supports rollback.
     *
     * @param userId        ID of the user
     * @param amount Amount to deduct from the wallet
     */
    private TransactionDto updateWalletBalance(Long userId, BigDecimal amount, boolean isCredit, String remarks) {
        String operation = isCredit ? "Crediting" : "Debiting";
        log.info("{} wallet balance - userId: {}, amount: {}", operation, userId, amount);

        WalletUpdateRequest walletUpdateRequest = new WalletUpdateRequest(
                amount,
                TransactionType.INVESTMENT_RESERVE,
                isCredit,
                "investment-reserved",
                remarks,
                null
        );
        TransactionDto txn = walletApi.updateWalletBalance(userId, walletUpdateRequest);
        if (txn == null || txn.getId() == null) {
            String failOp = isCredit ? "credit" : "debit";
            log.error("Wallet deduction failed - userId: {}, amount: {}", userId, amount);
            throw new ValidationException("Wallet " + failOp + " failed", ErrorCode.WALLET_DEDUCTION_FAILED);
        }
        String successOp = isCredit ? "credit" : "debit";
        log.info("Wallet {} successful - txnId: {}, userId: {}, amount: {}", successOp, txn.getId(), userId, amount);
        return txn;
    }


    private void sendReservationNotification(Long userId, UserReservation reservation) {
        String title = "Your investment stake has been reserved!";
        String message = String.format(
                "You successfully reserved a stake worth %s (Reservation ID: %d). Your investment is now active. Check your portfolio for details.",
                reservation.getReservedAmount().toPlainString(),
                reservation.getId()
        );

        publishInAppNotification(userId, title, message);
    }

    private void sendSaleNotification(Long userId, UserReservation reservation, BigDecimal soldAmount) {
        String title = "Your stake has been successfully sold!";
        String message = String.format(
                "You earned a total of %s from your stake (Reservation ID: %d). Check your wallet for details.",
                soldAmount.toPlainString(),
                reservation.getId()
        );

        publishInAppNotification(userId, title, message);
    }


    @Async
    private void publishInAppNotification(Long userId, String title, String message) {
        eventPublisher.publishEvent(
                new NotificationEvent(this,
                        NotificationRequest.forInApp(
                                String.valueOf(userId),
                                title,
                                message
                        )
                )
        );
    }
}
