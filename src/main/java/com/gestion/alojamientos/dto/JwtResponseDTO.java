package com.gestion.alojamientos.dto;

public record JwtResponseDTO(
    String token,
    String tokenType, // "Bearer"
    String role,
    Long userId,
    String email
) {}
