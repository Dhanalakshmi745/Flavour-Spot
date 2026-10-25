package com.flavorspot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Health Check REST Controller.
 * Verifies that the Spring Boot application, Tomcat server, and REST Dispatcher Servlet
 * are operational.
 */
@RestController
@RequestMapping("/api/health")
public class HealthCheckController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> checkHealth() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("application", "Flavor Spot - Restaurant Reservation and Dining Management System");
        response.put("timestamp", LocalDateTime.now());
        response.put("phase", "Phase 2: Environment Setup & Project Configuration Complete");
        return ResponseEntity.ok(response);
    }
}
