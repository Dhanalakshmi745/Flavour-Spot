package com.flavorspot.repository;

import com.flavorspot.entity.Reservation;
import com.flavorspot.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUserIdOrderByReservationDateDescReservationTimeDesc(Long userId);

    List<Reservation> findAllByOrderByReservationDateDescReservationTimeDesc();

    List<Reservation> findByStatusOrderByReservationDateAscReservationTimeAsc(ReservationStatus status);

    // Business check: Find conflicting reservations for the same table, date, and time that are not CANCELLED
    @Query("SELECT r FROM Reservation r WHERE r.table.id = :tableId " +
           "AND r.reservationDate = :date " +
           "AND r.reservationTime = :time " +
           "AND r.status IN ('PENDING', 'CONFIRMED')")
    List<Reservation> findConflictingReservations(
            @Param("tableId") Long tableId,
            @Param("date") LocalDate date,
            @Param("time") LocalTime time
    );

    // Check if customer has a confirmed reservation eligible for placing orders
    @Query("SELECT r FROM Reservation r WHERE r.id = :reservationId " +
           "AND r.user.id = :userId " +
           "AND r.status = 'CONFIRMED'")
    Optional<Reservation> findConfirmedReservationForUser(
            @Param("reservationId") Long reservationId,
            @Param("userId") Long userId
    );
}
