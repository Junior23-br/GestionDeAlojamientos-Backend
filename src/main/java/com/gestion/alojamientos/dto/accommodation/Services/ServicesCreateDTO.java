package com.gestion.alojamientos.dto.accommodation.Services;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ServicesCreateDTO(
        /**
         * Nombre del servicio, ejemplo piscina, jacuzzi..etc
         */
        @NotNull @NotBlank
        String name
) {
}
