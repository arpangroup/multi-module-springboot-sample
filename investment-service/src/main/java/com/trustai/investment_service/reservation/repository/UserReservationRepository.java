package com.trustai.investment_service.reservation.repository;

import com.trustai.investment_service.reservation.entity.UserReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserReservationRepository extends JpaRepository<UserReservation, Long> {
    boolean existsByUserIdAndReservationDate(Long userId, LocalDate reservationDate);

    @Query("SELECT ur FROM UserReservation ur WHERE ur.userId = :userId AND ur.reservationDate = :today")
    List<UserReservation> findAllByUserIdAndToday(@Param("userId") Long userId, @Param("today") LocalDate today);


    List<UserReservation> findByUserId(Long userId);

    List<UserReservation> findByUserIdAndIsSoldFalseAndExpiryAtAfter(Long userId, LocalDateTime now);

    List<UserReservation> findByIsSoldFalseAndExpiryAtBefore(LocalDateTime now);

    Optional<UserReservation> findByIdAndUserIdAndIsSoldFalse(Long reservationId, Long userId);


    @Query("""
        SELECT r FROM UserReservation r
        WHERE r.userId = :userId
          AND (:startDate IS NULL OR r.reservationDate >= :startDate)
          AND (:endDate IS NULL OR r.reservationDate <= :endDate)
    """)
    List<UserReservation> findAllReservationsWithOptionalDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
