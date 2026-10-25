package com.flavorspot.service;

import com.flavorspot.dto.request.OrderItemRequest;
import com.flavorspot.dto.request.OrderRequest;
import com.flavorspot.dto.response.OrderResponse;
import com.flavorspot.entity.*;
import com.flavorspot.exception.BadRequestException;
import com.flavorspot.repository.MenuItemRepository;
import com.flavorspot.repository.OrderRepository;
import com.flavorspot.repository.ReservationRepository;
import com.flavorspot.repository.UserRepository;
import com.flavorspot.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private MenuItemRepository menuItemRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User sampleUser;
    private RestaurantTable sampleTable;
    private Reservation sampleReservation;
    private MenuCategory sampleCategory;
    private MenuItem samplePizza;
    private MenuItem sampleTiramisu;

    @BeforeEach
    void setUp() {
        sampleUser = new User("John Doe", "john@example.com", "pass", "+1 555-0100", Role.ROLE_CUSTOMER);
        sampleUser.setId(2L);

        sampleTable = new RestaurantTable("T-02", 4, "MAIN_DINING", true);
        sampleTable.setId(2L);

        sampleReservation = new Reservation(sampleUser, sampleTable, LocalDate.now().plusDays(1), LocalTime.of(19, 0), 2, null, ReservationStatus.CONFIRMED);
        sampleReservation.setId(100L);

        sampleCategory = new MenuCategory("Mains", "Dishes", 1);
        sampleCategory.setId(1L);

        samplePizza = new MenuItem(sampleCategory, "Margherita Pizza", "Fresh basil", new BigDecimal("14.00"), null, true);
        samplePizza.setId(5L);

        sampleTiramisu = new MenuItem(sampleCategory, "Classic Tiramisu", "Espresso", new BigDecimal("7.50"), null, true);
        sampleTiramisu.setId(7L);
    }

    @Test
    @DisplayName("Should throw BadRequestException when reservation is not confirmed or does not exist")
    void placeOrder_ThrowsException_WhenNoConfirmedReservation() {
        OrderRequest request = new OrderRequest(
                2L, 999L, List.of(new OrderItemRequest(5L, 1)), null
        );

        when(userRepository.findById(2L)).thenReturn(Optional.of(sampleUser));
        when(reservationRepository.findConfirmedReservationForUser(999L, 2L)).thenReturn(Optional.empty());

        BadRequestException ex = assertThrows(BadRequestException.class, () -> {
            orderService.placeOrder(request);
        });

        assertTrue(ex.getMessage().contains("Dine-in food orders require an active CONFIRMED table reservation"));
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should accurately calculate total price on the server ($14.00 * 2 + $7.50 * 1 = $35.50)")
    void placeOrder_CalculatesServerSidePrice_AndSavesOrder() {
        OrderRequest request = new OrderRequest(
                2L, 100L, 
                List.of(
                        new OrderItemRequest(5L, 2), // 2 x 14.00 = 28.00
                        new OrderItemRequest(7L, 1)  // 1 x 7.50  = 7.50 -> Total: 35.50
                ), 
                "No dairy allergy"
        );

        when(userRepository.findById(2L)).thenReturn(Optional.of(sampleUser));
        when(reservationRepository.findConfirmedReservationForUser(100L, 2L)).thenReturn(Optional.of(sampleReservation));
        when(menuItemRepository.findById(5L)).thenReturn(Optional.of(samplePizza));
        when(menuItemRepository.findById(7L)).thenReturn(Optional.of(sampleTiramisu));

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            o.setId(1L);
            return o;
        });

        OrderResponse response = orderService.placeOrder(request);

        assertNotNull(response);
        assertEquals(new BigDecimal("35.50"), response.getTotalAmount());
        assertEquals(OrderStatus.PLACED, response.getStatus());
        assertEquals(2, response.getItems().size());
        verify(orderRepository, times(1)).save(any(Order.class));
    }
}
