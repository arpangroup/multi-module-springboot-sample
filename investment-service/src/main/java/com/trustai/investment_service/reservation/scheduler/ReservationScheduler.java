package com.trustai.investment_service.reservation.scheduler;

import com.trustai.investment_service.reservation.entity.UserReservation;
import com.trustai.investment_service.reservation.repository.UserReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationScheduler {
    private final UserReservationRepository reservationRepository;

    /**
     * Cron job to expire unsold reservations daily.
     * This runs every day at 1:00 AM.
     */
    @Scheduled(cron = "0 0 1 * * *") // Runs at 01:00 AM daily server time
    public void expireUnclaimedReservations() {
        LocalDateTime now = LocalDateTime.now();
        log.info("Starting scheduled task: expireUnclaimedReservations at {}", now);
        List<UserReservation> expiredReservations = reservationRepository.findByIsSoldFalseAndExpiryAtBefore(now);

        if (expiredReservations.isEmpty()) {
            log.info("No reservations to expire at {}", now);
            return;
        }

        log.info("Found {} expired reservations to process.", expiredReservations.size());

        for (UserReservation reservation : expiredReservations) {
            log.debug("Expiring reservation id={} for userId={} schemaId={}", reservation.getId(), reservation.getUserId(), reservation.getSchema().getId());

            // Marking them as expired by keeping them unsold and past expiry.
            // Move it to user’s collection or investment table
            reservation.setSold(false); // Or keep as unsold expired
            // For now, just marking them as expired by keeping them unsold and past expiry
            // Optionally create UserInvestment for permanent holding
        }

        reservationRepository.saveAll(expiredReservations);
        log.info("Expired {} reservations successfully.", expiredReservations.size());
    }
}
