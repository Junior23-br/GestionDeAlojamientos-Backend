package com.gestion.alojamientos.dto.booking;

import com.gestion.alojamientos.dto.booking.detailBooking.DetailBookingCreateDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record BookingCreateDTO(
        /**
         * Estado de la creacion de la reserva, se hace por medio
         * de un String para que en la capa de de servicios se maneje correctamente
         * Campo obligatorio
         */
        @NotBlank @NotNull @NotEmpty
        String bookingState,

        /**
         * Valor total de la reserva
         * Campo obligatorio
         */
        @NotBlank @NotNull @NotEmpty
        Double totalPrice,

        /**
         * Estado de pago
         * Campo obligatorio
         */
        @NotBlank @NotNull @NotEmpty
        Boolean paymenStatus,


        /**
         * Identificador del metodo de pago
         * Campo obligatorio
         */
        @NotBlank @NotNull @NotEmpty
        Long idPaymentMethod,

        /**
         * DTO del detalle de la reserva
         * Campo obligatorio
         */
        @NotNull
        DetailBookingCreateDTO detailBookingCreateDTO,

        /**
         * Identificador del alojamiento
         * Campo obligatorio
         */
        @NotBlank @NotNull @NotEmpty
        Long idAccommodation,

        /**
         * Identificador del huesped
         * Campo obligatorio
         */
        @NotBlank @NotNull @NotEmpty
        Long guestId

        ) {


}
