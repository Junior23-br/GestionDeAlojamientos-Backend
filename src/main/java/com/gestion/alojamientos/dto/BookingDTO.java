package com.gestion.alojamientos.dto;

public record BookingDTO(
    String id,
    String guestId,
    String accommodationId,
    String startDate,
    String endDate, 
    String status,
    double totalPrice
) {


}
