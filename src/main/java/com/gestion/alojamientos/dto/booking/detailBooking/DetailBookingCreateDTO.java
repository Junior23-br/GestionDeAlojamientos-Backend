package com.gestion.alojamientos.dto.booking.detailBooking;

import com.gestion.alojamientos.model.accomodation.Services;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record  DetailBookingCreateDTO (



        /**
         * Fecha de inicio de la reserva
         * Campo obligatorio
         */
        @NotBlank @NotNull @NotEmpty
        LocalDate checkInDate,             // Fecha de entrada

        /**
         * Fecha de salida de la reserva
         * Campo obligatorio
         */
        @NotBlank @NotNull @NotEmpty
        LocalDate checkOutDate,             // Fecha de salida


        /**
         * Numero de personas las que se alojaran en el alojamiento
         * Campo obligatorio
         */
        @NotBlank @NotNull @NotEmpty
        Integer numberOfGuest,              // Número de huéspedes


        /**
         * Precio por noche
         * Campo obligatorio
         */
        @NotBlank @NotNull @NotEmpty
        Double priceNightAccommodation,     // Precio por noche


        /**
         * Subtotal de la reserva
         * Campo obligatorio
         */
        @NotBlank @NotNull @NotEmpty
        Double subTotal,                    // Subtotal calculado


        /**
         * Numero de personas las que se alojaran en el alojamiento
         * Campo no obligatorio
         */

        Double discount,                    // Descuento aplicado (opcional)


        /**
         * Identificador del cargo por el servicio
         * Campo  obligatorio
         */
        @NotBlank @NotNull @NotEmpty
        Long serviceFeeId,                  // ID del cargo por servicio (ServiceFee)


        /**
         * Lista de servicios del alojamiento
         * Campo no obligatorio
         */
        List<Services>listServicesIds         // IDs de los servicios seleccionados
) {
}
