package com.gestion.alojamientos.dto.transaction.Voucher.DetailVoucher;

import jakarta.validation.constraints.*;

/**
 * DTO utilizado para la actualización de un DetailVoucher existente.
 * Permite modificar los valores del detalle de un voucher.
 */
public record DetailVoucherUpdateDTO(

        /**
         * ID del detalle del voucher que se desea actualizar.
         * Es obligatorio para identificar el registro existente.
         */
        @NotNull(message = "El ID del detalle del voucher no puede ser nulo")
        Long id,

        /**
         * Descripción del detalle del voucher.
         * Es opcional, pero si se proporciona, no puede superar los 500 caracteres.
         */
        @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
        String description,

        /**
         * Precio por noche del alojamiento.
         * Es opcional, pero si se proporciona, debe ser mayor que cero.
         */
        @Positive(message = "El precio por noche debe ser mayor que cero")
        Double priceNight,

        /**
         * Número total de noches asociadas al voucher.
         * Es opcional, pero si se proporciona, debe ser un número positivo.
         */
        @Positive(message = "El número de noches debe ser mayor que cero")
        Integer numberNights,

        /**
         * Subtotal calculado (priceNight * numberNights).
         * Es opcional, pero si se proporciona, no puede ser negativo.
         */
        @PositiveOrZero(message = "El subtotal no puede ser negativo")
        Double subTotal

) {}
