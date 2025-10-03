package com.gestion.alojamientos.dto.booking.detailBooking;

import com.gestion.alojamientos.dto.transaction.ServiceFeeDTO;
import com.gestion.alojamientos.model.accomodation.Services;

import java.time.LocalDate;
import java.util.List;

/**
 * Un DTO (Data Transfer Object) es un objeto plano que se usa para intercambiar datos entre capas
 * Tiene la información basica del detalle del alojamiento
 *
 */
public record  DetailBookingDTO (Long idDetailBooking, LocalDate checkIn, LocalDate checkOut,
                                 Integer numberOfGuest, Double priceNightAccommodation,
                                 Double subTotal, Double discount, ServiceFeeDTO serviceDTO, List<Services> servicesList, Long idBooking){
}
