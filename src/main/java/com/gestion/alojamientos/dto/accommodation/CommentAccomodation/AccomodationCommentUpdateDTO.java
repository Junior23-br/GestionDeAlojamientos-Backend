package com.gestion.alojamientos.dto.accommodation.CommentAccomodation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AccomodationCommentUpdateDTO(

        /**
         * ID del comentario a editar.
         */
        @NotNull(message = "El ID del comentario es obligatorio")
        Long id,

        /**
         * Nuevo texto del comentario.
         */
        @NotBlank(message = "El texto del comentario no puede estar vacío")
        String text,

        /**
         * Indica si el comentario es visible o no.
         */
        @NotNull(message = "Debe especificarse si el comentario es visible")
        Boolean isVisible
) {
}
