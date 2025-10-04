package com.gestion.alojamientos.dto.booking;

import com.gestion.alojamientos.dto.booking.detailBooking.DetailBookingDTO;
import com.gestion.alojamientos.model.enums.StatesOfBooking;

import java.time.LocalDateTime;

public record  BookingDTO(

        Long id,
        LocalDateTime creationDate,
        LocalDateTime updateTime,
        StatesOfBooking StatesOfBookingState,
        Double totalPrice,
        Boolean paymentStatus,
        Long paymentMethodId,   // en lugar de exponer el objeto completo
        Long guestId,           // solo el ID del huésped
        Long detailBookingId,   // ID del detalle
        Long voucherId,         // ID del voucher
        Long accommodationId,    // ID del alojamiento
        DetailBookingDTO detailBookingDTO
) {
}


