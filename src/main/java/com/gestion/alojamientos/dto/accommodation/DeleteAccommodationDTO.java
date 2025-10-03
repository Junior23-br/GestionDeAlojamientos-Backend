package com.gestion.alojamientos.dto.accommodation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 *DTO para eliminar logicamente a un alojamiento del sistema
 * Este objeto es utilizado para validar la identidad del solicitante
 */
public record DeleteAccommodationDTO(

        /**
         * Identificador único del alojamiento.
         * Campo obligatorio.
         */
        @NotNull Long idAccommodation,
        /**
         * Contraseña del usuario.
         * Campo obligatorio para validar la eliminación.
         */
        @NotBlank String password,
        /**
         * Identificador único del usuario.
         * Campo obligatorio.
         */
        @NotNull Long idHost
) {
}
