package com.example.backend.dto;

public record JwtResponse(
        String token,
        String type,
        String username
) {}
