package com.gestion.alojamientos.dto.booking.detailBooking;

import com.gestion.alojamientos.model.accomodation.Services;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO para actualizar los detalles de una reserva existente.
 * Solo se incluyen los campos que pueden cambiar.
 */
public record DetailBookingUpdateDTO(

        /**
         * Identificador de la reserva
         * Campo obligatorio
         */
        Long idBooking,


        /**
         * Identificador del alojamiento
         * Campo obligatorio
         */
        Long idAccommodation,


        /**
         * Identificador del detalle de reserva
         * Campo obligatorio
         */
        Long idDetailBooking,

        /**
         * Fecha de inicio de la reserva (opcional)
         */
        LocalDate checkInDate,

        /**
         * Fecha de salida de la reserva (opcional)
         */
        LocalDate checkOutDate,

        /**
         * Número de huéspedes (opcional)
         */
        Integer numberOfGuest,
        /**
         * Identificador del cargo por el servicio
         * Campo  obligatorio
         */
        @NotBlank @NotNull @NotEmpty
        Long serviceFeeId


) {}
