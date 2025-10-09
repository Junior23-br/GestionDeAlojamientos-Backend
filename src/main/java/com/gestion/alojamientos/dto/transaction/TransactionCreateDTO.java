package com.gestion.alojamientos.dto.transaction;


import jakarta.validation.constraints.*;

/**
 * DTO utilizado para la creación de una nueva transacción.
 * Contiene los datos necesarios para registrar una relación entre un voucher y su titular.
 */
public record TransactionCreateDTO(

        /**
         * ID del voucher asociado a la transacción.
         * Representa la relación One-to-One con la entidad Voucher.
         * Es obligatorio y no puede ser nulo.
         */
        @NotNull(message = "El ID del voucher no puede ser nulo")
        Long voucherId,

        /**
         * ID del huésped titular (holder) de la transacción.
         * Representa la relación Many-to-One con la entidad Guest.
         * Es obligatorio y no puede ser nulo.
         */
        @NotNull(message = "El ID del titular no puede ser nulo")
        Long holderId

) {}
