package com.gestion.alojamientos.dto;

public record TransactionDTO(
    String id,
    String bookingId,
    double amount,
    String currency,
    String status
) {
    
}
