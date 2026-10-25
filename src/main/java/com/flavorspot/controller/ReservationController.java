package com.flavorspot.controller;

import com.flavorspot.dto.request.ReservationRequest;
import com.flavorspot.dto.request.TableRequest;
import com.flavorspot.dto.response.ApiResponse;
import com.flavorspot.dto.response.ReservationResponse;
import com.flavorspot.dto.response.TableResponse;
import com.flavorspot.entity.ReservationStatus;
import com.flavorspot.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    // -------------------------------------------------------------
    // Table Inventory Endpoints
    // -------------------------------------------------------------

    @GetMapping("/tables")
    public ResponseEntity<ApiResponse<List<TableResponse>>> getAllTables() {
        List<TableResponse> tables = reservationService.getAllTables();
        return ResponseEntity.ok(
                ApiResponse.success("Tables retrieved successfully", tables)
        );
    }

    @PostMapping("/tables")
    public ResponseEntity<ApiResponse<TableResponse>> createTable(@Valid @RequestBody TableRequest request) {
        TableResponse table = reservationService.createTable(request);
        return new ResponseEntity<>(
                ApiResponse.success("Table created successfully", table),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/tables/{id}")
    public ResponseEntity<ApiResponse<TableResponse>> updateTable(
            @PathVariable Long id,
            @Valid @RequestBody TableRequest request) {
        TableResponse table = reservationService.updateTable(id, request);
        return ResponseEntity.ok(
                ApiResponse.success("Table updated successfully", table)
        );
    }

    @GetMapping("/reservations/available-tables")
    public ResponseEntity<ApiResponse<List<TableResponse>>> getAvailableTables(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime time,
            @RequestParam Integer guests) {
        List<TableResponse> availableTables = reservationService.getAvailableTables(date, time, guests);
        return ResponseEntity.ok(
                ApiResponse.success("Available tables retrieved", availableTables)
        );
    }

    // -------------------------------------------------------------
    // Customer Reservation Endpoints
    // -------------------------------------------------------------

    @PostMapping("/reservations")
    public ResponseEntity<ApiResponse<ReservationResponse>> bookReservation(
            @Valid @RequestBody ReservationRequest request) {
        ReservationResponse reservation = reservationService.bookReservation(request);
        return new ResponseEntity<>(
                ApiResponse.success("Reservation confirmed successfully! We look forward to hosting you.", reservation),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/reservations/my")
    public ResponseEntity<ApiResponse<List<ReservationResponse>>> getMyReservations(@RequestParam Long userId) {
        List<ReservationResponse> myReservations = reservationService.getReservationsByCustomer(userId);
        return ResponseEntity.ok(
                ApiResponse.success("Customer reservations retrieved", myReservations)
        );
    }

    @GetMapping("/reservations/{id}")
    public ResponseEntity<ApiResponse<ReservationResponse>> getReservationById(@PathVariable Long id) {
        ReservationResponse reservation = reservationService.getReservationById(id);
        return ResponseEntity.ok(
                ApiResponse.success("Reservation details retrieved", reservation)
        );
    }

    @PatchMapping("/reservations/{id}/cancel")
    public ResponseEntity<ApiResponse<ReservationResponse>> cancelReservation(
            @PathVariable Long id,
            @RequestParam Long userId) {
        ReservationResponse cancelled = reservationService.cancelReservation(id, userId);
        return ResponseEntity.ok(
                ApiResponse.success("Reservation has been cancelled.", cancelled)
        );
    }

    // -------------------------------------------------------------
    // Admin Reservation Operations
    // -------------------------------------------------------------

    @GetMapping("/reservations/admin/all")
    public ResponseEntity<ApiResponse<List<ReservationResponse>>> getAllReservationsForAdmin() {
        List<ReservationResponse> all = reservationService.getAllReservationsForAdmin();
        return ResponseEntity.ok(
                ApiResponse.success("All reservations retrieved for admin", all)
        );
    }

    @GetMapping("/reservations/admin")
    public ResponseEntity<ApiResponse<List<ReservationResponse>>> getReservationsByStatus(
            @RequestParam ReservationStatus status) {
        List<ReservationResponse> filtered = reservationService.getReservationsByStatus(status);
        return ResponseEntity.ok(
                ApiResponse.success("Reservations filtered by status: " + status, filtered)
        );
    }

    @PatchMapping("/reservations/{id}/status")
    public ResponseEntity<ApiResponse<ReservationResponse>> updateStatus(
            @PathVariable Long id,
            @RequestParam ReservationStatus status) {
        ReservationResponse updated = reservationService.updateReservationStatus(id, status);
        return ResponseEntity.ok(
                ApiResponse.success("Reservation status updated to: " + status, updated)
        );
    }
}
