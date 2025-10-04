package com.gestion.alojamientos.dto.booking;

import com.gestion.alojamientos.dto.booking.detailBooking.DeleteDetailBookingDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record DeleteBookingDTO(

        /**
         * Identificador de la reserva
         * Campo obligatorio
         */
        @NotNull @NotEmpty @NotBlank
        Long idBooking,

        /**
         * Identificador del alojamiento para eliminar la reserva del alojamiento
         * Campo obligatorio
         */
        @NotNull @NotEmpty @NotBlank
        Long idAccommodation,

        /**
         * DTO para la eliminacion logica del detalle de alojamiento
         */
        @NotNull @NotEmpty @NotBlank
        DeleteDetailBookingDTO deleteDetailBookingDTO,

        /**
         * Identificador del huesped para eliminar la reserva de
         * la lista de resevas del huesped
         * Campo obligatorio
         */
        @NotNull @NotEmpty @NotBlank
        Long idGuest

) {
}
