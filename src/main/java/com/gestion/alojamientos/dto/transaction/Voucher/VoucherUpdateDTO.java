package com.gestion.alojamientos.dto.transaction.Voucher;

import jakarta.validation.constraints.NotNull;

public record VoucherUpdateDTO (

        /**
         * Identificador del voucher
         */
        @NotNull
        Long idVoucher,

        /**
         * Nuevo estado de la factura
         */
        @NotNull
        String voucherState
) {
}
