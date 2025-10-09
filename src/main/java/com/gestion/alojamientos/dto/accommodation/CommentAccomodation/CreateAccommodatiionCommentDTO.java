package com.gestion.alojamientos.dto.accommodation.CommentAccomodation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateAccommodatiionCommentDTO(
        /**
         * Texto del comentario. No puede estar vacío.
         */
        @NotBlank(message = "El texto del comentario no puede estar vacío")
        String text,

        /**
         * ID del autor (Guest) que crea el comentario.
         */
        @NotNull(message = "El autor es obligatorio")
        Long authorId,

        /**
         * ID del alojamiento al que pertenece el comentario.
         */
        @NotNull(message = "El alojamiento es obligatorio")
        Long accomodationId

) {
}
