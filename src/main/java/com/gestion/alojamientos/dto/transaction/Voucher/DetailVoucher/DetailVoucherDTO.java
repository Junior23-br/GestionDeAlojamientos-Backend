package com.gestion.alojamientos.dto.transaction.Voucher.DetailVoucher;

import jakarta.validation.constraints.*;

/**
 * DTO utilizado para representar o crear el detalle de un Voucher.
 * Contiene la información relacionada con el cobro por noches y subtotal.
 */
public record DetailVoucherDTO(

        /**
         * Descripción del detalle del voucher.
         * Es opcional, pero si se envía, no puede superar los 500 caracteres.
         */
        String description,

        /**
         * Precio por noche del alojamiento.
         * Este campo es obligatorio y debe ser mayor que cero.
         */
        @NotNull(message = "El precio por noche no puede ser nulo")
        @Positive(message = "El precio por noche debe ser mayor que cero")
        Double priceNight,

        /**
         * Número total de noches.
         * Este campo es obligatorio y debe ser un valor positivo.
         */
        @NotNull(message = "El número de noches no puede ser nulo")
        @Positive(message = "El número de noches debe ser mayor que cero")
        Integer numberNights,

        /**
         * Subtotal calculado (priceNight * numberNights).
         * Este campo es obligatorio y debe ser un valor positivo o cero.
         */
        @NotNull(message = "El subtotal no puede ser nulo")
        @PositiveOrZero(message = "El subtotal no puede ser negativo")
        Double subTotal,


        /**
         * Identificador de la factura
         */
        Long idVoucher
) {}
