package com.gestion.alojamientos.dto.transaction.Voucher;

import com.gestion.alojamientos.dto.transaction.Voucher.DetailVoucher.DetailVoucherDTO;
import jakarta.validation.constraints.*;
import java.io.Serializable;

/**
 * DTO utilizado para la creación de un nuevo Voucher.
 * No incluye los campos generados automáticamente (id, creationDate).
 */
public record VoucherCreateDTO(

        /**
         * ID del huésped asociado al voucher.
         * Este campo es obligatorio y no puede ser nulo.
         */
        @NotNull(message = "El ID del huésped no puede ser nulo")
        Long guestId,

        /**
         * Subtotal del voucher (sin impuestos ni descuentos).
         * Debe ser un valor positivo o cero.
         */
        @NotNull(message = "El subtotal no puede ser nulo")
        @PositiveOrZero(message = "El subtotal no puede ser negativo")
        Double subTotal,

        /**
         * Valor del impuesto aplicado al subtotal.
         * Debe ser un valor positivo o cero.
         */
        @NotNull(message = "El valor del impuesto no puede ser nulo")
        @PositiveOrZero(message = "El impuesto no puede ser negativo")
        Double tax,

        /**
         * Descuento aplicado al voucher.
         * Es opcional, pero si se proporciona, no puede ser negativo.
         */
        @PositiveOrZero(message = "El descuento no puede ser negativo")
        Double discount,

        /**
         * Total final del voucher (subtotal + impuestos - descuentos).
         * Debe ser mayor que cero.
         */
        @NotNull(message = "El total no puede ser nulo")
        @Positive(message = "El total debe ser mayor que cero")
        Double total,

        /**
         * Estado actual del voucher.
         * Debe enviarse como texto y no puede estar vacío (ejemplo: "PENDING", "PAID", "CANCELLED").
         */
        @NotBlank(message = "El estado del voucher no puede estar vacío")
        String voucherState,

        /**
         * ID del método de pago asociado al voucher.
         * Es opcional y puede ser nulo si no se ha definido un método de pago.
         */
        Long paymentMethodId,

        /**
         * DTO del detalle del voucher asociado.
         * Este campo es obligatorio y no puede ser nulo.
         */

        DetailVoucherDTO detailVoucherDTO,

        /**
         * ID de la reserva asociada al voucher.
         * Es opcional y puede ser nulo si no se asocia una reserva.
         */
        Long bookingId

) {}

