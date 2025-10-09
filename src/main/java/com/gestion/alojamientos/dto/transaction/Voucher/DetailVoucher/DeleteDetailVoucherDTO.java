package com.gestion.alojamientos.dto.transaction.Voucher.DetailVoucher;


import jakarta.validation.constraints.*;

/**
 * DTO utilizado para la eliminación de un DetailVoucher existente.
 * Solo contiene el identificador necesario para realizar la operación de borrado.
 */
public record DeleteDetailVoucherDTO(

        /**
         * ID del detalle del voucher que se desea eliminar.
         * Es obligatorio para identificar el registro que será borrado.
         */
        @NotNull(message = "El ID del detalle del voucher no puede ser nulo")
        Long id

) {}
