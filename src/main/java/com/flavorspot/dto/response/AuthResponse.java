package com.flavorspot.dto.response;

import com.flavorspot.entity.Role;

public class AuthResponse {

    private Long id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private Role role;
    private String token; // Token or session identifier for authentication state

    public AuthResponse() {}

    public AuthResponse(Long id, String fullName, String email, String phoneNumber, Role role, String token) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.token = token;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
