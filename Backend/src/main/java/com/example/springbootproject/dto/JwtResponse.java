package com.example.springbootproject.dto;

import lombok.Data;

@Data
public class JwtResponse {
    private String token;
    private String role;
    private String email;

    public JwtResponse(String token, String role, String email) {
        this.token = token;
        this.role = role;
        this.email = email;
    }
}