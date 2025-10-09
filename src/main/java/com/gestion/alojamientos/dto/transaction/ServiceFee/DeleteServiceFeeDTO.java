package com.gestion.alojamientos.dto.transaction.ServiceFee;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DeleteServiceFeeDTO (
        /**
         * Identificador de la comision, para eliminacion fisica, no logica
         */
        @NotNull@NotBlank
        Long id
) {
}
