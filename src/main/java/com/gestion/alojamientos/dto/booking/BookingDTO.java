package com.gestion.alojamientos.dto.booking;
import com.gestion.alojamientos.dto.booking.detailBooking.DetailBookingDTO;
import com.gestion.alojamientos.model.enums.StatesOfBooking;

import java.time.LocalDateTime;

/**
 * DTO que representa una reserva (Booking) en el sistema.
 * Contiene información relevante de la reserva sin exponer entidades completas.
 */
public record BookingDTO(
        Long id,
        LocalDateTime creationDate,
        LocalDateTime updateTime,
        StatesOfBooking bookingState, //
        Double totalPrice,
        Boolean paymentStatus,
        Long paymentMethodId,    // ID del método de pago
        Long guestId,            // ID del huésped
        Long detailBookingId,    // ID del detalle de reserva
        Long voucherId,          // ID del comprobante
        Long accommodationId,    // ID del alojamiento
        DetailBookingDTO detailBookingDTO
) { }

