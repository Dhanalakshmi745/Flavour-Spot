package com.flavorspot.service;

import com.flavorspot.dto.request.ReservationRequest;
import com.flavorspot.dto.response.ReservationResponse;
import com.flavorspot.entity.Reservation;
import com.flavorspot.entity.ReservationStatus;
import com.flavorspot.entity.RestaurantTable;
import com.flavorspot.entity.Role;
import com.flavorspot.entity.User;
import com.flavorspot.exception.BadRequestException;
import com.flavorspot.repository.ReservationRepository;
import com.flavorspot.repository.RestaurantTableRepository;
import com.flavorspot.repository.UserRepository;
import com.flavorspot.service.impl.ReservationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private RestaurantTableRepository tableRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    private User sampleUser;
    private RestaurantTable sampleTable;

    @BeforeEach
    void setUp() {
        sampleUser = new User("Jane Doe", "jane@example.com", "hashedPass", "+1 555-0199", Role.ROLE_CUSTOMER);
        sampleUser.setId(10L);

        sampleTable = new RestaurantTable("T-01", 4, "MAIN_DINING", true);
        sampleTable.setId(1L);
    }

    @Test
    @DisplayName("Should throw BadRequestException when reservation date is in the past")
    void bookReservation_ThrowsException_WhenPastDate() {
        ReservationRequest request = new ReservationRequest(
                10L, 1L, LocalDate.now().minusDays(1), LocalTime.of(19, 0), 2, "Window"
        );

        when(userRepository.findById(10L)).thenReturn(Optional.of(sampleUser));

        BadRequestException ex = assertThrows(BadRequestException.class, () -> {
            reservationService.bookReservation(request);
        });

        assertTrue(ex.getMessage().contains("cannot be in the past"));
        verify(reservationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw BadRequestException when party size exceeds table capacity")
    void bookReservation_ThrowsException_WhenGuestsExceedCapacity() {
        LocalDate futureDate = LocalDate.now().plusDays(2);
        ReservationRequest request = new ReservationRequest(
                10L, 1L, futureDate, LocalTime.of(19, 0), 6, null // 6 guests for 4-seater table
        );

        when(userRepository.findById(10L)).thenReturn(Optional.of(sampleUser));
        when(tableRepository.findById(1L)).thenReturn(Optional.of(sampleTable));

        BadRequestException ex = assertThrows(BadRequestException.class, () -> {
            reservationService.bookReservation(request);
        });

        assertTrue(ex.getMessage().contains("only seats up to 4 guests"));
        verify(reservationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw BadRequestException when table already has a conflicting reservation")
    void bookReservation_ThrowsException_WhenConflictingBookingExists() {
        LocalDate futureDate = LocalDate.now().plusDays(2);
        LocalTime time = LocalTime.of(19, 0);

        ReservationRequest request = new ReservationRequest(
                10L, 1L, futureDate, time, 2, null
        );

        Reservation existingConflict = new Reservation(sampleUser, sampleTable, futureDate, time, 2, null, ReservationStatus.CONFIRMED);

        when(userRepository.findById(10L)).thenReturn(Optional.of(sampleUser));
        when(tableRepository.findById(1L)).thenReturn(Optional.of(sampleTable));
        when(reservationRepository.findConflictingReservations(1L, futureDate, time))
                .thenReturn(List.of(existingConflict));

        BadRequestException ex = assertThrows(BadRequestException.class, () -> {
            reservationService.bookReservation(request);
        });

        assertTrue(ex.getMessage().contains("already booked"));
        verify(reservationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should successfully book reservation when valid and no conflict exists")
    void bookReservation_Succeeds_WhenValid() {
        LocalDate futureDate = LocalDate.now().plusDays(2);
        LocalTime time = LocalTime.of(19, 0);

        ReservationRequest request = new ReservationRequest(
                10L, 1L, futureDate, time, 2, "Anniversary"
        );

        when(userRepository.findById(10L)).thenReturn(Optional.of(sampleUser));
        when(tableRepository.findById(1L)).thenReturn(Optional.of(sampleTable));
        when(reservationRepository.findConflictingReservations(1L, futureDate, time))
                .thenReturn(Collections.emptyList());

        Reservation savedEntity = new Reservation(sampleUser, sampleTable, futureDate, time, 2, "Anniversary", ReservationStatus.CONFIRMED);
        savedEntity.setId(55L);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(savedEntity);

        ReservationResponse response = reservationService.bookReservation(request);

        assertNotNull(response);
        assertEquals(55L, response.getId());
        assertEquals("T-01", response.getTableNumber());
        assertEquals(ReservationStatus.CONFIRMED, response.getStatus());
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }
}
