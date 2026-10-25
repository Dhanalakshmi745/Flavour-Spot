package com.flavorspot.controller;

import com.flavorspot.dto.request.LoginRequest;
import com.flavorspot.dto.request.RegisterRequest;
import com.flavorspot.dto.response.ApiResponse;
import com.flavorspot.dto.response.AuthResponse;
import com.flavorspot.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Customer registration endpoint.
     * Accessible by anyone.
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.registerCustomer(request);
        return new ResponseEntity<>(
                ApiResponse.success("Registration successful! Welcome to Flavor Spot.", response),
                HttpStatus.CREATED
        );
    }

    /**
     * Customer & Admin login endpoint.
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(
                ApiResponse.success("Login successful!", response)
        );
    }

    /**
     * Fetch user profile by ID.
     */
    @GetMapping("/profile/{userId}")
    public ResponseEntity<ApiResponse<AuthResponse>> getProfile(@PathVariable Long userId) {
        AuthResponse response = authService.getUserProfile(userId);
        return ResponseEntity.ok(
                ApiResponse.success("Profile retrieved successfully", response)
        );
    }

    /**
     * Fetch user profile by email query param.
     */
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<AuthResponse>> getProfileByEmail(@RequestParam String email) {
        AuthResponse response = authService.getUserProfileByEmail(email);
        return ResponseEntity.ok(
                ApiResponse.success("Profile retrieved successfully", response)
        );
    }

    /**
     * Logout endpoint.
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        return ResponseEntity.ok(
                ApiResponse.success("Logged out successfully.")
        );
    }
}
