package com.flavorspot.service.impl;

import com.flavorspot.dto.request.ReservationRequest;
import com.flavorspot.dto.request.TableRequest;
import com.flavorspot.dto.response.ReservationResponse;
import com.flavorspot.dto.response.TableResponse;
import com.flavorspot.entity.Reservation;
import com.flavorspot.entity.ReservationStatus;
import com.flavorspot.entity.RestaurantTable;
import com.flavorspot.entity.User;
import com.flavorspot.exception.BadRequestException;
import com.flavorspot.exception.ResourceNotFoundException;
import com.flavorspot.repository.ReservationRepository;
import com.flavorspot.repository.RestaurantTableRepository;
import com.flavorspot.repository.UserRepository;
import com.flavorspot.service.ReservationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationServiceImpl implements ReservationService {

    private static final LocalTime OPENING_TIME = LocalTime.of(11, 0);
    private static final LocalTime CLOSING_TIME = LocalTime.of(22, 0);

    private final ReservationRepository reservationRepository;
    private final RestaurantTableRepository tableRepository;
    private final UserRepository userRepository;

    public ReservationServiceImpl(ReservationRepository reservationRepository,
                                  RestaurantTableRepository tableRepository,
                                  UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.tableRepository = tableRepository;
        this.userRepository = userRepository;
    }

    // -------------------------------------------------------------
    // Table Inventory Management
    // -------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public List<TableResponse> getAllTables() {
        return tableRepository.findAll().stream()
                .map(this::mapToTableResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TableResponse createTable(TableRequest request) {
        tableRepository.findByTableNumber(request.getTableNumber().trim()).ifPresent(t -> {
            throw new BadRequestException("Table with number '" + request.getTableNumber() + "' already exists.");
        });

        RestaurantTable table = new RestaurantTable(
                request.getTableNumber().trim().toUpperCase(),
                request.getSeatingCapacity(),
                request.getLocationArea(),
                request.getIsActive() != null ? request.getIsActive() : true
        );

        RestaurantTable saved = tableRepository.save(table);
        return mapToTableResponse(saved);
    }

    @Override
    @Transactional
    public TableResponse updateTable(Long id, TableRequest request) {
        RestaurantTable table = tableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RestaurantTable", "id", id));

        table.setTableNumber(request.getTableNumber().trim().toUpperCase());
        table.setSeatingCapacity(request.getSeatingCapacity());
        table.setLocationArea(request.getLocationArea());
        if (request.getIsActive() != null) {
            table.setIsActive(request.getIsActive());
        }

        RestaurantTable updated = tableRepository.save(table);
        return mapToTableResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TableResponse> getAvailableTables(LocalDate date, LocalTime time, Integer guestCount) {
        validateOperatingHoursAndDate(date, time);

        // Fetch all active tables capable of seating the guest count
        List<RestaurantTable> eligibleTables = tableRepository.findBySeatingCapacityGreaterThanEqualAndIsActiveTrue(guestCount);

        // Filter out tables that already have active overlapping bookings
        return eligibleTables.stream()
                .filter(table -> reservationRepository.findConflictingReservations(table.getId(), date, time).isEmpty())
                .sorted(Comparator.comparingInt(RestaurantTable::getSeatingCapacity))
                .map(this::mapToTableResponse)
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------------
    // Reservation Core Engine
    // -------------------------------------------------------------

    @Override
    @Transactional
    public ReservationResponse bookReservation(ReservationRequest request) {
        // 1. Validate customer exists
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));

        // 2. Validate timing & operating hours
        validateOperatingHoursAndDate(request.getReservationDate(), request.getReservationTime());

        RestaurantTable selectedTable;

        // 3. Resolve table: Explicit selection vs Auto-assignment
        if (request.getTableId() != null) {
            selectedTable = tableRepository.findById(request.getTableId())
                    .orElseThrow(() -> new ResourceNotFoundException("RestaurantTable", "id", request.getTableId()));

            if (!Boolean.TRUE.equals(selectedTable.getIsActive())) {
                throw new BadRequestException("Table " + selectedTable.getTableNumber() + " is currently out of service.");
            }

            if (request.getGuestCount() > selectedTable.getSeatingCapacity()) {
                throw new BadRequestException("Table " + selectedTable.getTableNumber() +
                        " only seats up to " + selectedTable.getSeatingCapacity() + " guests.");
            }

            // Conflict Check
            List<Reservation> conflicts = reservationRepository.findConflictingReservations(
                    selectedTable.getId(), request.getReservationDate(), request.getReservationTime());

            if (!conflicts.isEmpty()) {
                throw new BadRequestException("Table " + selectedTable.getTableNumber() +
                        " is already booked at " + request.getReservationTime() + " on " + request.getReservationDate() +
                        ". Please select another table or time.");
            }
        } else {
            // Auto-assign the most optimal available table
            List<TableResponse> availableTables = getAvailableTables(
                    request.getReservationDate(), request.getReservationTime(), request.getGuestCount());

            if (availableTables.isEmpty()) {
                throw new BadRequestException("No tables are available for " + request.getGuestCount() +
                        " guests at " + request.getReservationTime() + " on " + request.getReservationDate() + ".");
            }

            Long optimalTableId = availableTables.get(0).getId();
            selectedTable = tableRepository.findById(optimalTableId).orElseThrow();
        }

        // 4. Create and persist reservation with CONFIRMED status
        Reservation reservation = new Reservation(
                user,
                selectedTable,
                request.getReservationDate(),
                request.getReservationTime(),
                request.getGuestCount(),
                request.getSpecialRequests(),
                ReservationStatus.CONFIRMED
        );

        Reservation saved = reservationRepository.save(reservation);
        return mapToReservationResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponse> getReservationsByCustomer(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }

        return reservationRepository.findByUserIdOrderByReservationDateDescReservationTimeDesc(userId).stream()
                .map(this::mapToReservationResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponse> getAllReservationsForAdmin() {
        return reservationRepository.findAllByOrderByReservationDateDescReservationTimeDesc().stream()
                .map(this::mapToReservationResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponse> getReservationsByStatus(ReservationStatus status) {
        return reservationRepository.findByStatusOrderByReservationDateAscReservationTimeAsc(status).stream()
                .map(this::mapToReservationResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ReservationResponse getReservationById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", id));
        return mapToReservationResponse(reservation);
    }

    @Override
    @Transactional
    public ReservationResponse updateReservationStatus(Long id, ReservationStatus newStatus) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", id));

        reservation.setStatus(newStatus);
        Reservation updated = reservationRepository.save(reservation);
        return mapToReservationResponse(updated);
    }

    @Override
    @Transactional
    public ReservationResponse cancelReservation(Long id, Long userId) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", id));

        // Ensure customer owns the reservation
        if (!reservation.getUser().getId().equals(userId)) {
            throw new BadRequestException("You are not authorized to cancel this reservation.");
        }

        if (reservation.getStatus() == ReservationStatus.COMPLETED) {
            throw new BadRequestException("Completed reservations cannot be cancelled.");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        Reservation updated = reservationRepository.save(reservation);
        return mapToReservationResponse(updated);
    }

    // -------------------------------------------------------------
    // Private Helpers & Validation
    // -------------------------------------------------------------

    private void validateOperatingHoursAndDate(LocalDate date, LocalTime time) {
        if (date.isBefore(LocalDate.now())) {
            throw new BadRequestException("Reservation date cannot be in the past.");
        }

        if (date.isEqual(LocalDate.now()) && time.isBefore(LocalTime.now())) {
            throw new BadRequestException("Reservation time has already passed for today.");
        }

        if (time.isBefore(OPENING_TIME) || time.isAfter(CLOSING_TIME)) {
            throw new BadRequestException("Flavor Spot dining hours are from " +
                    OPENING_TIME + " to " + CLOSING_TIME + ". Please pick a valid time slot.");
        }
    }

    private TableResponse mapToTableResponse(RestaurantTable table) {
        return new TableResponse(
                table.getId(),
                table.getTableNumber(),
                table.getSeatingCapacity(),
                table.getLocationArea(),
                table.getIsActive()
        );
    }

    private ReservationResponse mapToReservationResponse(Reservation r) {
        return new ReservationResponse(
                r.getId(),
                r.getUser().getId(),
                r.getUser().getFullName(),
                r.getUser().getEmail(),
                r.getUser().getPhoneNumber(),
                r.getTable().getId(),
                r.getTable().getTableNumber(),
                r.getTable().getSeatingCapacity(),
                r.getTable().getLocationArea(),
                r.getReservationDate(),
                r.getReservationTime(),
                r.getGuestCount(),
                r.getSpecialRequests(),
                r.getStatus(),
                r.getCreatedAt()
        );
    }
}
