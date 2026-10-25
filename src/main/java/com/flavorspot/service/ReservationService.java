package com.flavorspot.service;

import com.flavorspot.dto.request.ReservationRequest;
import com.flavorspot.dto.request.TableRequest;
import com.flavorspot.dto.response.ReservationResponse;
import com.flavorspot.dto.response.TableResponse;
import com.flavorspot.entity.ReservationStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ReservationService {

    // Table Inventory
    List<TableResponse> getAllTables();
    TableResponse createTable(TableRequest request);
    TableResponse updateTable(Long id, TableRequest request);
    List<TableResponse> getAvailableTables(LocalDate date, LocalTime time, Integer guestCount);

    // Reservation Workflows
    ReservationResponse bookReservation(ReservationRequest request);
    List<ReservationResponse> getReservationsByCustomer(Long userId);
    List<ReservationResponse> getAllReservationsForAdmin();
    List<ReservationResponse> getReservationsByStatus(ReservationStatus status);
    ReservationResponse getReservationById(Long id);
    ReservationResponse updateReservationStatus(Long id, ReservationStatus newStatus);
    ReservationResponse cancelReservation(Long id, Long userId);
}
