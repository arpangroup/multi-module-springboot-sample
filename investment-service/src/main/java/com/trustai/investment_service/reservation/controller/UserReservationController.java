package com.trustai.investment_service.reservation.controller;

import com.trustai.common.controller.BaseController;
import com.trustai.investment_service.reservation.dto.ReservationRequest;
import com.trustai.investment_service.reservation.dto.ReservationSummary;
import com.trustai.investment_service.reservation.dto.UserReservationDto;
import com.trustai.investment_service.reservation.entity.UserReservation;
import com.trustai.investment_service.reservation.service.StakeReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Slf4j
public class UserReservationController extends BaseController {
    private final StakeReservationService reservationService;

    /**
     * Get active (visible) reservations of the user.
     */
    @GetMapping
    public ResponseEntity<List<UserReservationDto>> getOrders(
            @RequestParam(value = "activeOnly", required = false, defaultValue = "false") boolean activeOnly,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate  end
    ) {
        Long userId = getCurrentUserId();
        log.info("Fetching {} reservations for userId: {} between {} and {}",
                activeOnly ? "active" : "all", userId, start, end);
        List<UserReservationDto> reservations = reservationService.getReservations(userId, activeOnly, start, end);

        log.info("Retrieved {} {} reservations for userId: {}",
                reservations.size(),
                activeOnly ? "active" : "total",
                userId);
        return ResponseEntity.ok(reservations);
    }

    /**
     * Reserve a stake/schema for today.
     */
    @PostMapping("/reserve")
    public ResponseEntity<UserReservation> autoReserve(@RequestBody @Valid ReservationRequest request) {
        Long userId = getCurrentUserId();
        log.info("Received reserve request - userId: {}, schemaId: {}, amount: {}", userId, request.getSchemaId(), request.getAmount());
        UserReservation reservation = reservationService.autoReserve(userId);
        log.info("Reservation successful - reservationId: {}", reservation.getId());
        return ResponseEntity.ok(reservation);
    }

    /**
     * Sell a reservation.
     */
    @PostMapping("/{orderId}/sell")
    public ResponseEntity<Void> sellReservation(@PathVariable Long orderId) {
        Long userId = getCurrentUserId();
        log.info("Received sell request - orderId: {}, userId: {}", orderId, userId);
        reservationService.sellReservation(orderId, userId);
        log.info("Sell successful - orderId: {}, userId: {}", orderId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/summary")
    public ResponseEntity<ReservationSummary> reservationSummary() {
        Long userId = getCurrentUserId();
        log.info("Fetching reservation info for userId: {}", userId);
        ReservationSummary reservationSummary = reservationService.getReservationSummary(userId);
        log.info("Retrieved reservation summary for userId: {}", userId);
        return ResponseEntity.ok(reservationSummary);
    }


}
