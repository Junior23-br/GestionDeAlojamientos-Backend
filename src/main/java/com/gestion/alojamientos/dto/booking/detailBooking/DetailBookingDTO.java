package com.gestion.alojamientos.dto.booking.detailBooking;

import com.gestion.alojamientos.dto.transaction.ServiceFee.ServiceFeeDTO;
import com.gestion.alojamientos.model.accomodation.Services;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO que representa el detalle de una reserva.
 * Contiene información básica del alojamiento y sus servicios asociados.
 */
public record DetailBookingDTO(
        Long id,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        Integer numberOfGuest,
        Double priceNightAccommodation,
        Double subTotal,
        Double discount,
        ServiceFeeDTO serviceFeeDTO,   // nombre corregido
        List<Services> listServices,   // nombre coherente con la entidad
        Long idBooking                 // ID de la reserva asociada
) { }
