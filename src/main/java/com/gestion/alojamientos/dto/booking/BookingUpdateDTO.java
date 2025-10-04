package com.gestion.alojamientos.dto.booking;

import com.gestion.alojamientos.dto.booking.detailBooking.DetailBookingCreateDTO;
import com.gestion.alojamientos.dto.booking.detailBooking.DetailBookingUpdateDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record BookingUpdateDTO(

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
        DetailBookingUpdateDTO detailBookingUpdateDTO,

        /**
         * Identificador del alojamiento
         * Campo obligatorio
         */
        @NotBlank @NotNull @NotEmpty
        Long idAccommodation,

        /**
         * Fecha de actualizacion
         * Campo obligatorio
         */
        @NotBlank@NotNull@NotEmpty
        LocalDateTime updateTime
) {

}
