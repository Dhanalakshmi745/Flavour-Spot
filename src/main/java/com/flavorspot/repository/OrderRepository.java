package com.flavorspot.repository;

import com.flavorspot.entity.Order;
import com.flavorspot.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Order> findAllByOrderByCreatedAtDesc();
    List<Order> findByReservationId(Long reservationId);
    List<Order> findByStatusOrderByCreatedAtAsc(OrderStatus status);
    Long countByStatus(OrderStatus status);
}
