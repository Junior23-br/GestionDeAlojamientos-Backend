package com.gestion.alojamientos.dto.transaction;

import com.gestion.alojamientos.dto.guest.GuestDto;
import com.gestion.alojamientos.dto.transaction.Voucher.VoucherDTO;
import jakarta.validation.constraints.*;

/**
 * DTO utilizado para representar una transacción existente.
 * Contiene las referencias necesarias al voucher y al titular (huésped).
 */
public record TransactionDTO(

        /**
         * ID único de la transacción.
         * Se genera automáticamente en la base de datos.
         */
        Long id,

        /**
         * DTO del voucher asociado a esta transacción.
         * Representa la relación One-to-One con la entidad Voucher.
         */
        @NotNull
        VoucherDTO voucherDTO,

        /**
         * DTO del huésped titular de la transacción.
         * Representa la relación Many-to-One con la entidad Guest.
         */
        @NotNull
        GuestDto holderDTO

) {}
