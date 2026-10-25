package com.flavorspot.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "restaurant_tables")
public class RestaurantTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "table_number", nullable = false, unique = true, length = 20)
    private String tableNumber;

    @Column(name = "seating_capacity", nullable = false)
    private Integer seatingCapacity;

    @Column(name = "location_area", nullable = false, length = 50)
    private String locationArea = "MAIN_DINING";

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    public RestaurantTable() {}

    public RestaurantTable(String tableNumber, Integer seatingCapacity, String locationArea, Boolean isActive) {
        this.tableNumber = tableNumber;
        this.seatingCapacity = seatingCapacity;
        this.locationArea = locationArea != null ? locationArea : "MAIN_DINING";
        this.isActive = isActive != null ? isActive : true;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTableNumber() { return tableNumber; }
    public void setTableNumber(String tableNumber) { this.tableNumber = tableNumber; }

    public Integer getSeatingCapacity() { return seatingCapacity; }
    public void setSeatingCapacity(Integer seatingCapacity) { this.seatingCapacity = seatingCapacity; }

    public String getLocationArea() { return locationArea; }
    public void setLocationArea(String locationArea) { this.locationArea = locationArea; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
