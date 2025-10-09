package com.gestion.alojamientos.dto.transaction.Voucher;

import com.gestion.alojamientos.dto.FinancialAccount.FinancialAccountDTO;
import com.gestion.alojamientos.dto.booking.BookingDTO;
import com.gestion.alojamientos.dto.guest.GuestDto;
import com.gestion.alojamientos.dto.transaction.Voucher.DetailVoucher.DetailVoucherDTO;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record VoucherDTO(


        /**
         * Identificador del Voucher
         */
        @NotNull @NotEmpty
        Long id,

        /**
         * Fecha de creacion
         */
        @NotNull @NotEmpty
        LocalDateTime creattionDate,

        /**
         * DTO del Guest que esta asociado a la factura
         */
        @NotNull @NotEmpty
        GuestDto guestDTO,

        /**
         * Sub total de la factura
         */
        @NotNull
        Double subTotal,


        /**
         * Valor del impuesto cargado legalmente por el gobierno
         */
        @NotNull
        Double tax,

        /**
         * Descuento aplicado a la reserva, es opcional
         */
        Double discount,


        /**
         * Total del pago con impuestos y reserva incluida
         */
        @NotNull @NotEmpty
        Double total,


        /**
         * Estado de la factura
         */
        @NotNull @NotEmpty
        String voucherState,

        /**
         *DTO de la cuenta con la que se pago la factura
         */
        @NotNull @NotEmpty
        FinancialAccountDTO financialAccountDTO,

        /**
         * DTO del detalle de la factura
         *
         */
        @NotNull @NotEmpty
        DetailVoucherDTO detailVoucherDTO,


        /**
         * DTO de la reserva asociado a la factura
         */
        @NotNull @NotEmpty
        BookingDTO bookingDTO






) {
}
