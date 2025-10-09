package com.gestion.alojamientos.dto.accommodation.Services;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DeleteServicesDTO (
        /**
         * Identificador del servicio en la base de datos
         */

        @NotNull @NotBlank
        Long id
){
}
