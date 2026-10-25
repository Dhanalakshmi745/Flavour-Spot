package com.flavorspot.service.impl;

import com.flavorspot.dto.request.LoginRequest;
import com.flavorspot.dto.request.RegisterRequest;
import com.flavorspot.dto.response.AuthResponse;
import com.flavorspot.entity.Role;
import com.flavorspot.entity.User;
import com.flavorspot.exception.BadRequestException;
import com.flavorspot.exception.ResourceNotFoundException;
import com.flavorspot.repository.UserRepository;
import com.flavorspot.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public AuthResponse registerCustomer(RegisterRequest request) {
        // 1. Business Check: Duplicate email prevention
        if (userRepository.existsByEmail(request.getEmail().trim().toLowerCase())) {
            throw new BadRequestException("An account with email '" + request.getEmail() + "' already exists.");
        }

        // 2. Hash raw password securely with BCrypt
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // 3. Create and persist customer entity
        User customer = new User(
                request.getFullName().trim(),
                request.getEmail().trim().toLowerCase(),
                hashedPassword,
                request.getPhoneNumber().trim(),
                Role.ROLE_CUSTOMER
        );

        User savedUser = userRepository.save(customer);

        // 4. Generate authentication token
        String token = "FS-" + UUID.randomUUID().toString();

        return new AuthResponse(
                savedUser.getId(),
                savedUser.getFullName(),
                savedUser.getEmail(),
                savedUser.getPhoneNumber(),
                savedUser.getRole(),
                token
        );
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        // 1. Find user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Invalid email or password."));

        // 2. Verify hashed password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid email or password.");
        }

        // 3. Generate session/auth token
        String token = "FS-" + UUID.randomUUID().toString();

        return new AuthResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRole(),
                token
        );
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        return new AuthResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRole(),
                null
        );
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse getUserProfileByEmail(String email) {
        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        return new AuthResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRole(),
                null
        );
    }
}
