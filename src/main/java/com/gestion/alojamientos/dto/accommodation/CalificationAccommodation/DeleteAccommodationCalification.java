package com.gestion.alojamientos.dto.accommodation.CalificationAccommodation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 *DTO para eliminar logicamente a un calificacion del sistema
 * Este objeto es utilizado para validar la identidad del solicitante
 */
public record DeleteAccommodationCalification(

        /**
         *Identificador unico de la calificacion
         * No puede ser nula, vacio, o en blanco
         * Campo obligatorio
         */

        @NotNull @NotEmpty @NotBlank
        Long idCalification,


        /**
         * Identificador unico del alojamiento al que esta ligado la calificacion
         */
        @NotNull @NotEmpty @NotBlank
        Long idAccommodation



) {
}
