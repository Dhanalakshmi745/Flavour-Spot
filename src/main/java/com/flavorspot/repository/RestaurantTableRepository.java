package com.flavorspot.repository;

import com.flavorspot.entity.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {
    Optional<RestaurantTable> findByTableNumber(String tableNumber);
    List<RestaurantTable> findByIsActiveTrue();
    List<RestaurantTable> findByLocationAreaAndIsActiveTrue(String locationArea);
    List<RestaurantTable> findBySeatingCapacityGreaterThanEqualAndIsActiveTrue(Integer capacity);
}
