package com.flavorspot.service;

import com.flavorspot.dto.request.LoginRequest;
import com.flavorspot.dto.request.RegisterRequest;
import com.flavorspot.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse registerCustomer(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse getUserProfile(Long userId);
    AuthResponse getUserProfileByEmail(String email);
}
