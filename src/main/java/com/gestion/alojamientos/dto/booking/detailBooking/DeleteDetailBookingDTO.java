package com.gestion.alojamientos.dto.booking.detailBooking;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record DeleteDetailBookingDTO(
        /**
         * Identificador de el detalle de la reserva
         * Campo obligatorio
         */
        @NotBlank @NotNull @NotEmpty
        Long idDetailBooking,

        /**
         * Identificador de la reserva
         * Campo obligatorio
         */
        @NotBlank @NotNull @NotEmpty
        Long idBooking
) {
}
